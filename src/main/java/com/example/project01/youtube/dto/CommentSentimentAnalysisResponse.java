package com.example.project01.youtube.dto;

import lombok.Data;

@Data
public class CommentSentimentAnalysisResponse {
    private String keywords;
    private float percent;

    public boolean valid() {
        return (keywords != null);
    }
}
