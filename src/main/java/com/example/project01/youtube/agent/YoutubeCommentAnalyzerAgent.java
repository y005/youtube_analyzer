package com.example.project01.youtube.agent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class YoutubeCommentAnalyzerAgent {
    private final RestTemplate restTemplate;
}
