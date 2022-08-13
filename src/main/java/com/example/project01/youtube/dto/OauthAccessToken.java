package com.example.project01.youtube.dto;

import lombok.Data;

@Data
public class OauthAccessToken {
    private String access_token;
    private Long expires_in;
    private String token_type;
}
