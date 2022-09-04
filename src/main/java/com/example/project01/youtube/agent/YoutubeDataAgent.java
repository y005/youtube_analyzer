package com.example.project01.youtube.agent;

import com.example.project01.youtube.entity.YoutubeContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
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
    public final long MAX_COMMENT_COUNT = 100L;
    private final YouTube youtube;

    public String getUserId(String access_token) throws IOException {
        YouTube.Channels.List request = makeUserRequest(access_token);
        try {
            ChannelListResponse response = request.execute();
            return response.getItems().get(0).getSnippet().getTitle();
        } catch (Exception e) {
            throw new RuntimeException("에러 발생");
        }
    }

    public List<YoutubeContent> getYoutubeContent(String access_token) throws IOException {
        List<Subscription> subscriptionList = getSubscribeInfo(access_token);
        List<String> channelIdList = subscriptionList.stream().map(e->e.getSnippet().getResourceId().getChannelId()).collect(Collectors.toList());
        Map<String, BigInteger> subscriptionCountInfo = getSubscriptionCountRequest(access_token, channelIdList);
        List<String> videoIdList = getRecentVideoId(access_token, channelIdList);
        return getVideoInfo(access_token, videoIdList, subscriptionCountInfo);
    }

    public List<Subscription> getSubscribeInfo(String access_token) throws IOException {
        YouTube.Subscriptions.List request = makeSubscriptionRequest(access_token);
        SubscriptionListResponse response = request.execute();
        return response.getItems();
    }

    private Map<String, BigInteger> getSubscriptionCountRequest(String access_token, List<String> channelIdList) throws IOException {
        YouTube.Channels.List request = makeSubscriptionCountRequest(access_token, channelIdList);
        try {
            ChannelListResponse response = request.execute();
            return response.getItems().stream().collect(Collectors.toMap(Channel::getId, (e)->e.getStatistics().getSubscriberCount()));
        } catch (IOException e) {
            return Map.of();
        }
    }

    private List<String> getRecentVideoId(String access_token, List<String> channelIdList) {
        List<String> recentVideoIdList = new ArrayList<>();
        channelIdList.forEach(
                (channelId) -> {
                    try {
                       SearchListResponse response = makeRecentYoutubeContentRequest(access_token, channelId).execute();
                       response.getItems().forEach(e->recentVideoIdList.add(e.getId().getVideoId()));
                    } catch (Exception e) {
                        throw new RuntimeException("채널 영상 정보 탐색에서 에러 발생");
                    }
                }
        );
        return recentVideoIdList;
    }

    private List<YoutubeContent> getVideoInfo(String access_token, List<String> videoIdList, Map<String, BigInteger> subscriptionCountInfo) {
        return videoIdList.stream().map(
                (videoId)->{
                    try {
                        return makeYoutubeContent(access_token, videoId, subscriptionCountInfo);
                    } catch (Exception e) {
                        throw new RuntimeException("유튜브 정보 분석에서 에러 발생");
                    }
                }
        ).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private YouTube.Channels.List makeUserRequest(String access_token) throws IOException {
        YouTube.Channels.List request = youtube.channels().list(Collections.singletonList(SNIPPET));
        request.setAccessToken(access_token);
        request.setMine(true);
        return request;
    }

    private YoutubeContent makeYoutubeContent(String access_token, String videoId, Map<String, BigInteger> subscriptionCountInfo) throws IOException {
        VideoListResponse response1 = makeVideoInfoRequest(access_token, videoId).execute();
        VideoSnippet snippet = response1.getItems().get(0).getSnippet();
        VideoStatistics statistics = response1.getItems().get(0).getStatistics();
        CommentThreadListResponse response2 = makeVideoCommentRequest(access_token, videoId).execute();
        List<String> commentList = response2.getItems().stream().map(
                e-> e.getSnippet().getTopLevelComment().getSnippet().getTextOriginal()
        ).collect(Collectors.toList());
        String comments = String.join(",", commentList);
        return YoutubeContent.builder()
                .title(snippet.getTitle())
                .video_id(videoId)
                .channel_id(snippet.getChannelId())
                .subscribe_count(subscriptionCountInfo.get(snippet.getChannelId()))
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

    private YouTube.Channels.List makeSubscriptionCountRequest(String access_token, List<String> channelIdList) throws IOException {
        YouTube.Channels.List request = youtube.channels().list(Collections.singletonList(STATISTICS));
        request.setAccessToken(access_token);
        request.setId(channelIdList);
        return request;
    }

    private YouTube.Search.List makeRecentYoutubeContentRequest(String access_token,String channelId) throws IOException {
        YouTube.Search.List request = youtube.search().list(Collections.singletonList(ID));
        request.setAccessToken(access_token);
        request.setChannelId(channelId);
        request.setOrder(DATE);
        request.setType(Collections.singletonList(VIDEO));
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        request.setPublishedAfter(yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"Z");
        return request;
    }

    private YouTube.Videos.List makeVideoInfoRequest(String access_token, String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(List.of(SNIPPET, STATISTICS));
        request.setAccessToken(access_token);
        request.setId(Collections.singletonList(videoId));
        request.setMaxResults(MAX_VIDEO_COUNT);
        return request;
    }

    private YouTube.CommentThreads.List makeVideoCommentRequest(String access_token, String videoId) throws IOException {
        YouTube.CommentThreads.List request = youtube.commentThreads().list(Collections.singletonList(SNIPPET));
        request.setAccessToken(access_token);
        request.setVideoId(videoId);
        request.setOrder(RELEVANCE);
        request.setMaxResults(MAX_COMMENT_COUNT);
        return request;
    }
}
