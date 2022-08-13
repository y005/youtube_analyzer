package com.example.project01.youtube.repository;

import com.example.project01.youtube.entity.RefreshToken;
import com.example.project01.youtube.entity.YoutubeContent;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface YoutubeVideoRepository {
    void save(@Param("param") YoutubeContent youtubeContent);
}
