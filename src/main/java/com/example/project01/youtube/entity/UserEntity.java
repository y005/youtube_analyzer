package com.example.project01.youtube.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
public class UserEntity {
    @Id
    private String id;
    @Column
    private String password;
    @Column
    private String roles;

    @OneToMany
    private List<SubscribeEntity> subscribeEntityList;
}
