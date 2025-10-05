package com.archetype.layer.client.pokemon;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Pokemon Feign client.
 * Demonstrates centralized client configuration as per ADR 0007.
 */
@Configuration
public class PokemonClientConfiguration {

    @Value("${clients.pokemon.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${clients.pokemon.read-timeout:10000}")
    private int readTimeout;

    /**
     * Configure logging level for Feign client.
     * BASIC level logs request method, URL, response status and execution time.
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * Request interceptor to add common headers or authentication.
     * Example: adding correlation IDs, authentication tokens, etc.
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("Content-Type", "application/json");
            template.header("Accept", "application/json");
            // Add correlation ID or authentication headers here
            // template.header("Authorization", "Bearer " + tokenProvider.getToken());
        };
    }

    /**
     * Custom error decoder to handle HTTP errors and translate them to domain exceptions.
     * This allows the client to throw meaningful exceptions instead of generic FeignExceptions.
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new PokemonClientErrorDecoder();
    }
}
