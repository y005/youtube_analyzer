package com.example.project01.youtube.mapper;

import com.example.project01.youtube.model.YoutubeContent;
import org.apache.ibatis.annotations.Param;

public interface YoutubeContentMapper {
    void save(@Param("param") YoutubeContent youtubeContent);
    void update(@Param("param") YoutubeContent youtubeContent);
}
