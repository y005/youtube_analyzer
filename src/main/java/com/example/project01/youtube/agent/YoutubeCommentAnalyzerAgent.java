package com.example.project01.youtube.agent;

import com.example.project01.youtube.dto.CommentSentimentAnalysisRequest;
import com.example.project01.youtube.dto.CommentSentimentAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class YoutubeCommentAnalyzerAgent {
    public static final String FLASK_SERVER_URL = "http://localhost:6000/analysis";
    private final RestTemplate restTemplate;

    public CommentSentimentAnalysisResponse getCommentAnalysis(String comments) {
        URI uri = makeURI();
        return request(uri, comments);
    }

    private URI makeURI() {
        return UriComponentsBuilder
                .fromUriString(FLASK_SERVER_URL)
                .encode(StandardCharsets.UTF_8)
                .build().toUri();
    }

    private CommentSentimentAnalysisResponse request(URI uri , String comments) {
        HttpEntity<CommentSentimentAnalysisRequest> body = new HttpEntity(makeBody(comments));
        return restTemplate.postForObject(uri, body, CommentSentimentAnalysisResponse.class);
    }

    private CommentSentimentAnalysisRequest makeBody(String comments) {
        CommentSentimentAnalysisRequest commentSentimentAnalysisRequest = new CommentSentimentAnalysisRequest();
        commentSentimentAnalysisRequest.setComments(comments);
        return commentSentimentAnalysisRequest;
    }
}
