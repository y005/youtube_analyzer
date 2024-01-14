package com.example.project01.youtube.entity;

import com.example.project01.youtube.model.RefreshToken;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "refresh_token")
@Getter
public class RefreshTokenEntity {
    @Id
    private String userId;

    @Column
    private String token;


    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(RefreshToken refreshToken) {
        this.userId = refreshToken.getUserId();
        this.token = refreshToken.getRefreshToken();
    }
}
