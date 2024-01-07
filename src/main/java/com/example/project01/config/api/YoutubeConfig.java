package com.example.project01.config.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties({YoutubeConfig.class})
@ConfigurationProperties(prefix="youtube")
public class YoutubeConfig {
    private String oauthServer;
    private String clientId;
    private String clientPwd;
    private String redirectUrl;
    private String apiKey;
    private String sentimentApiServer;
}
