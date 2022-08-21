package com.example.project01.youtube.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpForm {
    private String userId;
    private String userPassword;
}
