package com.example.project01.youtube.repository;

import com.example.project01.youtube.dto.YoutubeContentSearchParam;
import com.example.project01.youtube.entity.YoutubeContentEntity;
import org.springframework.data.domain.Pageable;
import java.util.List;


public interface YoutubeContentQdslRepository {
    List<YoutubeContentEntity> findByParam(YoutubeContentSearchParam searchParam, Pageable pageable);
}