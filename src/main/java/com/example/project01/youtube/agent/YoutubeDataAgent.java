package com.example.project01.youtube.agent;

import com.example.project01.youtube.model.YoutubeContent;
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

    public String getUserId(String accessToken) throws IOException {
        YouTube.Channels.List request = makeUserRequest(accessToken);
        try {
            ChannelListResponse response = request.execute();
            return response.getItems().get(0).getSnippet().getTitle();
        } catch (Exception e) {
            throw new RuntimeException("에러 발생");
        }
    }

    public List<YoutubeContent> getYoutubeContent(String accessToken) throws IOException {
        List<Subscription> subscriptionList = getSubscribeInfo(accessToken);
        List<String> channelIdList = subscriptionList.stream().map(e->e.getSnippet().getResourceId().getChannelId()).collect(Collectors.toList());
        Map<String, BigInteger> subscriptionCountInfo = getSubscriptionCountRequest(accessToken, channelIdList);
        List<String> videoIdList = getRecentVideoId(accessToken, channelIdList);
        return getVideoInfo(accessToken, videoIdList, subscriptionCountInfo);
    }

    public List<Subscription> getSubscribeInfo(String accessToken) throws IOException {
        YouTube.Subscriptions.List request = makeSubscriptionRequest(accessToken);
        SubscriptionListResponse response = request.execute();
        return response.getItems();
    }

    private Map<String, BigInteger> getSubscriptionCountRequest(String accessToken, List<String> channelIdList) throws IOException {
        YouTube.Channels.List request = makeSubscriptionCountRequest(accessToken, channelIdList);
        try {
            ChannelListResponse response = request.execute();
            return response.getItems().stream().collect(Collectors.toMap(Channel::getId, (e)->e.getStatistics().getSubscriberCount()));
        } catch (IOException e) {
            return Map.of();
        }
    }

    private List<String> getRecentVideoId(String accessToken, List<String> channelIdList) {
        List<String> recentVideoIdList = new ArrayList<>();
        channelIdList.forEach(
                (channelId) -> {
                    try {
                       SearchListResponse response = makeRecentYoutubeContentRequest(accessToken, channelId).execute();
                       response.getItems().forEach(e->recentVideoIdList.add(e.getId().getVideoId()));
                    } catch (Exception e) {
                        throw new RuntimeException("채널 영상 정보 탐색에서 에러 발생");
                    }
                }
        );
        return recentVideoIdList;
    }

    private List<YoutubeContent> getVideoInfo(String accessToken, List<String> videoIdList, Map<String, BigInteger> subscriptionCountInfo) {
        return videoIdList.stream().map(
                (videoId)->{
                    try {
                        return makeYoutubeContent(accessToken, videoId, subscriptionCountInfo);
                    } catch (Exception e) {
                        throw new RuntimeException("유튜브 정보 분석에서 에러 발생");
                    }
                }
        ).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private YouTube.Channels.List makeUserRequest(String accessToken) throws IOException {
        YouTube.Channels.List request = youtube.channels().list(Collections.singletonList(SNIPPET));
        request.setAccessToken(accessToken);
        request.setMine(true);
        return request;
    }

    private YoutubeContent makeYoutubeContent(String accessToken, String videoId, Map<String, BigInteger> subscriptionCountInfo) throws IOException {
        VideoListResponse response1 = makeVideoInfoRequest(accessToken, videoId).execute();
        VideoSnippet snippet = response1.getItems().get(0).getSnippet();
        VideoStatistics statistics = response1.getItems().get(0).getStatistics();
        CommentThreadListResponse response2 = makeVideoCommentRequest(accessToken, videoId).execute();
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
                .like_count(statistics.getLikeCount())
                .dislike_count(statistics.getDislikeCount())
                .comments(comments)
                .published_time(new Date(snippet.getPublishedAt().getValue()))
                .build();
    }

    private YouTube.Subscriptions.List makeSubscriptionRequest(String accessToken) throws IOException {
        YouTube.Subscriptions.List request = youtube.subscriptions().list(Collections.singletonList(SNIPPET));
        request.setAccessToken(accessToken);
        request.setMaxResults(MAX_CHANNEL_COUNT);
        request.setOrder(UNREAD);
        request.setMine(true);
        return request;
    }

    private YouTube.Channels.List makeSubscriptionCountRequest(String accessToken, List<String> channelIdList) throws IOException {
        YouTube.Channels.List request = youtube.channels().list(Collections.singletonList(STATISTICS));
        request.setAccessToken(accessToken);
        request.setId(channelIdList);
        return request;
    }

    private YouTube.Search.List makeRecentYoutubeContentRequest(String accessToken,String channelId) throws IOException {
        YouTube.Search.List request = youtube.search().list(Collections.singletonList(ID));
        request.setAccessToken(accessToken);
        request.setChannelId(channelId);
        request.setOrder(DATE);
        request.setType(Collections.singletonList(VIDEO));
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        request.setPublishedAfter(yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"Z");
        return request;
    }

    private YouTube.Videos.List makeVideoInfoRequest(String accessToken, String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(List.of(SNIPPET, STATISTICS));
        request.setAccessToken(accessToken);
        request.setId(Collections.singletonList(videoId));
        request.setMaxResults(MAX_VIDEO_COUNT);
        return request;
    }

    private YouTube.CommentThreads.List makeVideoCommentRequest(String accessToken, String videoId) throws IOException {
        YouTube.CommentThreads.List request = youtube.commentThreads().list(Collections.singletonList(SNIPPET));
        request.setAccessToken(accessToken);
        request.setVideoId(videoId);
        request.setOrder(RELEVANCE);
        request.setMaxResults(MAX_COMMENT_COUNT);
        return request;
    }
}
