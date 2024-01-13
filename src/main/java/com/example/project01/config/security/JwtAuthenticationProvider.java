package com.example.project01.config.security;

import com.example.project01.youtube.model.User;
import com.example.project01.youtube.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//커스텀 인증 객체에 대한 인가 객체
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final Jwt jwt;
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //미인증된 객체애 대한 로그인과 Jwt 토큰 발행이 동작
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String userId = (String) jwtAuthenticationToken.getPrincipal();
        String userPassword = (String) jwtAuthenticationToken.getCredentials();
        User user = userService.login(userId, userPassword);
        if (user != null) {//등록된 유저인 경우 Jwt 토큰 발행과 함께 인증 객체를 생성함
            JwtAuthentication jwtAuthentication = getJwtAuthentication(user);
            List<GrantedAuthority> authorities = getAuthorities(user);
            JwtAuthenticationToken authenticated = new JwtAuthenticationToken(jwtAuthentication, null, authorities);
            authenticated.setDetails(user);
            return authenticated;
        }
        throw new RuntimeException("등록되지 않은 유저입니다.");
    }

    private JwtAuthentication getJwtAuthentication(User user) {
        String token = jwt.sign(Jwt.Claims.from(user.getId(), user.getRoles()));
        return new JwtAuthentication(token, user.getId());
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> authorities;
        authorities = user.getRoles()
                .stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
