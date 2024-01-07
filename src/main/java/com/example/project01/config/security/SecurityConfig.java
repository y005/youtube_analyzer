package com.example.project01.config.security;

import com.example.project01.youtube.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
public class SecurityConfig {
    private static final String[] AUTH_WHITELIST = {
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v2/**",
            "/youtube/login/**",
            "/youtube/redirect/**",
            "/youtube/oauth/**",
            "/youtube/signup/**",
            "/main/**",
            "/asset/**"
    };
    private static final String[] AUTH_ADMIN_LIST = {
            "/youtube/crawling/**"
    };
    private static final String[] AUTH_USER_LIST = {
            "/youtube/subscribe/**",
            "/youtube/content/**",
            "/youtube/test/**"
    };
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public Jwt jwt(JwtConfig jwtConfig) {
        return new Jwt(jwtConfig.getISSUER(), jwtConfig.getCLIENT_SECRET(), jwtConfig.getEXPIRY_SECONDS());
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        Jwt jwt = applicationContext.getBean(Jwt.class);
        JwtConfig jwtConfig = applicationContext.getBean(JwtConfig.class);
        return new JwtAuthenticationFilter(jwt, jwtConfig);
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        Jwt jwt = applicationContext.getBean(Jwt.class);
        UserService userService = applicationContext.getBean(UserService.class);
        return new JwtAuthenticationProvider(jwt, userService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(jwtAuthenticationProvider())
                .authorizeRequests()
                    .antMatchers(AUTH_WHITELIST).permitAll()
                    .antMatchers(AUTH_ADMIN_LIST).hasAnyRole("ADMIN")
                    .antMatchers(AUTH_USER_LIST).hasAnyRole("USER")
                    .anyRequest().denyAll()
                .and()
                .formLogin()
                    .disable()
                .csrf()
                    .disable()
                .headers()
                    .disable()
                .httpBasic()
                    .disable()
                .rememberMe()
                    .disable()
                .logout()
                    .disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .addFilterAfter(jwtAuthenticationFilter(), SecurityContextHolderFilter.class);
        return http.build();
    }
}
