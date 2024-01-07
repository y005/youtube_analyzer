package com.example.project01.youtube.mapper;

import com.example.project01.youtube.model.RefreshToken;
import org.apache.ibatis.annotations.Param;

public interface YoutubeTokenMapper {
    RefreshToken findByUserId(@Param("user_id") String userId);
    void save(@Param("param") RefreshToken refreshToken);
}
