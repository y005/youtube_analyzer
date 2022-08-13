package com.example.project01.youtube.entity;

import com.google.api.client.util.DateTime;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class YoutubeContent {
    private String title;
    private String channelName;
    private String channelId;
    private String id;
    private Long viewCount;
    private Long likeCount;
    private Long dislikeCount;
    private DateTime time;
    private List<String> comments;
}
