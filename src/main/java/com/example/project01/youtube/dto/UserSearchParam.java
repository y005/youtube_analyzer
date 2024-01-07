package com.example.project01.youtube.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSearchParam {
    String userId;
    String userPassword;

    public static UserSearchParam of(String userId, String userPassword) {
        return UserSearchParam.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();
    }
}
