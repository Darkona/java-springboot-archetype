package com.fci.sdk.skeleton.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * This class configures the OpenAPI specification for the application,
 * including API information, contact details, license, and server configurations.
 */
@Configuration
@NoArgsConstructor
public class OpenApiConfig {

    /** Application name injected from Spring configuration */
    @Value("${spring.application.name:Skeleton}")
    private String applicationName;

    /**
     * Creates and configures the OpenAPI specification bean.
     * This method sets up the complete OpenAPI documentation including
     * API metadata, contact information, license details, and server configurations.
     * 
     * @return Configured OpenAPI specification
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API")
                        .description("API for storing and retrieving data from Redis, MongoDB, and PostgreSQL databases")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Platform Engineering Team")
                                .email("platform@dummy.com")
                                .url("https://github.com/dummy/skeleton"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.dummy.com")
                                .description("Production Server")
                ));
    }
} 
