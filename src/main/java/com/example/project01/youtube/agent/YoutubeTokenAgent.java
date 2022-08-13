package com.example.project01.youtube.agent;

import com.example.project01.youtube.YoutubeConfig;
import com.example.project01.youtube.dto.OauthAccessToken;
import com.example.project01.youtube.dto.OauthRefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class YoutubeTokenAgent {
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String REFRESH_TOKEN = "refresh_token";
    private final RestTemplate restTemplate;
    private final YoutubeConfig youtubeConfig;
    public String makeOauthServerUrl() {
        return UriComponentsBuilder
                .fromUriString(youtubeConfig.getOauthServer())
                .path("/auth")
                .queryParam("client_id", youtubeConfig.getClientId())
                .queryParam("redirect_uri", youtubeConfig.getRedirectUrl())
                .queryParam("response_type", "code")
                .queryParam("scope", makeScope())
                .queryParam("access_type", "offline")
                .encode(StandardCharsets.UTF_8)
                .build().toUri().toString();
    }

    private String makeScope() {
        return "https://www.googleapis.com/auth/youtube.readonly " +
                "https://www.googleapis.com/auth/youtube " +
                "https://www.googleapis.com/auth/youtube.force-ssl " +
                "https://www.googleapis.com/auth/youtubepartner";
    }

    public OauthRefreshToken getRefreshToken(String code) {
        return requestRefreshToken(makeTokenUrl(), code);
    }

    public OauthAccessToken getAccessToken(String refreshToken) {
        return requestAccessToken(makeTokenUrl(), refreshToken);
    }

    public String makeTokenUrl() {
        return UriComponentsBuilder
                .fromUriString(youtubeConfig.getOauthServer())
                .path("/token")
                .encode(StandardCharsets.UTF_8)
                .build().toUri().toString();
    }

    private OauthRefreshToken requestRefreshToken(String url, String code) {
        MultiValueMap<String, String> params = makeDefaultParma();
        params.add("grant_type", AUTHORIZATION_CODE);
        params.add("redirect_uri", youtubeConfig.getRedirectUrl());
        params.add("code", code);
        return restTemplate.postForObject(url, params, OauthRefreshToken.class);
    }

    private OauthAccessToken requestAccessToken(String url, String refreshToken) {
        MultiValueMap<String, String> params = makeDefaultParma();
        params.add("grant_type", REFRESH_TOKEN);
        params.add("refresh_token", refreshToken);
        return restTemplate.postForObject(url, params, OauthAccessToken.class);
    }

    private MultiValueMap<String, String> makeDefaultParma() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", youtubeConfig.getClientId());
        params.add("client_secret", youtubeConfig.getClientPwd());
        return params;
    }
}

