package com.example.project01.security;

import lombok.Getter;

//인증된 유저 엔티티(JwtAuthenticationToken의 Principal)
@Getter
public class JwtAuthentication {
    public final String token;

    public final String userId;

    JwtAuthentication(String token, String userId) {
        assert token.isBlank();
        assert userId.isBlank();
        this.token = token;
        this.userId = userId;
    }
}
