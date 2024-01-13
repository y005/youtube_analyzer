package com.example.project01.youtube.repository;

import com.example.project01.youtube.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
