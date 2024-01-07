package com.example.project01.youtube.service;

import com.example.project01.youtube.agent.YoutubeCommentAnalyzerAgent;
import com.example.project01.youtube.agent.YoutubeDataAgent;
import com.example.project01.youtube.agent.YoutubeTokenAgent;
import com.example.project01.youtube.dto.CommentSentimentAnalysisResponse;
import com.example.project01.youtube.dto.OauthAccessToken;
import com.example.project01.youtube.dto.YoutubeContentSearchParam;
import com.example.project01.youtube.model.RefreshToken;
import com.example.project01.youtube.model.YoutubeContent;
import com.example.project01.youtube.mapper.YoutubeContentMapper;
import com.example.project01.youtube.mapper.YoutubeTokenMapper;
import com.example.project01.youtube.repository.YoutubeContentQdslRepository;
import com.google.api.services.youtube.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YoutubeService {
    private final YoutubeTokenMapper youtubeTokenMapper;
    private final YoutubeContentMapper youtubeContentMapper;
    private final YoutubeContentQdslRepository youtubeContentQdslRepository;
    private final YoutubeTokenAgent youtubeTokenAgent;
    private final YoutubeDataAgent youtubeDataAgent;
    private final YoutubeCommentAnalyzerAgent youtubeCommentAnalyzerAgent;

    @Transactional
    public void saveToken(RefreshToken refreshToken) {
        youtubeTokenMapper.save(refreshToken);
    }

    public List<YoutubeContent> findYoutubeContent(YoutubeContentSearchParam searchParam, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return youtubeContentQdslRepository.findByParam(searchParam, pageable)
                .stream()
                .map(YoutubeContent::of)
                .collect(Collectors.toList());
    }

    public String getUserYoutubeId(String access_token) throws IOException {
        return youtubeDataAgent.getUserId(access_token);
    }

    @Transactional
    public List<YoutubeContent> getYoutubeContent(String id) throws IOException {
        OauthAccessToken access_token = getAccessToken(id);
        List<YoutubeContent> youtubeContentList = youtubeDataAgent.getYoutubeContent(access_token.getAccess_token());
        for (YoutubeContent element : youtubeContentList) {
            try {
                if (element.getComments() != null) {
                    CommentSentimentAnalysisResponse commentSentimentAnalysisResponse = youtubeCommentAnalyzerAgent.getCommentAnalysis(element.getComments());
                    if (commentSentimentAnalysisResponse.valid()){
                        element.setPercent((double) commentSentimentAnalysisResponse.getPercent());
                        element.setKeywords(commentSentimentAnalysisResponse.getKeywords());
                    }
                }
                youtubeContentMapper.save(element);
            } catch (Exception e) {
                throw e;
            }
        }
        return youtubeContentList;
    }

    public List<Subscription> getSubscribeInfo(String id) throws IOException {
        return youtubeDataAgent.getSubscribeInfo(getAccessToken(id).getAccess_token());
    }

    public RefreshToken getRefreshToken(String id) {
        return youtubeTokenMapper.findByUserId(id);
    }

    private OauthAccessToken getAccessToken(String id) {
        RefreshToken refreshToken = getRefreshToken(id);
        return youtubeTokenAgent.getAccessToken(refreshToken.getRefresh_token());
    }

    @Transactional
    public void sentiment() {
        int page = 0;
        List<YoutubeContent> youtubeContentList;
        int MAX_SIZE = 300;
        do {
            youtubeContentList = youtubeContentQdslRepository.findByParam(YoutubeContentSearchParam.of(null, null), PageRequest.of(page, MAX_SIZE))
                    .stream()
                    .map(YoutubeContent::of)
                    .collect(Collectors.toList());
            youtubeContentList.forEach((content)->{
                if (content.getPercent() == null) {
                    CommentSentimentAnalysisResponse commentSentimentAnalysisResponse = youtubeCommentAnalyzerAgent.getCommentAnalysis(content.getComments());
                    content.setKeywords(commentSentimentAnalysisResponse.getKeywords());
                    content.setPercent((double) commentSentimentAnalysisResponse.getPercent());
                    youtubeContentMapper.update(content);
                }
            });
            ++page;
        } while (youtubeContentList.size() == MAX_SIZE);
    }
}
