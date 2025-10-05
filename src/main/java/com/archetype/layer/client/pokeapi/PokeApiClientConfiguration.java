package com.archetype.layer.client.pokeapi;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for PokeAPI Feign client.
 * Configures timeouts, logging, and error handling for PokeAPI requests.
 * Follows ADR 0007 (Prefer OpenFeign) configuration patterns.
 */
@Configuration
@Slf4j
public class PokeApiClientConfiguration {

    @Value("${clients.pokeapi.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${clients.pokeapi.read-timeout:10000}")
    private int readTimeout;

    /**
     * Configure request options for PokeAPI client.
     * Sets connection and read timeouts based on configuration.
     */
    @Bean
    public Request.Options pokeApiRequestOptions() {
        return new Request.Options(
                connectTimeout, TimeUnit.MILLISECONDS,
                readTimeout, TimeUnit.MILLISECONDS,
                true
        );
    }

    /**
     * Configure Feign logging level for PokeAPI client.
     * Uses BASIC level to log request/response without sensitive data.
     */
    @Bean
    public Logger.Level pokeApiFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * Configure error decoder for PokeAPI client.
     * Handles PokeAPI-specific error responses and converts to domain exceptions.
     */
    @Bean
    public ErrorDecoder pokeApiErrorDecoder() {
        return new PokeApiClientErrorDecoder();
    }
}
