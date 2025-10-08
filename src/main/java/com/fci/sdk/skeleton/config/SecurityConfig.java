package com.fci.sdk.skeleton.config;

import com.fci.sdk.skeleton.exception.SkeletonSecurityException;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Skeleton application.
 * 
 * This configuration is designed for a data access layer where CSRF protection
 * is not required as the application primarily serves as an API backend.
 * 
 * CSRF is disabled by default across all environments. See ADR-0000 for details.
 */
@Configuration
@EnableWebSecurity
@NoArgsConstructor
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * 
     * This configuration:
     * - Disables CSRF protection (see ADR-0000)
     * - Permits all API endpoints without authentication
     * - Requires authentication for actuator endpoints
     * - Disables HTTP Basic authentication
     * 
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     */
    @Bean
    //TODO: Can this be static?
    public SecurityFilterChain filterChain(final HttpSecurity http)  {
        try {
            http
                    // Disable CSRF protection - see global ADR 0000 for rationale
                    .csrf(AbstractHttpConfigurer::disable)

                    // Configure authorization
                    .authorizeHttpRequests(authz -> authz
                            // Allow all API endpoints without authentication
                            .requestMatchers("/api/**").permitAll()
                            // Allow Swagger UI and OpenAPI docs
                            .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                            // Require authentication for actuator endpoints
                            .requestMatchers("/actuator/**").authenticated()
                            // Allow health endpoint for monitoring
                            .requestMatchers("/actuator/health/**").permitAll()
                            // Require authentication for all other requests
                            .anyRequest().authenticated()
                    )

                    // Disable HTTP Basic authentication
                    .httpBasic(AbstractHttpConfigurer::disable);

                    return http.build();
        } catch (final Exception e) {
            throw new SkeletonSecurityException("Error configuring security filter chain", e);
        }
    }
} 
