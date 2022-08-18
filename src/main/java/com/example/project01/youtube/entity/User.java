package com.example.project01.youtube.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String user_id;
    private String user_password;
    private String user_roles;
}
