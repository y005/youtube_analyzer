package com.example.project01.youtube.controller;

import com.example.project01.security.Jwt;
import com.example.project01.security.JwtAuthentication;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube")
public class YoutubeApiController {
    private final YoutubeTokenAgent youtubeTokenAgent;
    private final YoutubeService youtubeService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final Jwt jwt;

    @ExceptionHandler({Exception.class})
    public String handle(Exception e) {
        return e.getMessage();
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignUpForm signUpForm) {
        User user = userService.findByUsername(signUpForm.getUserId());
        if (user == null) {
            userService.save(makeUser(signUpForm.getUserId(), signUpForm.getUserPassword()));
            return "성공적으로 아이디가 생성되었습니다.";
        }
        else {
            return "이미 존재하는 아이디입니다.";
        }
    }

    private User makeUser(String userId, String userPassword) {
        return User.builder()
                .user_id(userId)
                .user_password(userPassword)
                .user_roles("USER")
                .build();
    }

    @GetMapping("/login")
    public Object login(@RequestBody LoginForm loginForm) {
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(loginForm.getUserId(), loginForm.getUserPassword());
        JwtAuthenticationToken authenticatedToken = (JwtAuthenticationToken) authenticationManager.authenticate(jwtAuthenticationToken);
        JwtAuthentication authentication = (JwtAuthentication) authenticatedToken.getPrincipal();
        return JwtToken.builder()
                .token(authentication.getToken())
                .user_id(authentication.getUserId())
                .build();
    }

    @GetMapping("/content")
    public List<YoutubeContent> getYoutubeContent(@RequestHeader String token) throws IOException {
        return youtubeService.getYoutubeContent(getUserId(token));
    }

    @GetMapping("/subscribe")
    public List<Subscription> getSubscribeInfo(@RequestHeader String token) throws IOException {
        return youtubeService.getSubscribeInfo(getUserId(token));
    }

    @GetMapping("/oauth")
    public void oauth(HttpServletResponse response) throws IOException {
        response.sendRedirect(youtubeTokenAgent.makeOauthServerUrl());
    }

    @GetMapping("/redirect")
    public OauthRefreshToken redirect(@RequestParam("code") String code) {
        String userId = "sm";
        OauthRefreshToken oauthRefreshToken = youtubeTokenAgent.getRefreshToken(code);
        youtubeService.saveToken(oauthRefreshToken.toRefreshToken(userId));
        userService.save(User.builder().user_id(userId).build());
        return oauthRefreshToken;
    }

    private String getUserId(String token) {
        Map<String, Object> claims= jwt.verify(token).asMap();
        return (String) claims.get("userId");
    }
}
