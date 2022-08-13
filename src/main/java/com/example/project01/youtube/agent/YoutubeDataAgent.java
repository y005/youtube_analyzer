package com.example.project01.youtube.agent;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
    private final YouTube youtube = new YouTube.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), request -> {}).build();
    public final long MAX_RESULTS = 1L;
    private String ACCESS_TOKEN;

    public void analysis(String access_token) throws IOException {
        ACCESS_TOKEN = access_token;
        List<Subscription> subscriptionList = getSubscribeInfo();
        List<String> channelIdList = subscriptionList.stream().map(e->e.getSnippet().getResourceId().getChannelId()).collect(Collectors.toList());
        List<String> videoIdList = getRecentVideoId(channelIdList);
        getVideoInfo(videoIdList);
    }

    public List<Subscription> getSubscribeInfo() throws IOException {
        YouTube.Subscriptions.List request = makeSubscriptionRequest();
        SubscriptionListResponse response = request.execute();
        return response.getItems();
    }

    private List<String> getRecentVideoId(List<String> channelIdList) throws IOException {
        List<String> recentVideoIdList = new ArrayList<>();
        channelIdList.forEach(
                (channelId) -> {
                    try {
                       SearchListResponse response = makeRecentYoutubeContentRequest(channelId).execute();
                       response.getItems().forEach(e->recentVideoIdList.add(e.getId().getVideoId()));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        );
        return recentVideoIdList;
    }

    private List<Video> getVideoInfo(List<String> videoIdList) {
        return videoIdList.stream().map(
                (videoId)->{
                    try {
                        VideoListResponse response = makeVideoInfoRequest(videoId).execute();
                        return response.getItems().get(0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).collect(Collectors.toList());
    }

    private YouTube.Subscriptions.List makeSubscriptionRequest() throws IOException {
        YouTube.Subscriptions.List request = youtube.subscriptions().list(Collections.singletonList("snippet"));
        request.setAccessToken(ACCESS_TOKEN);
        request.setMaxResults(MAX_RESULTS);
        request.setOrder("unread");
        request.setMine(true);
        return request;
    }

    private YouTube.Search.List makeRecentYoutubeContentRequest(String channelId) throws IOException {
        YouTube.Search.List request = youtube.search().list(Collections.singletonList("id"));
        request.setAccessToken(ACCESS_TOKEN);
        request.setChannelId(channelId);
        request.setOrder("date");
        request.setType(Collections.singletonList("video"));
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        request.setPublishedAfter(yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"Z");
        return request;
    }

    private YouTube.Videos.List makeVideoInfoRequest(String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(List.of("snippet", "statistics"));
        request.setId(Collections.singletonList(videoId));
        request.setMaxResults(1L);
        return request;
    }

    private YouTube.CommentThreads.List makeVideoCommentRequest(String videoId) throws IOException {
        // TODO 실제 데이터 연결해서 DB에 저장하는 거 완성하기
        YouTube.CommentThreads.List request = youtube.commentThreads().list(Collections.singletonList("replies"));
        request.setVideoId(videoId);
        request.setOrder("relevance");
        request.setMaxResults(10L);
        return request;
    }
}
