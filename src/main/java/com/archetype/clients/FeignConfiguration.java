package com.archetype.clients;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Shared Feign configuration for all clients.
 * Provides a basic authorization interceptor and a simple error decoder placeholder.
 */
@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor authorizationInterceptor() {
        return template -> {
            // Example: add Authorization header from a centralized provider or property
            template.header("Authorization", "Bearer ${clients.default.token:dummy}");
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder() {
            private final ErrorDecoder defaultDecoder = new Default();

            @Override
            public Exception decode(String methodKey, feign.Response response) {
                // Translate HTTP errors into domain-specific exceptions if desired.
                // For now delegate to default decoder.
                return defaultDecoder.decode(methodKey, response);
            }
        };
    }
}

