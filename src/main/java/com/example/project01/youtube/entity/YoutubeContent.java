package com.example.project01.youtube.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeContent {
    private String title;
    private String channel_name;
    private String channel_id;
    private String video_id;
    private BigInteger subscribe_count;
    private BigInteger view_count;
    private BigInteger like_Count;
    private BigInteger dislike_count;
    private Date published_time;
    private String comments;
    private Double percent;
    private String keywords;
    private String user_id;
}
