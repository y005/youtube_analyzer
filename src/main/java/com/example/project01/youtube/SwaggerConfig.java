package com.example.project01.youtube;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket youtubeApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("youtube")
                .select()
                .apis(RequestHandlerSelectors.
                        basePackage("com.example.project01"))
                .paths(PathSelectors.ant("/youtube/**"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "유튜브 API",
                "내가 구독한 채널의 영상 분석 정보를 제공해주는 API입니다.",
                "1.0",
                null,
                new Contact("Contact", "https://github.com/y005", "y005@naver.com"),
                null,
                null,
                new ArrayList<>()
        );
    }
}