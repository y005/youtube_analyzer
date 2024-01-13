package com.example.project01.youtube.entity;

import com.example.project01.youtube.enums.RoleType;
import com.example.project01.youtube.model.User;
import lombok.Getter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @Transient
    private List<RoleType> roleList;
    @OneToMany
    private List<SubscribeEntity> subscribeEntityList;

    public UserEntity() {
    }

    public UserEntity(User user) {
        this.id = user.getId();
        this.password = user.getPassword();
        this.roleList = user.getRoles();
    }

    @PostLoad
    public void loadRoleList() {
        this.roleList = Arrays.stream(this.roles.split(","))
                .map(RoleType::valueOf)
                .collect(Collectors.toList());
    }

    @PreUpdate
    @PrePersist
    public void writeRoleList() {
        this.roles = this.roleList.stream()
                .map(RoleType::name)
                .collect(Collectors.joining(","));
    }
}
