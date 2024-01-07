package com.example.project01.config.security;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Jwt {
    private final String ISSUER;
    private final String CLIENT_SECRET;
    private final int EXPIRY_SECONDS;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;

    public Jwt(String ISSUER, String CLIENT_SECRET, int EXPIRY_SECONDS) {
        this.ISSUER = ISSUER;
        this.CLIENT_SECRET = CLIENT_SECRET;
        this.EXPIRY_SECONDS = EXPIRY_SECONDS;
        this.algorithm = Algorithm.HMAC512(CLIENT_SECRET);
        this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm).withIssuer(ISSUER).build();
    }

    //클레임 정보를 토큰으로 서명하는 로직
    public String sign(Claims claims) {
        Date now = new Date();
        JWTCreator.Builder builder = com.auth0.jwt.JWT.create();
        builder.withIssuer(ISSUER);
        builder.withIssuedAt(now);
        if (EXPIRY_SECONDS > 0) {
            builder.withExpiresAt(new Date(now.getTime() + EXPIRY_SECONDS * 1000L));
        }
        var result = new Date(now.getTime() + EXPIRY_SECONDS * 1000L);
        builder.withClaim("userId", claims.userId);
        builder.withArrayClaim("roles", claims.roles);
        return builder.sign(algorithm);
    }

    //서명된 토큰 정보를 검증하는 로직
    public Claims verify(String token) throws JWTVerificationException {
        return new Claims(jwtVerifier.verify(token));
    }

    public static class Claims {
        String userId;
        String[] roles;
        Date iat;
        Date exp;

        private Claims() {}

        Claims(DecodedJWT decodedJWT) {
            Claim userId = decodedJWT.getClaim("userId");
            if (!userId.isNull()) {
                this.userId = userId.asString();
            }
            Claim roles = decodedJWT.getClaim("roles");
            if (!roles.isNull()) {
                this.roles = roles.asArray(String.class);
            }
            this.iat = decodedJWT.getIssuedAt();
            this.exp = decodedJWT.getExpiresAt();
        }

        private Claims(String userId, String[] roles) {
            this.userId = userId;
            this.roles = roles;
        }

        public static Claims from(String userId, String[] roles) {
            return new Claims(userId, roles);
        }

        public Map<String, Object> asMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("roles", roles);
            map.put("iat", iat());
            map.put("exp", exp());
            return map;
        }

        long iat() {
            return iat != null ? iat.getTime() : -1;
        }

        long exp() {
            return exp != null ? exp.getTime() : -1;
        }
    }
}
