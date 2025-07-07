package com.example.btg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.time.OffsetDateTime;
import java.util.Optional;

@Configuration
@EnableMongoAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class AuditConfig {

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }
}
