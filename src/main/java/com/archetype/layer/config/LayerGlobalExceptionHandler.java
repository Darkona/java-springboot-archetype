package com.archetype.layer.config;

import com.archetype.layer.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the layer module.
 * 
 * Provides RFC 9457 compliant error responses with internationalization support.
 * Handles both Bean Validation (@Valid) errors and domain-specific exceptions.
 * 
 * Follows ADR 0015 (Spring annotations over ResponseEntity) and 
 * ADR 0016 (Exception handling strategy).
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class LayerGlobalExceptionHandler {
    
    private final MessageSource messageSource;
    
    private static final String PROBLEM_BASE_URL = "https://example.com/problems";
    
    /**
     * Build a RFC 9457 compliant ProblemDetail for domain exceptions.
     * Centralizes the common response building logic to reduce duplication.
     * 
     * IMPORTANT: This method NEVER includes stack traces or internal details in the response.
     * Only localized user-friendly messages are exposed via the API.
     */
    private ProblemDetail buildDomainProblemDetail(
            LayerDomainException ex, 
            HttpServletRequest request, 
            Locale locale, 
            HttpStatus status, 
            String problemType,
            String reasonKey) {
        
        // Only include localized messages - NO internal details or stack traces
        String detail = messageSource.getMessage(ex.getErrorCode(), ex.getMessageArgs(), locale);
        String reason = messageSource.getMessage(reasonKey, null, locale);
        String title = messageSource.getMessage("http." + status.value() + ".title", null, locale);
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create(PROBLEM_BASE_URL + "/" + problemType));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("errorCode", ex.getReasonCode());
        problem.setProperty("reason", reason);
        
        return problem;
    }
    
    /**
     * Log domain exceptions with consistent format and appropriate log level.
     * Stack traces are ONLY logged internally, NEVER exposed in API responses.
     */
    private void logDomainException(LayerDomainException ex, HttpServletRequest request, boolean logAsError) {
        String message = "{} exception: errorCode={}, args={}, request={}";
        Object[] args = {
            ex.getClass().getSimpleName(), 
            ex.getErrorCode(), 
            java.util.Arrays.toString(ex.getMessageArgs()), 
            request.getRequestURI()
        };
        
        if (logAsError) {
            log.error(message, args, ex); // Include stack trace in LOGS ONLY for service errors
        } else {
            log.warn(message, args); // No stack trace for client errors
        }
    }
    
    /**
     * Handle Pokemon not found exceptions.
     * Results in HTTP 404 Not Found.
     */
    @ExceptionHandler(PokemonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handlePokemonNotFound(PokemonNotFoundException ex, 
                                               HttpServletRequest request,
                                               Locale locale) {
        
        logDomainException(ex, request, false);
        return buildDomainProblemDetail(ex, request, locale, HttpStatus.NOT_FOUND, 
                                       "pokemon-not-found", ex.getErrorCode() + ".reason");
    }
    
    /**
     * Handle Pokemon already exists exceptions.
     * Results in HTTP 409 Conflict.
     */
    @ExceptionHandler(PokemonAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handlePokemonAlreadyExists(PokemonAlreadyExistsException ex,
                                                    HttpServletRequest request,
                                                    Locale locale) {
        
        logDomainException(ex, request, false);
        return buildDomainProblemDetail(ex, request, locale, HttpStatus.CONFLICT, 
                                       "pokemon-already-exists", "pokemon.already-exists.reason");
    }
    
    /**
     * Handle Pokemon business validation exceptions.
     * Results in HTTP 422 Unprocessable Entity.
     */
    @ExceptionHandler(PokemonValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handlePokemonValidation(PokemonValidationException ex,
                                                 HttpServletRequest request,
                                                 Locale locale) {
        
        logDomainException(ex, request, false);
        return buildDomainProblemDetail(ex, request, locale, HttpStatus.UNPROCESSABLE_ENTITY, 
                                       "pokemon-validation-error", "pokemon.validation.reason");
    }
    
    /**
     * Handle Pokemon service exceptions.
     * Results in HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(PokemonServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handlePokemonService(PokemonServiceException ex,
                                              HttpServletRequest request,
                                              Locale locale) {
        
        logDomainException(ex, request, true); // Log as ERROR (with stack trace in logs only)
        return buildDomainProblemDetail(ex, request, locale, HttpStatus.INTERNAL_SERVER_ERROR, 
                                       "pokemon-service-error", "pokemon.service.reason");
    }
    
    /**
     * Handle Bean Validation errors (@Valid annotations).
     * Results in HTTP 400 Bad Request with detailed field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex,
                                         HttpServletRequest request,
                                         Locale locale) {
        
        // Log validation failure
        log.warn("Validation failed for request: {}, errors: {}", 
                request.getRequestURI(), ex.getBindingResult().getFieldErrorCount());
        
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        
        // Build detailed validation errors
        List<Map<String, Object>> errors = fieldErrors.stream()
            .map(error -> {
                String message = messageSource.getMessage(error, locale);
                return Map.of(
                    "field", error.getField(),
                    "rejectedValue", error.getRejectedValue() != null ? error.getRejectedValue() : "",
                    "message", message,
                    "code", error.getCode() != null ? error.getCode() : "validation.error"
                );
            })
            .collect(Collectors.toList());
        
        String title = messageSource.getMessage("http.400.title", null, locale);
        String detail = messageSource.getMessage("validation.multiple-errors", new Object[]{errors.size()}, locale);
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle(title);
        problem.setType(URI.create(PROBLEM_BASE_URL + "/validation-error"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("errorCode", "VALIDATION_ERROR");
        problem.setProperty("errors", errors);
        
        return problem;
    }
    
    /**
     * Handle method argument type mismatch errors (e.g., invalid UUID format).
     * Results in HTTP 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                           HttpServletRequest request,
                                           Locale locale) {
        
        log.warn("Type mismatch error: parameter={}, value={}, request={}", 
                ex.getName(), ex.getValue(), request.getRequestURI());
        
        String title = messageSource.getMessage("http.400.title", null, locale);
        String detail = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle(title);
        problem.setType(URI.create(PROBLEM_BASE_URL + "/type-mismatch"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("errorCode", "TYPE_MISMATCH");
        problem.setProperty("parameter", ex.getName());
        problem.setProperty("rejectedValue", ex.getValue());
        
        return problem;
    }
    
    /**
     * Handle any other layer domain exceptions.
     * Results in HTTP 500 Internal Server Error as fallback.
     */
    @ExceptionHandler(LayerDomainException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleLayerDomain(LayerDomainException ex,
                                          HttpServletRequest request,
                                          Locale locale) {
        
        logDomainException(ex, request, true); // Log as ERROR (with stack trace in logs only)
        
        // Fallback to generic reason for unhandled domain exceptions
        String reasonKey = ex.getErrorCode() + ".reason";
        return buildDomainProblemDetail(ex, request, locale, HttpStatus.INTERNAL_SERVER_ERROR, 
                                       "layer-domain-error", reasonKey);
    }
}
