package com.fci.sdk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for FCI (Failed Customer Interaction) components.
 * This class provides the necessary beans for Redis operations and JSON serialization.
 */
@Configuration
@NoArgsConstructor
public class FciConfiguration {

    /**
     * Creates a RedisTemplate bean configured for FCI operations.
     * Uses String serialization for both keys and values to ensure compatibility
     * with the FCI event format.
     *
     * @param connectionFactory The Redis connection factory
     * @return Configured RedisTemplate for FCI operations
     */
    /*@Bean
    public RedisTemplate<String, String> fciRedisTemplate(final RedisConnectionFactory connectionFactory) {
        final RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String serialization for both keys and values
        final StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        template.afterPropertiesSet();
        return template;
    }*/

    /**
     * Creates an ObjectMapper bean configured for FCI JSON serialization.
     * Includes proper handling of Java 8 time types and pretty printing for development.
     *
     * @return Configured ObjectMapper for FCI JSON operations
     */
    @Bean
    public ObjectMapper fciObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();

        // Register Java 8 time module for proper timestamp handling
        mapper.registerModule(new JavaTimeModule());

        // Enable pretty printing for better readability in development
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Disable writing dates as timestamps to use ISO format
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
} 
