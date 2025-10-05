package com.archetype.layer.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * Configuration for internationalization (i18n) support in the layer module.
 * <p>
 * Provides message source for error messages and locale resolution.
 * Follows ADR 0016 (Exception handling strategy) for configurable error messages.
 */
@Configuration
public class InternationalizationConfig {

    /**
     * Configure the message source for internationalized error messages.
     * Messages are loaded from messages.properties and locale-specific variants.
     *
     * @return Configured MessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        // Base name for message bundles (messages.properties, messages_es.properties, etc.)
        messageSource.setBasename("messages");

        // Use UTF-8 encoding for proper character support
        messageSource.setDefaultEncoding("UTF-8");

        // Don't fall back to system locale if requested locale is not available
        messageSource.setFallbackToSystemLocale(false);

        // Use message code as default message if message is not found
        messageSource.setUseCodeAsDefaultMessage(true);

        // Cache messages for better performance
        messageSource.setCacheSeconds(3600); // Cache for 1 hour

        return messageSource;
    }

    /**
     * Configure locale resolver to determine user's preferred locale.
     * Uses Accept-Language header from HTTP requests.
     *
     * @return Configured LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();

        // Default to English if no locale is specified
        resolver.setDefaultLocale(Locale.ENGLISH);

        // Support common locales (can be extended as needed)
        resolver.setSupportedLocales(java.util.List.of(
                Locale.ENGLISH,
                Locale.forLanguageTag("es"), // Spanish
                Locale.forLanguageTag("fr"), // French
                Locale.forLanguageTag("de"), // German
                Locale.JAPANESE
        ));

        return resolver;
    }
}
