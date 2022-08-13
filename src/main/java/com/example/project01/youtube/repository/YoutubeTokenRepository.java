package com.example.project01.youtube.repository;

import com.example.project01.youtube.entity.RefreshToken;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface YoutubeTokenRepository {
    RefreshToken findByUserId(@Param("user_id") String userId);
    void save(@Param("param") RefreshToken refreshToken);
}
