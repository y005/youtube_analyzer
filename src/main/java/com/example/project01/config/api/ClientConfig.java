package com.example.project01.config.api;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(200).setMaxConnPerRoute(200).build();
        HttpComponentsClientHttpRequestFactory customRequestFactory = new HttpComponentsClientHttpRequestFactory();
        customRequestFactory.setHttpClient(httpClient);
        customRequestFactory.setConnectTimeout(30000);
        customRequestFactory.setReadTimeout(30000);
        return builder.requestFactory(() -> customRequestFactory).build();
    }

    @Bean
    public YouTube youtube() {
        return new YouTube.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), request -> {}).build();
    }
}
