package com.example.project01.youtube.agent;

import com.example.project01.youtube.YoutubeConfig;
import com.example.project01.youtube.entity.YoutubeContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class YoutubeDataAgent {
    public static final String SNIPPET = "snippet";
    public static final String STATISTICS = "statistics";
    public static final String RELEVANCE = "relevance";
    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String VIDEO = "video";
    public static final long MAX_CHANNEL_COUNT = 50L;
    public static final long MAX_VIDEO_COUNT = 5L;
    public static final String UNREAD = "unread";
    public final long MAX_COMMENT_COUNT = 10L;
    private final YouTube youtube;
    private final YoutubeConfig youtubeConfig;

    public List<YoutubeContent> getYoutubeContent(String access_token) throws IOException {
        List<Subscription> subscriptionList = getSubscribeInfo(access_token);
        List<String> channelIdList = subscriptionList.stream().map(e->e.getSnippet().getResourceId().getChannelId()).collect(Collectors.toList());
        List<String> videoIdList = getRecentVideoId(channelIdList);
        return getVideoInfo(videoIdList);
    }

    public List<Subscription> getSubscribeInfo(String access_token) throws IOException {
        YouTube.Subscriptions.List request = makeSubscriptionRequest(access_token);
        SubscriptionListResponse response = request.execute();
        return response.getItems();
    }

    private List<String> getRecentVideoId(List<String> channelIdList) {
        List<String> recentVideoIdList = new ArrayList<>();
        channelIdList.forEach(
                (channelId) -> {
                    try {
                       SearchListResponse response = makeRecentYoutubeContentRequest(channelId).execute();
                       response.getItems().forEach(e->recentVideoIdList.add(e.getId().getVideoId()));
                    } catch (Exception e) {
                        throw new RuntimeException("채널 영상 정보 탐색에서 에러 발생");
                    }
                }
        );
        return recentVideoIdList;
    }

    private List<YoutubeContent> getVideoInfo(List<String> videoIdList) {
        return videoIdList.stream().map(
                (videoId)->{
                    try {
                        return makeYoutubeContent(videoId);
                    } catch (Exception e) {
                        throw new RuntimeException("유튜브 정보 분석에서 에러 발생");
                    }
                }
        ).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private YoutubeContent makeYoutubeContent(String videoId) throws IOException {
        VideoListResponse response1 = makeVideoInfoRequest(videoId).execute();
        VideoSnippet snippet = response1.getItems().get(0).getSnippet();
        VideoStatistics statistics = response1.getItems().get(0).getStatistics();
        CommentThreadListResponse response2 = makeVideoCommentRequest(videoId).execute();
        List<String> commentList = response2.getItems().stream().map(
                e-> e.getSnippet().getTopLevelComment().getSnippet().getTextOriginal()
        ).collect(Collectors.toList());
        String comments = String.join(",", commentList);
        return YoutubeContent.builder()
                .title(snippet.getTitle())
                .video_id(videoId)
                .channel_id(snippet.getChannelId())
                .channel_name(snippet.getChannelTitle())
                .view_count(statistics.getViewCount())
                .like_Count(statistics.getLikeCount())
                .dislike_count(statistics.getDislikeCount())
                .comments(comments)
                .published_time(new Date(snippet.getPublishedAt().getValue()))
                .build();
    }

    private YouTube.Subscriptions.List makeSubscriptionRequest(String access_token) throws IOException {
        YouTube.Subscriptions.List request = youtube.subscriptions().list(Collections.singletonList(SNIPPET));
        request.setAccessToken(access_token);
        request.setMaxResults(MAX_CHANNEL_COUNT);
        request.setOrder(UNREAD);
        request.setMine(true);
        return request;
    }

    private YouTube.Search.List makeRecentYoutubeContentRequest(String channelId) throws IOException {
        YouTube.Search.List request = youtube.search().list(Collections.singletonList(ID));
        request.setKey(youtubeConfig.getApiKey());
        request.setChannelId(channelId);
        request.setOrder(DATE);
        request.setType(Collections.singletonList(VIDEO));
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        request.setPublishedAfter(yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"Z");
        return request;
    }

    private YouTube.Videos.List makeVideoInfoRequest(String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(List.of(SNIPPET, STATISTICS));
        request.setKey(youtubeConfig.getApiKey());
        request.setId(Collections.singletonList(videoId));
        request.setMaxResults(MAX_VIDEO_COUNT);
        return request;
    }

    private YouTube.CommentThreads.List makeVideoCommentRequest(String videoId) throws IOException {
        YouTube.CommentThreads.List request = youtube.commentThreads().list(Collections.singletonList(SNIPPET));
        request.setKey(youtubeConfig.getApiKey());
        request.setVideoId(videoId);
        request.setOrder(RELEVANCE);
        request.setMaxResults(MAX_COMMENT_COUNT);
        return request;
    }
}
