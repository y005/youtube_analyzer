package com.example.project01.youtube.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshToken {
    private String userId;
    private String refreshToken;
}
