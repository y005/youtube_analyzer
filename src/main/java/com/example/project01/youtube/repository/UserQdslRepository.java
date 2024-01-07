package com.example.project01.youtube.repository;

import com.example.project01.youtube.dto.UserSearchParam;
import com.example.project01.youtube.entity.UserEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserQdslRepository {
    List<UserEntity> findByParam(UserSearchParam searchParam, Pageable pageable);

    void save(UserEntity userEntity);
}
