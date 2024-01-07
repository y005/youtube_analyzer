package com.example.project01.youtube.model;

import com.example.project01.youtube.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {
    private String id;
    private String password;
    private String role;

    public static User of(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .build();
    }
}
