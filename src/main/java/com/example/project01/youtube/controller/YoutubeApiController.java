package com.example.project01.youtube.controller;

import com.example.project01.config.security.JwtAuthentication;
import com.example.project01.config.security.JwtAuthenticationProvider;
import com.example.project01.config.security.JwtAuthenticationToken;
import com.example.project01.youtube.agent.YoutubeTokenAgent;
import com.example.project01.youtube.dto.*;
import com.example.project01.youtube.enums.RoleType;
import com.example.project01.youtube.model.User;
import com.example.project01.youtube.model.YoutubeContent;
import com.example.project01.youtube.service.UserService;
import com.example.project01.youtube.service.YoutubeService;
import com.google.api.services.youtube.model.Subscription;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube")
@Api
public class YoutubeApiController {
    private final YoutubeTokenAgent youtubeTokenAgent;
    private final YoutubeService youtubeService;
    private final UserService userService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @ExceptionHandler({Exception.class})
    @ApiIgnore
    public ResponseV1 handle(Exception e) {
        return ResponseV1.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @PostMapping("/signup")
    @ApiOperation(value="계정 생성", notes="계정을 생성한다.")
    public ResponseV1 signup(@RequestBody SignUpForm signUpForm) {
        User user = userService.findByUsername(signUpForm.getUserId());
        if (user == null) {
            userService.save(makeUser(signUpForm.getUserId(), signUpForm.getUserPassword()));
            log.info("signup:user:{} create successfully", signUpForm.getUserId());
            return ResponseV1.ok("계정 생성 성공");
        }
        else {
            log.warn("signup:user:{} already exist", signUpForm.getUserId());
            return ResponseV1.error(HttpStatus.BAD_REQUEST,"이미 존재하는 아이디");
        }
    }

    private User makeUser(String userId, String userPassword) {
        return User.builder()
                .id(userId)
                .password(userPassword)
                .roles(Collections.singletonList(RoleType.ROLE_USER))
                .build();
    }

    @GetMapping("/oauth")
    @ApiIgnore
    public void oauth(HttpServletResponse response) throws IOException {
        response.sendRedirect(youtubeTokenAgent.makeOauthServerUrl());
    }

    @GetMapping("/redirect")
    @ApiIgnore
    public ResponseV1 redirect(@RequestParam("code") String code) {
        OauthRefreshToken oauthRefreshToken;
        String userId;
        try {
            oauthRefreshToken = youtubeTokenAgent.getRefreshToken(code);
            userId = youtubeService.getUserYoutubeId(oauthRefreshToken.getAccess_token());
            youtubeService.saveToken(oauthRefreshToken.toRefreshToken(userId));
            userService.save(User.builder().id(userId).roles(Collections.singletonList(RoleType.ROLE_USER)).build());
        } catch (Exception e) {
            log.warn("redirect:fail {}", e.getMessage());
            return ResponseV1.error(HttpStatus.BAD_REQUEST, "에러 발생");
        }
        return ResponseV1.ok(oauthRefreshToken.toRefreshToken(userId));
    }

    @PostMapping("/login")
    @ApiOperation(value="로그인", notes="로그인이 성공하면 jwt 토큰을 반환한다.")
    public ResponseV1 login(@RequestBody LoginForm loginForm) {
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(loginForm.getUserId(), loginForm.getUserPassword());
        JwtAuthenticationToken authenticatedToken = (JwtAuthenticationToken) jwtAuthenticationProvider.authenticate(jwtAuthenticationToken);
        JwtAuthentication authentication = (JwtAuthentication) authenticatedToken.getPrincipal();
        log.info("user:login:{} login successfully", authentication.getUserId());
        JwtToken jwtToken = JwtToken.builder()
                .token(authentication.getToken())
                .user_id(authentication.getUserId())
                .build();
        return ResponseV1.ok(jwtToken);
    }

    @GetMapping("/content")
    @ApiOperation(value="구독 영상 정보 조회", notes="유저가 구독한 영상 정보를 반환한다. (jwt 토큰 필요)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", required = true, paramType = "header", dataTypeClass = String.class),
    })
    @Cacheable(value = "youtube", key = "{#jwtAuthentication.userId, #pagingCondition}")
    public ResponseV1 findYoutubeContent(@AuthenticationPrincipal JwtAuthentication jwtAuthentication, @RequestParam YoutubeContentSearchParam searchParam, @RequestParam int page, @RequestParam int size) {
        log.info("youtube:content:{}", jwtAuthentication.getUserId());
        List<YoutubeContentResponse> youtubeContents;
        PagingResponse<YoutubeContentResponse> result = new PagingResponse<>();
        try {
            youtubeContents = youtubeService.findYoutubeContent(searchParam, page, size)
                    .stream()
                    .map(YoutubeContentResponse::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("youtube:content:{} {}", jwtAuthentication.getUserId(), e.getMessage());
            return ResponseV1.error(HttpStatus.FORBIDDEN, "허용되지 않은 접근");
        }
        return ResponseV1.ok(result.from(page, size, youtubeContents));
    }

    //  TODO 추가 검색 api 만들기 (관련 키워드 영상 정보)

    @GetMapping("/crawling")
    @ApiOperation(value="구독 채널 영상 정보 크롤링", notes="가입한 모든 유저에 대해 새로 올라온 구독 채널 영상 정보를 수집한다. (관리자 계정만 사용 가능)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", required = true, paramType = "header", dataTypeClass = String.class),
    })
    public ResponseV1 crawlingAllYoutubeContent() {
        crawlingYoutubeContent();
        return ResponseV1.ok("crawling finished");
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void crawlingYoutubeContent() {
        int page = 0;
        List<User> users;
        int MAX_USER = 300;
        log.info("Youtube Content Crawling start :)");
        do {
            users = userService.getEveryUser(page, MAX_USER);
            users.forEach(
                    (user) -> {
                        try {
                            youtubeService.getYoutubeContent(user.getId());
                            log.info("youtube:crawl:success {}", user.getId());
                        } catch (Exception e) {
                            log.warn("youtube:crawl:fail {} {}", user.getId(), e.getMessage());
                        }
                    }
            );
            ++page;
        } while (users.size() == MAX_USER);
    }

    @GetMapping("/test/content")
    @ApiIgnore
    public ResponseV1 getYoutubeContent(@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
        log.info("youtube:content:{}", jwtAuthentication.getUserId());
        List<YoutubeContent> youtubeContents;
        try {
            youtubeContents = youtubeService.getYoutubeContent(jwtAuthentication.getUserId());
        } catch (Exception e) {
            log.warn("youtube:content:{} {}", jwtAuthentication.getUserId(), e.getMessage());
            return ResponseV1.error(HttpStatus.FORBIDDEN, "허용되지 않은 접근");
        }
        return ResponseV1.ok(youtubeContents);
    }

    @GetMapping("/test/subscribe")
    @ApiIgnore
    public ResponseV1 getSubscribeInfo(@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
        log.info("youtube:content:{}", jwtAuthentication.getUserId());
        List<Subscription> subscriptions;
        try {
            subscriptions = youtubeService.getSubscribeInfo(jwtAuthentication.getUserId());
        } catch (Exception e) {
            log.warn("youtube:subscribe:{} {}", jwtAuthentication.getUserId(), e.getMessage());
            return ResponseV1.error(HttpStatus.TOO_MANY_REQUESTS, "제한된 API 사용량 초과");
        }
        return ResponseV1.ok(subscriptions);
    }

    @GetMapping("/test/sentiment")
    @ApiIgnore
    public void sentiment() {
        youtubeService.sentiment();
    }
}
