package com.example.project01.youtube.controller;

import com.example.project01.security.JwtAuthentication;
import com.example.project01.security.JwtAuthenticationProvider;
import com.example.project01.security.JwtAuthenticationToken;
import com.example.project01.youtube.agent.YoutubeTokenAgent;
import com.example.project01.youtube.dto.*;
import com.example.project01.youtube.entity.User;
import com.example.project01.youtube.entity.YoutubeContent;
import com.example.project01.youtube.service.UserService;
import com.example.project01.youtube.service.YoutubeService;
import com.google.api.services.youtube.model.Subscription;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
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
@Api
public class YoutubeApiController {
    private final YoutubeTokenAgent youtubeTokenAgent;
    private final YoutubeService youtubeService;
    private final UserService userService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @ExceptionHandler({Exception.class})
    public ResponseV1 handle(Exception e) {
        return ResponseV1.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @PostMapping("/signup")
    public ResponseV1 signup(@RequestBody SignUpForm signUpForm) {
        User user = userService.findByUsername(signUpForm.getUserId());
        if (user == null) {
            userService.save(makeUser(signUpForm.getUserId(), signUpForm.getUserPassword()));
            log.info("signup:user:{} create successfully", signUpForm.getUserId());
            return ResponseV1.ok("계정 생성 성공");
        }
        else {
            log.warn("signup:user:{} already exist", signUpForm.getUserId());
            return ResponseV1.error(HttpStatus.BAD_REQUEST,"이미 존재하는 아이디");
        }
    }

    private User makeUser(String userId, String userPassword) {
        return User.builder()
                .user_id(userId)
                .user_password(userPassword)
                .user_roles("ROLE_USER")
                .build();
    }

    @GetMapping("/oauth")
    public void oauth(HttpServletResponse response) throws IOException {
        response.sendRedirect(youtubeTokenAgent.makeOauthServerUrl());
    }

    @GetMapping("/redirect")
    public ResponseV1 redirect(@RequestParam("code") String code) {
        String userId = "sm";
        OauthRefreshToken oauthRefreshToken;
        try {
            oauthRefreshToken = youtubeTokenAgent.getRefreshToken(code);
            youtubeService.saveToken(oauthRefreshToken.toRefreshToken(userId));
            userService.save(User.builder().user_id(userId).build());
        } catch (Exception e) {
            log.warn("redirect:fail {}", e.getMessage());
            return ResponseV1.error(HttpStatus.BAD_REQUEST, "에러 발생");
        }
        return ResponseV1.ok(oauthRefreshToken);
    }

    @GetMapping("/login")
    public ResponseV1 login(@RequestBody LoginForm loginForm) {
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(loginForm.getUserId(), loginForm.getUserPassword());
        JwtAuthenticationToken authenticatedToken = (JwtAuthenticationToken) jwtAuthenticationProvider.authenticate(jwtAuthenticationToken);
        JwtAuthentication authentication = (JwtAuthentication) authenticatedToken.getPrincipal();
        log.info("user:login:{} login successfully", authentication.getUserId());
        JwtToken jwtToken = JwtToken.builder()
                .token(authentication.getToken())
                .user_id(authentication.getUserId())
                .build();
        return ResponseV1.ok(jwtToken);
    }

    @GetMapping("/content")
//    @Cacheable(key = "#id", cacheNames = "content")
    public ResponseV1 findYoutubeContent(@AuthenticationPrincipal JwtAuthentication jwtAuthentication, PagingCondition pagingCondition) {
        log.info("youtube:content:{}", jwtAuthentication.getUserId());
        List<YoutubeContent> youtubeContents;
        PagingResponse<YoutubeContent> result = new PagingResponse<>();
        try {
            pagingCondition.init();
            youtubeContents = youtubeService.findYoutubeContent(jwtAuthentication.getUserId(), pagingCondition);
        } catch (Exception e) {
            log.warn("youtube:content:{} {}", jwtAuthentication.getUserId(), e.getMessage());
            return ResponseV1.error(HttpStatus.FORBIDDEN, "허용되지 않은 접근");
        }
        return ResponseV1.ok(result.from(pagingCondition, youtubeContents));
    }

    @GetMapping("/crawling")
    public ResponseV1 crawlingAllYoutubeContent() {
        crawlingYoutubeContent();
        return ResponseV1.ok("crawling finished");
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void crawlingYoutubeContent() {
        int offset = 0;
        List<User> users;
        int MAX_USER = 300;
        log.info("Youtube Content Crawling start :)");
        do {
            users = userService.getEveryUser(offset, MAX_USER);
            users.forEach(
                    (user) -> {
                        try {
                            youtubeService.getYoutubeContent(user.getUser_id());
                            log.info("youtube:crawl:success {}", user.getUser_id());
                        } catch (Exception e) {
                            log.warn("youtube:crawl:fail {} {}", user.getUser_id(), e.getMessage());
                        }
                    }
            );
            offset += users.size();
        } while (users.size() == MAX_USER);
    }

    @GetMapping("/test/content")
    public ResponseV1 getYoutubeContent(@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
        log.info("youtube:content:{}", jwtAuthentication.getUserId());
        List<YoutubeContent> youtubeContents;
        try {
            youtubeContents = youtubeService.getYoutubeContent(jwtAuthentication.getUserId());
        } catch (Exception e) {
            log.warn("youtube:content:{} {}", jwtAuthentication.getUserId(), e.getMessage());
            return ResponseV1.error(HttpStatus.FORBIDDEN, "허용되지 않은 접근");
        }
        return ResponseV1.ok(youtubeContents);
    }

    @GetMapping("/test/subscribe")
    public ResponseV1 getSubscribeInfo(@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
        log.info("youtube:content:{}", jwtAuthentication.getUserId());
        List<Subscription> subscriptions;
        try {
            subscriptions = youtubeService.getSubscribeInfo(jwtAuthentication.getUserId());
        } catch (Exception e) {
            log.warn("youtube:subscribe:{} {}", jwtAuthentication.getUserId(), e.getMessage());
            return ResponseV1.error(HttpStatus.TOO_MANY_REQUESTS, "제한된 API 사용량 초과");
        }
        return ResponseV1.ok(subscriptions);
    }

    @GetMapping("/test/sentiment")
    public void sentiment() {
        youtubeService.sentiment();
    }
}
