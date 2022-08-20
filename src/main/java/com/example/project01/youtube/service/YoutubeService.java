package com.example.project01.youtube.service;

import com.example.project01.youtube.agent.YoutubeDataAgent;
import com.example.project01.youtube.agent.YoutubeTokenAgent;
import com.example.project01.youtube.dto.OauthAccessToken;
import com.example.project01.youtube.entity.RefreshToken;
import com.example.project01.youtube.entity.YoutubeContent;
import com.example.project01.youtube.repository.YoutubeContentRepository;
import com.example.project01.youtube.repository.YoutubeTokenRepository;
import com.google.api.services.youtube.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeService {
    private final YoutubeTokenRepository youtubeTokenRepository;

    private final YoutubeContentRepository youtubeContentRepository;

    private final YoutubeTokenAgent youtubeTokenAgent;

    private final YoutubeDataAgent youtubeDataAgent;

    @Transactional
    public void saveToken(RefreshToken refreshToken) {
        youtubeTokenRepository.save(refreshToken);
    }

    public List<YoutubeContent> getYoutubeContent(String id) throws IOException {
        OauthAccessToken access_token = getAccessToken(id);
        List<YoutubeContent> youtubeContentList = youtubeDataAgent.getYoutubeContent(access_token.getAccess_token());
        for (YoutubeContent element : youtubeContentList) {
            element.setUser_id(id);
            try {
                youtubeContentRepository.save(element);
            } catch (Exception ignored) {
                throw new RuntimeException("이미 존재하는 유튜브 영상 정보입니다.");
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
}
