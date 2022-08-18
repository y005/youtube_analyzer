package com.example.project01.youtube.repository;

import com.example.project01.youtube.entity.YoutubeContent;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface YoutubeContentRepository {
    void save(@Param("param") YoutubeContent youtubeContent);
}
