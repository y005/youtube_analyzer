package com.example.project01.youtube.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshToken {
    private Long id;
    private String user_id;
    private String refresh_token;
}
