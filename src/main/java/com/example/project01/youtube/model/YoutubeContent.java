package com.example.project01.youtube.model;

import com.example.project01.youtube.entity.YoutubeContentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;
import java.util.stream.Collectors;

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
    private BigInteger like_count;
    private BigInteger dislike_count;
    private Date published_time;
    private String comments;
    private Double percent;
    private String keywords;

    public static YoutubeContent of(YoutubeContentEntity entity) {
        return YoutubeContent.builder()
                .title(entity.getTitle())
                .channel_name(entity.getChannel().getChannelName())
                .channel_id(entity.getChannel().getId())
                .video_id(entity.getId())
                .subscribe_count(entity.getChannel().getSubscribeCount())
                .view_count(entity.getViewCount())
                .like_count(entity.getLikeCount())
                .dislike_count(entity.getDislikeCount())
                .published_time(entity.getPublishedTime())
                .comments(entity.getComments())
                .percent(entity.getPercent())
                .keywords(entity.getRelatedKeywordEntityList()
                        .stream()
                        .map((relatedKeywordEntity) -> relatedKeywordEntity.getKeyword().getKeyword())
                        .collect(Collectors.joining(",")))
                .build();
    }
}
