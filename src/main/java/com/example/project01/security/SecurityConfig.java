package com.example.project01.security;

import com.example.project01.youtube.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
public class SecurityConfig {
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
                    .antMatchers("/youtube/subscribe/**", "/youtube/content/**").hasAnyRole("USER")
                    .anyRequest().permitAll()
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
                .addFilterAfter(jwtAuthenticationFilter(), SecurityContextPersistenceFilter.class);
        return http.build();
    }
}
