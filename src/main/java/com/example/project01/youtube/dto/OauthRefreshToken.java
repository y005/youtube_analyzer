package com.example.project01.youtube.dto;

import com.example.project01.youtube.model.RefreshToken;
import lombok.Data;

@Data
public class OauthRefreshToken {
    private String refresh_token;
    private String access_token;
    private Long expires_in;
    private String token_type;

    public RefreshToken toRefreshToken(String userId) {
        return RefreshToken.builder()
                .user_id(userId)
                .refresh_token(this.refresh_token)
                .build();
    }
}
