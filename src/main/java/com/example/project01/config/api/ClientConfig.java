package com.example.project01.config.api;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Configuration
public class ClientConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .additionalInterceptors(retryInterceptor())
                .build();
    }

    @Bean
    public ClientHttpRequestInterceptor retryInterceptor() {
        return (request, body, execution) -> retryTemplate().execute(context -> {
            try {
                return execution.execute(request, body);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));
        return retryTemplate;
    }

    @Bean
    public YouTube youtube() {
        return new YouTube.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), request -> {}).build();
    }
}
