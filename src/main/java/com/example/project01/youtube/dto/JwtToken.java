package com.example.project01.youtube.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtToken {
    private String token;
    private String user_id;
}
