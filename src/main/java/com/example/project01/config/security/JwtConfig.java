package com.example.project01.config.security;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtConfig {
    private final String HEADER = "token";
    private final String ISSUER = "moon";
    private final String CLIENT_SECRET = "EENY5W0eegTf1naQB2eDeyCLl5kRS2b8xa5c4qLdS0hmVjtbvo8tOyhPMcAmtPuQ";
    private final int EXPIRY_SECONDS = 600;
}
