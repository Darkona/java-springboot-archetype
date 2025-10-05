package com.archetype.layer.client.pokeapi;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Error decoder for PokeAPI client responses.
 * Converts HTTP error responses from PokeAPI into domain-specific exceptions.
 * Follows ADR 0007 (Prefer OpenFeign) error handling patterns.
 */
@Slf4j
public class PokeApiClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.warn("PokeAPI error for method '{}': {} {}", 
                methodKey, response.status(), response.reason());

        return switch (response.status()) {
            case 404 -> new PokeApiNotFoundException(
                    "Pokemon not found: " + response.reason());
            case 429 -> new PokeApiRateLimitException(
                    "PokeAPI rate limit exceeded. Please wait before retrying.");
            case 500, 502, 503, 504 -> new PokeApiServiceException(
                    "PokeAPI service is temporarily unavailable: " + response.reason());
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }

    /**
     * Exception thrown when a Pokemon is not found in PokeAPI.
     */
    public static class PokeApiNotFoundException extends RuntimeException {
        public PokeApiNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when PokeAPI rate limit is exceeded.
     */
    public static class PokeApiRateLimitException extends RuntimeException {
        public PokeApiRateLimitException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when PokeAPI service is unavailable.
     */
    public static class PokeApiServiceException extends RuntimeException {
        public PokeApiServiceException(String message) {
            super(message);
        }
    }
}
