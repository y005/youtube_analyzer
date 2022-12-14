package com.example.project01.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final Jwt jwt;
    private final String header;

    public JwtAuthenticationFilter(Jwt jwt, JwtConfig jwtConfig) {
        this.jwt = jwt;
        this.header = jwtConfig.getHEADER();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        //인증 객체가 아직 저장되지 않은 경우
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = getToken(request);
            if (token != null) {//발급된 토큰을 가지고 있는 경우 <- 로그인 후 다른 서비스 요청 단계
                try {
                    Jwt.Claims claims = jwt.verify(token);
                    String userId = claims.userId;
                    List<GrantedAuthority> authorities = getAuthorities(claims);
                    if ((!userId.isBlank()) && (authorities.size() > 0)) {
                        //인증 객체 생성
                        JwtAuthenticationToken authentication = new JwtAuthenticationToken(new JwtAuthentication(token, userId), null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e){
                    log.warn("jwt:auth:fail invalid token");
                }
            }
        }
        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        return request.getHeader(header);
    }

    private List<GrantedAuthority> getAuthorities(Jwt.Claims claims) {
        return Arrays.stream(claims.roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
