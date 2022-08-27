package com.example.project01.youtube.repository;

import com.example.project01.youtube.entity.YoutubeContent;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YoutubeContentRepository {
    void save(@Param("param") YoutubeContent youtubeContent);
    void update(@Param("param") YoutubeContent youtubeContent);
    List<YoutubeContent> getAll();
    List<YoutubeContent> findByUserId(@Param("userId") String userId, @Param("offset") int offset, @Param("limit") int limit);
}
