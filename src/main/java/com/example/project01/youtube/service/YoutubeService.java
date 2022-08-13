package com.example.project01.youtube.service;

import com.example.project01.youtube.agent.YoutubeDataAgent;
import com.example.project01.youtube.agent.YoutubeTokenAgent;
import com.example.project01.youtube.dto.OauthAccessToken;
import com.example.project01.youtube.entity.RefreshToken;
import com.example.project01.youtube.repository.YoutubeTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class YoutubeService {
    private final YoutubeTokenRepository youtubeTokenRepository;

    private final YoutubeTokenAgent youtubeTokenAgent;

    private final YoutubeDataAgent youtubeDataAgent;

    @Transactional
    public void saveToken(RefreshToken refreshToken) {
        youtubeTokenRepository.save(refreshToken);
    }

    public void analysis(String userId) throws IOException {
        RefreshToken refreshToken = getRefreshToken(userId);
        OauthAccessToken oauthAccessToken = youtubeTokenAgent.getAccessToken(refreshToken.getRefresh_token());
        youtubeDataAgent.analysis(oauthAccessToken.getAccess_token());
    }

    @Transactional(readOnly = true)
    public RefreshToken getRefreshToken(String userId) {
        return youtubeTokenRepository.findByUserId(userId);
    }

    public OauthAccessToken getAccessToken(String userId) {
        RefreshToken refreshToken = getRefreshToken(userId);
        return youtubeTokenAgent.getAccessToken(refreshToken.getRefresh_token());
    }
}
