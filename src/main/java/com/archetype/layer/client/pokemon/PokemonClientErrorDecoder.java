package com.archetype.layer.client.pokemon;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Custom error decoder for Pokemon Feign client.
 * Translates HTTP errors into meaningful domain exceptions.
 */
@Slf4j
public class PokemonClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String url = response.request().url();

        log.warn("Pokemon client error - Method: {}, URL: {}, Status: {}",
                methodKey, url, status);

        switch (status) {
            case NOT_FOUND:
                return new PokemonClientNotFoundException(
                        String.format("Pokemon not found for request: %s", methodKey)
                );
            case BAD_REQUEST:
                return new PokemonClientValidationException(
                        String.format("Invalid request for: %s", methodKey)
                );
            case UNAUTHORIZED:
                return new PokemonClientAuthenticationException(
                        "Authentication failed for Pokemon service"
                );
            case FORBIDDEN:
                return new PokemonClientAuthorizationException(
                        "Access denied for Pokemon service"
                );
            case INTERNAL_SERVER_ERROR:
                return new PokemonClientServiceException(
                        "Pokemon service internal error"
                );
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }

    /**
     * Pokemon not found exception.
     */
    public static class PokemonClientNotFoundException extends RuntimeException {
        public PokemonClientNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Validation error from Pokemon service.
     */
    public static class PokemonClientValidationException extends RuntimeException {
        public PokemonClientValidationException(String message) {
            super(message);
        }
    }

    /**
     * Authentication error with Pokemon service.
     */
    public static class PokemonClientAuthenticationException extends RuntimeException {
        public PokemonClientAuthenticationException(String message) {
            super(message);
        }
    }

    /**
     * Authorization error with Pokemon service.
     */
    public static class PokemonClientAuthorizationException extends RuntimeException {
        public PokemonClientAuthorizationException(String message) {
            super(message);
        }
    }

    /**
     * General service error from Pokemon service.
     */
    public static class PokemonClientServiceException extends RuntimeException {
        public PokemonClientServiceException(String message) {
            super(message);
        }
    }
}
