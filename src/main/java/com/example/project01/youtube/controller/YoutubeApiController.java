package com.example.project01.youtube.controller;

import com.example.project01.youtube.agent.YoutubeTokenAgent;
import com.example.project01.youtube.dto.OauthAccessToken;
import com.example.project01.youtube.dto.OauthRefreshToken;
import com.example.project01.youtube.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube")
public class YoutubeApiController {
    private final YoutubeTokenAgent youtubeTokenAgent;
    private final YoutubeService youtubeService;

    @ExceptionHandler({Exception.class})
    public String handle() {
        return "error :(";
    }

    @GetMapping("/login")
    public void login() {
        //로그인을 통한 jwt 토큰 발행
    }

    @GetMapping("/analysis")
    public void analysis() {
        //1. JWT 토큰을 통한 인증
        //2. 계정이 구독한 유튜브 채널 동영상의 분석 정보 리스트를 반환한다.
    }

    @GetMapping("/subscription/{id}")
    public void getSubscription(@PathVariable String id) throws IOException {
        youtubeService.analysis(id);
    }

    @GetMapping("/oauth")
    public void oauth(HttpServletResponse response) throws IOException {
        response.sendRedirect(youtubeTokenAgent.makeOauthServerUrl());
    }

    @GetMapping("/redirect")
    public OauthRefreshToken redirect(@RequestParam("code") String code) {
        String userId = "samho101";
        OauthRefreshToken oauthRefreshToken = youtubeTokenAgent.getRefreshToken(code);
        youtubeService.saveToken(oauthRefreshToken.toRefreshToken(userId));
        return oauthRefreshToken;
    }

    @GetMapping("/token/{id}")
    public OauthAccessToken token(@PathVariable String id) {
        return youtubeService.getAccessToken(id);
    }
}
