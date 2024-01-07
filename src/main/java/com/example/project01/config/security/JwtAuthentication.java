package com.example.project01.config.security;

import io.swagger.annotations.ApiParam;
import lombok.Getter;

//인증된 유저 엔티티(JwtAuthenticationToken의 Principal)
@Getter
public class JwtAuthentication {
    public final String token;

    @ApiParam(hidden = true)
    public final String userId;

    JwtAuthentication(String token, String userId) {
        assert token.isBlank();
        assert userId.isBlank();
        this.token = token;
        this.userId = userId;
    }
}
