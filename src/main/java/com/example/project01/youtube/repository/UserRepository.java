package com.example.project01.youtube.repository;

import com.example.project01.youtube.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User findByUserId(@Param("user_id") String userId);
    void save(@Param("param") User user);
    User findByUserIdWithPassword(@Param("user_id") String userId, @Param("user_password") String userPassword);
}
