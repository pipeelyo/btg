package com.example.btg.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public String jwtSecret() {
        return "test-secret";
    }
    
    @Bean
    public Long jwtExpiration() {
        return 86400000L; // 24 hours in milliseconds
    }
}
