package com.example.project01.youtube.model;

import com.example.project01.youtube.entity.UserEntity;
import com.example.project01.youtube.enums.RoleType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class User {
    private String id;
    private String password;
    private List<RoleType> roles;

    public static User of(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .password(userEntity.getPassword())
                .roles(userEntity.getRoleList())
                .build();
    }
}
