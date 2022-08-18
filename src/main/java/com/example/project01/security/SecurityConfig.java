package com.example.project01.security;

import com.example.project01.youtube.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

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
    public AuthenticationManager authenticationManager() throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = applicationContext.getBean(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.authenticationProvider(jwtAuthenticationProvider()).build();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                    .antMatchers("/youtube/**").hasAnyRole("USER")
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
                .addFilterAfter(jwtAuthenticationFilter(), SecurityContextHolderFilter.class)
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authenticationManager(authenticationManager());
        return http.build();
    }
}
