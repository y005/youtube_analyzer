package com.example.project01.config.batch;

import com.example.project01.youtube.agent.YoutubeCommentAnalyzerAgent;
import com.example.project01.youtube.agent.YoutubeDataAgent;
import com.example.project01.youtube.dto.CommentSentimentAnalysisResponse;
import com.example.project01.youtube.dto.OauthAccessToken;
import com.example.project01.youtube.entity.UserEntity;
import com.example.project01.youtube.model.YoutubeContent;
import com.example.project01.youtube.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class YoutubeContentProcessor implements ItemProcessor<UserEntity, List<YoutubeContent>> {
    private final YoutubeCommentAnalyzerAgent youtubeCommentAnalyzerAgent;
    private final YoutubeService youtubeService;
    private final YoutubeDataAgent youtubeDataAgent;

    @Override
    public List<YoutubeContent> process(UserEntity userEntity) throws Exception {
        OauthAccessToken access_token = youtubeService.getAccessToken(userEntity.getId());
        List<YoutubeContent> youtubeContentList = youtubeDataAgent.getYoutubeContent(access_token.getAccess_token());
        return youtubeContentList.stream()
                .peek(this::commentAnalyze)
                .collect(Collectors.toList());
    }

    private void commentAnalyze(YoutubeContent youtubeContent) {
        if (youtubeContent.getComments() != null) {
            CommentSentimentAnalysisResponse commentSentimentAnalysisResponse = youtubeCommentAnalyzerAgent.getCommentAnalysis(youtubeContent.getComments());
            if (commentSentimentAnalysisResponse.valid()) {
                youtubeContent.setPercent((double) commentSentimentAnalysisResponse.getPercent());
                youtubeContent.setKeywords(commentSentimentAnalysisResponse.getKeywords());
            }
        }
    }
}
