package com.example.project01.youtube.controller;

import com.example.project01.security.JwtAuthentication;
import com.example.project01.security.JwtAuthenticationProvider;
import com.example.project01.security.JwtAuthenticationToken;
import com.example.project01.youtube.agent.YoutubeTokenAgent;
import com.example.project01.youtube.dto.JwtToken;
import com.example.project01.youtube.dto.LoginForm;
import com.example.project01.youtube.dto.OauthRefreshToken;
import com.example.project01.youtube.dto.SignUpForm;
import com.example.project01.youtube.entity.User;
import com.example.project01.youtube.entity.YoutubeContent;
import com.example.project01.youtube.service.UserService;
import com.example.project01.youtube.service.YoutubeService;
import com.google.api.services.youtube.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube")
public class YoutubeApiController {
    private final YoutubeTokenAgent youtubeTokenAgent;
    private final YoutubeService youtubeService;
    private final UserService userService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final int MAX_USER = 300;

    @ExceptionHandler({Exception.class})
    public String handle(Exception e) {
        return e.getMessage();
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignUpForm signUpForm) {
        User user = userService.findByUsername(signUpForm.getUserId());
        if (user == null) {
            userService.save(makeUser(signUpForm.getUserId(), signUpForm.getUserPassword()));
            log.info("signup:user:{} create successfully", signUpForm.getUserId());
            return "아이디 생성 성공";
        }
        else {
            log.warn("signup:user:{} already exist", signUpForm.getUserId());
            return "이미 존재하는 아이디입니다.";
        }
    }

    private User makeUser(String userId, String userPassword) {
        return User.builder()
                .user_id(userId)
                .user_password(userPassword)
                .user_roles("ROLE_USER")
                .build();
    }

    @GetMapping("/login")
    public Object login(@RequestBody LoginForm loginForm) {
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(loginForm.getUserId(), loginForm.getUserPassword());
        JwtAuthenticationToken authenticatedToken = (JwtAuthenticationToken) jwtAuthenticationProvider.authenticate(jwtAuthenticationToken);
        JwtAuthentication authentication = (JwtAuthentication) authenticatedToken.getPrincipal();
        log.info("user:login:{} login successfully", authentication.getUserId());
        return JwtToken.builder()
                .token(authentication.getToken())
                .user_id(authentication.getUserId())
                .build();
    }

    @GetMapping("/content")
    public List<YoutubeContent> getYoutubeContent(@AuthenticationPrincipal JwtAuthentication jwtAuthentication) throws IOException {
        log.info("youtube:content:{}", jwtAuthentication.getUserId());
        List<YoutubeContent> youtubeContents = null;
        try {
            youtubeContents = youtubeService.getYoutubeContent(jwtAuthentication.getUserId());
        } catch (Exception e) {
            log.warn("youtube:content:{} {}", jwtAuthentication.getUserId(), e.getMessage());
        }
        return youtubeContents;
    }

    @GetMapping("/subscribe")
    public List<Subscription> getSubscribeInfo(@AuthenticationPrincipal JwtAuthentication jwtAuthentication) throws IOException {
        log.info("youtube:content:{}", jwtAuthentication.getUserId());
        List<Subscription> subscriptions = null;
        try {
            subscriptions = youtubeService.getSubscribeInfo(jwtAuthentication.getUserId());
        } catch (Exception e) {
            log.warn("youtube:subscribe:{} {}", jwtAuthentication.getUserId(), e.getMessage());
        }
        return subscriptions;
    }

    @GetMapping("/oauth")
    public void oauth(HttpServletResponse response) throws IOException {
        response.sendRedirect(youtubeTokenAgent.makeOauthServerUrl());
    }

    @GetMapping("/redirect")
    public Object redirect(@RequestParam("code") String code) {
        String userId = "sm";
        OauthRefreshToken oauthRefreshToken;
        try {
            oauthRefreshToken = youtubeTokenAgent.getRefreshToken(code);
            youtubeService.saveToken(oauthRefreshToken.toRefreshToken(userId));
            userService.save(User.builder().user_id(userId).build());
        } catch (Exception e) {
            log.warn("redirect:fail {}", e.getMessage());
            return "유저 인증 실패";
        }
        return oauthRefreshToken;
    }

    @Scheduled(cron = "0 0 17 * * *")
    public void crawlingYoutubeContent() {
        int offset = 0;
        List<User> users;
        do {
            users = userService.getEveryUser(offset, MAX_USER);
            users.forEach(
                    (user) -> {
                        try {
                            youtubeService.getYoutubeContent(user.getUser_id());
                            log.info("youtube:crawl:{} {}", user.getUser_id());
                        } catch (Exception e) {
                            log.warn("youtube:crawl:{} {}", user.getUser_id(), e.getMessage());
                        }
                    }
            );
            offset += users.size();
        } while (users.size() == MAX_USER);
    }
}
