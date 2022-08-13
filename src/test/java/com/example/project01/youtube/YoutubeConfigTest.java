package com.example.project01.youtube;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
class YoutubeConfigTest {
    public static final String CLIENT_ID = "198658285628-fhaur6ctnljr5kf1867sf57e84c02vco.apps.googleusercontent.com";
    public static final String CLIENT_PWD = "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";
    public static final String OAUTH_SERVER = "https://accounts.google.com/o/oauth2/auth";
    public static final String REDIRECT = "http://localhost:8080/youtube/redirect";
    @Autowired
    private YoutubeConfig youtubeConfig;

    @Test
    void getClientId() {
        assertThat(youtubeConfig.getClientId(), is(CLIENT_ID));
    }

    @Test
    void getClientPwd() {
        assertThat(youtubeConfig.getClientPwd(), is(CLIENT_PWD));
    }

    @Test
    void getRedirectUrl() {
        assertThat(youtubeConfig.getRedirectUrl(), is(REDIRECT));
    }

    @Test
    void getOauthServer() {
        assertThat(youtubeConfig.getOauthServer(), is(OAUTH_SERVER));
    }
}