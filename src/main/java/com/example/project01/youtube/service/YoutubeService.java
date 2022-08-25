package com.example.project01.youtube.service;

import com.example.project01.youtube.agent.YoutubeCommentAnalyzerAgent;
import com.example.project01.youtube.agent.YoutubeDataAgent;
import com.example.project01.youtube.agent.YoutubeTokenAgent;
import com.example.project01.youtube.dto.CommentSentimentAnalysisResponse;
import com.example.project01.youtube.dto.OauthAccessToken;
import com.example.project01.youtube.entity.RefreshToken;
import com.example.project01.youtube.entity.YoutubeContent;
import com.example.project01.youtube.repository.YoutubeContentRepository;
import com.example.project01.youtube.repository.YoutubeTokenRepository;
import com.google.api.services.youtube.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeService {
    private final YoutubeTokenRepository youtubeTokenRepository;

    private final YoutubeContentRepository youtubeContentRepository;

    private final YoutubeTokenAgent youtubeTokenAgent;

    private final YoutubeDataAgent youtubeDataAgent;

    private final YoutubeCommentAnalyzerAgent youtubeCommentAnalyzerAgent;

    @Transactional
    public void saveToken(RefreshToken refreshToken) {
        youtubeTokenRepository.save(refreshToken);
    }

    public List<YoutubeContent> getYoutubeContent(String id) throws IOException {
        OauthAccessToken access_token = getAccessToken(id);
        List<YoutubeContent> youtubeContentList = youtubeDataAgent.getYoutubeContent(access_token.getAccess_token());
        for (YoutubeContent element : youtubeContentList) {
            try {
                element.setUser_id(id);
                if (element.getComments() != null) {
                    CommentSentimentAnalysisResponse commentSentimentAnalysisResponse = youtubeCommentAnalyzerAgent.getCommentAnalysis(element.getComments());
                    if (commentSentimentAnalysisResponse.valid()){
                        element.setPercent((double) commentSentimentAnalysisResponse.getPercent());
                        element.setKeywords(commentSentimentAnalysisResponse.getKeywords());
                    }
                }
                youtubeContentRepository.save(element);
            } catch (Exception e) {
                throw e;
            }
        }
        return youtubeContentList;
    }

    public List<Subscription> getSubscribeInfo(String id) throws IOException {
        return youtubeDataAgent.getSubscribeInfo(getAccessToken(id).getAccess_token());
    }

    @Transactional(readOnly = true)
    public RefreshToken getRefreshToken(String id) {
        return youtubeTokenRepository.findByUserId(id);
    }

    private OauthAccessToken getAccessToken(String id) {
        RefreshToken refreshToken = getRefreshToken(id);
        return youtubeTokenAgent.getAccessToken(refreshToken.getRefresh_token());
    }


    @Transactional
    public void test() {
        List<YoutubeContent> youtubeContentList = youtubeContentRepository.getAll();
        youtubeContentList.forEach((content)->{
            if (content.getPercent() == null) {
                CommentSentimentAnalysisResponse commentSentimentAnalysisResponse = youtubeCommentAnalyzerAgent.getCommentAnalysis(content.getComments());
                content.setKeywords(commentSentimentAnalysisResponse.getKeywords());
                content.setPercent((double) commentSentimentAnalysisResponse.getPercent());
                youtubeContentRepository.update(content);
            }
        });
    }
}
