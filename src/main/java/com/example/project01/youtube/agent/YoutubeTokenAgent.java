package com.example.project01.youtube.agent;

import com.example.project01.config.api.YoutubeConfig;
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
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CODE = "code";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String SCOPE = "scope";
    public static final String ACCESS_TYPE = "access_type";
    public static final String RESPONSE_TYPE = "response_type";
    private final RestTemplate restTemplate;
    private final YoutubeConfig youtubeConfig;
    public String makeOauthServerUrl() {
        return UriComponentsBuilder
                .fromUriString(youtubeConfig.getOauthServer())
                .path("/auth")
                .queryParam(CLIENT_ID, youtubeConfig.getClientId())
                .queryParam(REDIRECT_URI, youtubeConfig.getRedirectUrl())
                .queryParam(RESPONSE_TYPE, "code")
                .queryParam(SCOPE, makeScope())
                .queryParam(ACCESS_TYPE, "offline")
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
        params.add(GRANT_TYPE, AUTHORIZATION_CODE);
        params.add(REDIRECT_URI, youtubeConfig.getRedirectUrl());
        params.add(CODE, code);
        return restTemplate.postForObject(url, params, OauthRefreshToken.class);
    }

    private OauthAccessToken requestAccessToken(String url, String refreshToken) {
        MultiValueMap<String, String> params = makeDefaultParma();
        params.add(GRANT_TYPE, REFRESH_TOKEN);
        params.add(REFRESH_TOKEN, refreshToken);
        return restTemplate.postForObject(url, params, OauthAccessToken.class);
    }

    private MultiValueMap<String, String> makeDefaultParma() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(CLIENT_ID, youtubeConfig.getClientId());
        params.add(CLIENT_SECRET, youtubeConfig.getClientPwd());
        return params;
    }
}

