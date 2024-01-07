package com.example.project01.youtube.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class YoutubeContentSearchParam {
    private String userId;
    private String keyword;

    public static YoutubeContentSearchParam of(String userId, String keyword) {
        return YoutubeContentSearchParam.builder()
                .userId(userId)
                .keyword(keyword)
                .build();
    }
}
