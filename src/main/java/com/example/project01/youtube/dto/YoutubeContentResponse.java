package com.example.project01.youtube.dto;

import com.example.project01.youtube.entity.YoutubeContent;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Builder
@Data
public class YoutubeContentResponse implements Serializable {
    private String title;
    private String channel_name;
    private String channel_id;
    private String video_id;
    private BigInteger subscribe_count;
    private BigInteger view_count;
    private BigInteger like_Count;
    private BigInteger dislike_count;
    private Date published_time;
    private Double percent;
    private String keywords;
    private String user_id;

    public static YoutubeContentResponse from(YoutubeContent youtubeContent) {
        return YoutubeContentResponse.builder()
                .title(youtubeContent.getTitle())
                .channel_name(youtubeContent.getChannel_name())
                .channel_id(youtubeContent.getChannel_id())
                .subscribe_count(youtubeContent.getSubscribe_count())
                .video_id(youtubeContent.getVideo_id())
                .view_count(youtubeContent.getView_count())
                .like_Count(youtubeContent.getLike_Count())
                .dislike_count(youtubeContent.getDislike_count())
                .published_time(youtubeContent.getPublished_time())
                .percent(youtubeContent.getPercent())
                .keywords(youtubeContent.getKeywords())
                .user_id(youtubeContent.getUser_id())
                .build();
    }
}
