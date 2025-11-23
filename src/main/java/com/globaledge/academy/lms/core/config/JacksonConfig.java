package com.globaledge.academy.lms.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson configuration for proper handling of Java 8 date/time types.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.build();

        // Register Java 8 date/time module for LocalDateTime, LocalDate, etc.
        mapper.registerModule(new JavaTimeModule());

        // Configure to write dates as ISO-8601 strings instead of timestamps
        // Example: "2025-10-25T14:30:00" instead of 1729865400000
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Additional useful configurations
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return mapper;
    }
}