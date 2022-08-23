package com.example.project01.youtube.controller;

import com.example.project01.security.JwtAuthentication;
import com.example.project01.security.JwtAuthenticationProvider;
import com.example.project01.security.JwtAuthenticationToken;
import com.example.project01.youtube.agent.YoutubeCommentAnalyzerAgent;
import com.example.project01.youtube.agent.YoutubeTokenAgent;
import com.example.project01.youtube.dto.*;
import com.example.project01.youtube.entity.User;
import com.example.project01.youtube.entity.YoutubeContent;
import com.example.project01.youtube.service.UserService;
import com.example.project01.youtube.service.YoutubeService;
import com.google.api.services.youtube.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube")
public class YoutubeApiController {
    public static final String YOUTUBE_CRAWL_LOG_MESSAGE = "youtube:crawl:{} {}";
    private final YoutubeTokenAgent youtubeTokenAgent;
    private final YoutubeCommentAnalyzerAgent youtubeCommentAnalyzerAgent;
    private final YoutubeService youtubeService;
    private final UserService userService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @ExceptionHandler({Exception.class})
    public ResponseV1 handle(Exception e) {
        return ResponseV1.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @PostMapping("/signup")
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
                .user_id(userId)
                .user_password(userPassword)
                .user_roles("ROLE_USER")
                .build();
    }

    @GetMapping("/login")
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

    @GetMapping("/subscribe")
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

    @GetMapping("/oauth")
    public void oauth(HttpServletResponse response) throws IOException {
        response.sendRedirect(youtubeTokenAgent.makeOauthServerUrl());
    }

    @GetMapping("/redirect")
    public ResponseV1 redirect(@RequestParam("code") String code) {
        String userId = "sm";
        OauthRefreshToken oauthRefreshToken;
        try {
            oauthRefreshToken = youtubeTokenAgent.getRefreshToken(code);
            youtubeService.saveToken(oauthRefreshToken.toRefreshToken(userId));
            userService.save(User.builder().user_id(userId).build());
        } catch (Exception e) {
            log.warn("redirect:fail {}", e.getMessage());
            return ResponseV1.error(HttpStatus.BAD_REQUEST, "에러 발생");
        }
        return ResponseV1.ok(oauthRefreshToken);
    }

    @GetMapping("/test")
    public CommentSentimentAnalysisResponse test() {
        String comments = "여성 댄서들의 자존심을 건 생존 경쟁 리얼리티 서바이벌\uD83D\uDD25[#스트릿우먼파이터] 더 보러가기\\r\\n→https://youtube.com/playlist?list=PLqGVA3Cdr7gyMGMQEISObBctFx5f5AQ00 스텝업 영화의 한장면을 보는것같아요! 저 남자거든요 근데 와 이무대 보니까 진짜 언니란 소리가 절로 나옴니다 다들 너무 멋지고 화려해요 진짜 리스펙!!! 허니제이님 정말 독보적인듯 동작이 깔끔하고 군더더기가 없네 흔들림이 없이 멈출때 딱 멈춰줌 깔끔스 뽀대가 댄스영상으로도 눈물을 흘리게 될줄이야 ㅠㅠ\\n모든 댄서분들 다 멋지고 아름답고 최고십니다! 훅 카레이서 착장…. 오닥후 마음을 울리네요… 홀리뱅은 리더님 역량도 뛰어나지만 이런 무대 구성이 굉장히 고퀄인 느낌 ㅎㅎㅎㅎ 언니 사랑해요 ㅎㅎㅎㅎ\\n근데 라치카 무대도 넘 좋았음 라치카는 멤버 한 명 한 명 다 인기 있지 않음? 다 정감 가는 귀여운 소녀들같아 ㅎㅎㅎ\\n라치카 다 잘 춰 진짜 ㄷㄷㄷㄷ 허니제이\uD83D\uDE1A\uD83D\uDC4D\uD83D\uDC95 너~~~무멋있엉ㅜ 솔직히 잘추는 지 모르겠다 아이돌무대보면 확실히 거대자본과 그업계최고실력파 기획자들이 만들어놓은 무대와 오랜연습시간동안 만들어진 춤이랑 단기간 구성된 토너먼트 형태에서 만들어진 춤의 완성도가 아예 다름. 보아눈에는 어찌보였을지모르겠네 아이키 진짜 머리좋고 아이디어가 넘치는듯... 어떻게 수화,엄마를 연결해서 춤을 짠것도 그렇고 어딜가든 엄마 서사는 먹히니까... 진짜 대단하다 아이키\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB 진짜 취향 문제지 빠지는 퍼포가 하나도 없구나 개쩐다 엠넷이 딱 하나 잘한 거 캐스팅.. 능력 좋고 끼 좋은 업계 탑 사람들 모아놓으니 스케줄이나 진행이나 무대가 구려도 커버를 넘어서 훌륭히 소화함 훅 무대 보고울었다 ㅠㅜㅜ수화를 넣으니\\n넘 감동.. 아니 신나게 보다가 훅 엄마가 머리만져주자마자 오열하잖아 ㅠㅠㅠㅠㅠㅠㅠㅠㅠㅠㅠㅠㅠㅠㅠㅠㅠ 스우파 그냥 전원일기처럼 계속 해줘라… 모든 무대가 다 소름이다\\n황금라치카 개인적으로 미쳤고 너무 좋음 밝은 에너지가..\\n코카 리헤이 특유 그 느낌쓰 미침\\n홀리뱅 베놈은 말로 다했지 허제 진짜\\n훅 진짜 온몸 전율오는데 마지막 때탄 발바닥까지 완벽 아니 탈락제도는 왜 만들어 놓은거야 그냥 탈락말고 점수로 해도 댄서들 승부욕 있을텐데 스우파2 제작해 엠넷 당장 홀리뱅 베놈 마지막 엔딩 뭔가 예술이다..\uD83D\uDC4D 쫌이쑴 남성댄서들도 나오겠구나~~~기대~기대 코카앤 씨엘 너무심하게 카메라 잡아주던데 나만느끼나 ㅈㄴ 아쉬운부분 아 리정무대 개보고싶다고 다른 팀들이 음악에 어울리는 춤을 만들었다면\\n홀리뱅은 아예 모든 음절을 춤으로 만들어버린 느낌 스우파 모든팀 존멋~♡ 마지막 춤고 예술인데 와 홀리뱅이란 팀이 진짜 엄청 멋있는 팀이구나 ㄷ ㄷ 팀 훅은 ㄹㅇ 존나 힙 그 자체같음 가장 스트릿하고 이 프로그램 주인공같은 느낌임 어머머 카레이서춤.. 우와 우리방탄같이 칼춤같아요 너무 멋찌당 홀리뱅 진짜,,,, 진짜 대박임 유튜브 알고리즘에 떳길래 봤는데 다들..조오온나 멋있다 그리고 마지막 무대 보고 울음 ㅠㅠ 춤+예술+전문성\\n믓찌다~!!! 처음에는 입벌리고 보다가 뒤로 갈수록 나 우네 또… 심사위원을\\nsm이 독식한것도 이해 안되는데\\n태용이 심사하는 것도 코미디.\\n황상훈은 춤꾼에 안무가로서 오랜 경력이 있고\\n보아는 무대짬밥과 해석력 풍부한 표현력이라도 있으니 십분 이해하는데\\n태용은 대체 무슨 자격으로 져지 자리에 앉는건지..\\nsm 영업력으로 쪼무래기가 상전노릇한거라고 밖에는 생각이 안되네.\\n참가자들에게 굉장히 모욕적인 일 아닌가. 하도 스우파 스우파 해서 뒤늦게 보고있는데 베놈만 기억에 남네 홀리뱅의 베놈, 훅의 마지막 무대는 댄스를 클래식에 버금가는 예술의 경지에 올려놓은 듯 합니다. 이제까지 전혀 관심 없었는데, 나혼자산다에 나오신 분 보고 들어오게 되었네요~ 이런 기회를 통해서 댄서분들의 새로운 시도와 창작 무대를 많이 보게 되었으면 좋겠습니다~~ 모두가 정말 멋지십니다~~~ 훅 마지막 무대 상상이상 감동이상이었다 13:22 개소름 4:15 ㅅㅂ 왜 가비언니 두 분이신가 했네 와지엑스.. 못 잃어.. 물론 다 좋아해 사랑해 잘모르지만 멋있다 그걸로 끝 ^^ 자기가 좋아하는 일을 찾아서  힘들어도 자신의 열정과 노력을 쏟아부으며 계속 앞으로 나아가는 모습이 너무 멋있다,,방송을 통해 댄서분들이 하고싶은 일을 하면서 그 일에 자부심을 느끼는 게 잘 보여서 너무 멋있었고 그래서 더 예뻐보이고 매력적으로 느껴지는 것 같다. 너무너무 닮고싶고 너무 멋있고 좋다 베놈 찢었다 ㄹㅇ 17:17 아이 키우는 우는 아이키 쇼미보다가 알고리즘이 갑자기 여기로 인도해주네 뭐지 ㄷㄷ 베놈이 넘사벽인듯... 11:52 꿀언니의 세계관 최강자재질 빛나는버클 모먼트 (존멋) 다 멋있는데 홀리뱅이 젤 멋있었던듯 파이널무대에 맞는 무대인듯 아 라치카 눈물나❤❤ \uD83D\uDC4D 7rings에서 라치카 나올 때 분위기 전환되는거 정말 강력하다 내 심장을 찢엇어 라치카 훅은 진짜 내 스타일이랄까.. 장르가 다 비슷해보여 아이키팀은 좋다 힘빼서 코카는 완전 걋 뭐라해야돼지 진짜 딱 홀림 라치카 진짜 황홀 그 자체였어 특히 막판에 리안 미소 12:51 개무섭다 4:15 yes! 언니들모두모두 너어무  멋져서 숨을 못쉬겠다~~! 댄스팀 동료 팀멋짐폭팔 역대급으로 멋짐 멋지십니다 늠젬나요 유퀴즈 잘봣어요 역시 쵝고 음원 퍼포는 코카 라치카 훅 홀리뱅\\n자유 퍼포는 홀리뱅 라치카 코카 훅 와...진짜 이건 영화로 나와야한다.. 한국판 스텝업이네 5:45 이부분 지선언니 미친거같음ㅠㅠㅠㅠㅠㅠ존멋\\n8:50 윌리바운스 마 이제 걸.스.캔.두.애.니.띵.이디 습자지드라.. 모든팀이 다뭇지다 자기들이 좋아하는걸 즐기면서도 모든걸 거는모습은 충분히 인정받고 응원을 아낌없이 보내줘야 한다 모든팀 앞으로 무조건 흥해라 정말쵝오다 확실히 라치카 팀은 왁킹이 돋보인다 리안님이 청하님 메인 안무가라서 그런가 살짝살짝 청하님 스테이투나잇 안무 느낌이 많이 묻어나네 느므 므찌고 조으다 !!!!!!!! 아씨ㅜㅜ홀리뱅에 소름돋고 훅때매 울엇음ㅡ는 종이한장차이란말이 여기에 어울리네 우열을 가릴수가 없다 노래는 음색이 있어서 그 사람의 개성이 뚜렷한데\\n춤에는 춤선이 있긴 하지만 그걸로 그 사람의 시그니처가 되지는 않는거 같다.\\n그래서 뭔가 유명한 댄서의 유명한 춤 이런게 잘 없는거 같다. 언니들 여성성 팔아 성상품화 하는 이런  프로는 왜 생기는건데 holybang let’s go~ 4:16 ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ \uD83D\uDE2D\uD83D\uDE2D\uD83D\uDE2D 제인 표정 봐라~~~~~요~~~~~ 와…. 홀리뱅은 그냥 미쳤다…. 리안 ㅜㅜㅜ찡 훅 모야... 왜 울게만들어 \\n몰입도 쩌네 ㅜ CL이 너무 강렬했다..... 나오는 순간부터 CL콘서트의 한장면이 되어버림...... 너무 아쉽네......... 홀리뱅 제인?이라는 사람 좀 매력이 있는것 같다 나는 47세지만\\n언니들!!!! 진짜 멋있어요.\\n대한민국 모든 여성분들 정말 응원해요! 홀리뱅 허니제이 이분은\\n간지가 뭔지를 아는분인거같아 \\n이게 간지다 를 보여주는 그런느낌 댄서라는 말쓰지마라 진짜 예술가다. 너무멋지다ㅠ 파란 옷 입은 사람들이 제일 잘하는 것 같다 허니제이 보러왔다가  라치카언니들 무대보고 이밤에 심쿵중 훅 무대 진짜 비상이다... 갠적으로 코카앤버터 무대 둘다 너무 멋있었음 제일로 의상도 너무 예쁘고 ㅜㅜ 나 그만 자고싶은데 엄마가 딸에게도 너무 좋네...골드라치카도 그렇고 무대적인 것들이 조아 베놈 맘에는다 다멋있다 다쩐다걍 나는 어느 팀이든 모든 한사람한사람 다빛나보여서 순위는벗어났다..감동이였다 1.파이널 찢은건 진짜 라치카….골드치카의 벅찬느낌 여운 쩔고 시미즈 라틴파트 짜릿 그 자체 표현력 대박\\n2. 훅의 카레이서 도입부 킬링  미쳤다고봄 딱딱 맞을때의 전율\\n3. 홀리뱅 진짜 어떻게 그 노래에 저런구성을 뽑아내지? 매번 너무 진짜 신기하고 멋짐 그 자체 볼수록화나는거... 출연한 모든팀의 파이널무대를 못봣다는거... 각자 색이 1000000%로 분출하는 무대를 이제야 해주는데... 하 아 어케... 나샛기 라치카 무대에서부터 울면 어카라는거냐 한민족의 끼를 마음끗 보여주시네요. 얼마나 고생하고 준비하셨는지, 심쿵 감동입니다.  감사합니다. 나는 개인적으로 코카엔무대에 cl 무대 중앙까지 온거 좀 아쉽 5:18 진짜 너무 예뻐 미친 저는  몸치지만 춤을 너무 좋아 합니다 60이 다되가는 나이지만 춤이 배우고 싶죠 ㅎ~\\n너무 멋찌네요!~~^^ 13:28 우희님이 여기서 왜나와 ?? 대박 ㅎㅎㅎㅎㅎ 11:22부터 제인 존재감 미침 진짜 너무 멋있어 ";
        return youtubeCommentAnalyzerAgent.getCommentAnalysis(comments);
    }

    @Scheduled(cron = "0 0 17 * * *")
    public void crawlingYoutubeContent() {
        int offset = 0;
        List<User> users;
        int MAX_USER = 300;
        do {
            users = userService.getEveryUser(offset, MAX_USER);
            users.forEach(
                    (user) -> {
                        try {
                            youtubeService.getYoutubeContent(user.getUser_id());
                            log.info(YOUTUBE_CRAWL_LOG_MESSAGE, user.getUser_id());
                        } catch (Exception e) {
                            log.warn(YOUTUBE_CRAWL_LOG_MESSAGE, user.getUser_id(), e.getMessage());
                        }
                    }
            );
            offset += users.size();
        } while (users.size() == MAX_USER);
    }
}
