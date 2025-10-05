# ADR 0016 — Exception handling strategy with RFC 9457 compliance

Date: 2025-10-05

Status: Accepted

## Decision

Implement a comprehensive exception handling strategy for the layer module using:

1. **Domain-specific exceptions** with error codes and internationalization support
2. **Global exception handler** that produces RFC 9457 compliant error responses
3. **Hybrid validation approach** combining Bean Validation (@Valid) for format/constraint validation and domain exceptions for business logic validation
4. **Configurable error messages** using Spring's MessageSource with locale support
5. **Structured logging** with context information without exposing internals in responses

## Context

Spring Boot applications need consistent error handling that:
- Provides meaningful error responses to API consumers
- Follows international standards (RFC 9457 Problem Details)
- Supports multiple languages/locales
- Separates format validation from business logic validation
- Enables proper debugging without exposing sensitive information
- Maintains clean controller code per ADR 0015

The existing approach of throwing generic `RuntimeException` and using `ResponseStatusException` inconsistently led to poor error responses and debugging difficulties.

## Problem

### Current Issues:
- **Generic exceptions**: `RuntimeException("Pokemon not found")` provides no context
- **No standardization**: Different controllers handle errors differently
- **No internationalization**: Error messages are hardcoded in English
- **Mixed validation concerns**: Format validation mixed with business logic
- **Poor API experience**: Inconsistent error response formats
- **Debugging difficulties**: No structured logging or error correlation

### Requirements:
- RFC 9457 compliant error responses
- Configurable error messages with i18n support
- Clear separation between format and business validation
- Comprehensive logging without exposing internals
- Clean controller code without exception handling boilerplate

## Architecture

### Exception Hierarchy

```
LayerDomainException (base)
├── PokemonNotFoundException (404)
├── PokemonAlreadyExistsException (409)
├── PokemonValidationException (422)
└── PokemonServiceException (500)
```

### Validation Strategy (Hybrid Approach)

**Technical/Format Validation (@Valid)**
- Required fields (`@NotNull`, `@NotBlank`)
- Format constraints (`@Size`, `@Min`, `@Max`, `@Pattern`)
- Data type validation
- Results in HTTP 400 Bad Request

**Business Logic Validation (Domain Exceptions)**
- Duplicate business keys
- Complex business rules
- Cross-entity validations
- Results in appropriate HTTP status (409, 422, etc.)

### Response Format (RFC 9457)

```json
{
  "type": "https://example.com/problems/pokemon-not-found",
  "title": "Pokemon Not Found",
  "status": 404,
  "detail": "Pokemon with ID 12345-abc-def was not found",
  "instance": "/api/pokemon/12345-abc-def",
  "timestamp": "2024-10-05T05:00:00Z",
  "errorCode": "POKEMON_NOT_FOUND",
  "reason": "The requested Pokemon does not exist in the database"
}
```

For validation errors with multiple fields:
```json
{
  "type": "https://example.com/problems/validation-error",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Request contains 2 validation errors",
  "timestamp": "2024-10-05T05:00:00Z",
  "errorCode": "VALIDATION_ERROR",
  "errors": [
    {
      "field": "name",
      "rejectedValue": "",
      "message": "Pokemon name is required",
      "code": "pokemon.name.required"
    },
    {
      "field": "nationalId",
      "rejectedValue": 0,
      "message": "National ID must be at least 1",
      "code": "pokemon.national-id.min"
    }
  ]
}
```

## Implementation

### 1. Domain Exception Base Class

```java
public abstract class LayerDomainException extends RuntimeException {
    private final String errorCode;
    private final Object[] messageArgs;
    
    protected LayerDomainException(String errorCode, Object... messageArgs) {
        super(errorCode);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs != null ? messageArgs.clone() : new Object[0];
    }
    
    public String getErrorCode() { return errorCode; }
    public Object[] getMessageArgs() { return messageArgs.clone(); }
    public String getReasonCode() { 
        return errorCode.toUpperCase().replace('.', '_').replace('-', '_'); 
    }
}
```

### 2. Specific Domain Exceptions

```java
public class PokemonNotFoundException extends LayerDomainException {
    public PokemonNotFoundException(UUID id) {
        super("pokemon.not-found", id);
    }
}

public class PokemonAlreadyExistsException extends LayerDomainException {
    public PokemonAlreadyExistsException(int nationalId) {
        super("pokemon.already-exists.national-id", nationalId);
    }
}
```

### 3. Global Exception Handler

```java
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class LayerGlobalExceptionHandler {
    
    private final MessageSource messageSource;
    
    @ExceptionHandler(PokemonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handlePokemonNotFound(PokemonNotFoundException ex, 
                                               HttpServletRequest request,
                                               Locale locale) {
        // Log with context (no stack trace in response)
        log.warn("Pokemon not found: errorCode={}, args={}, request={}", 
                ex.getErrorCode(), Arrays.toString(ex.getMessageArgs()), request.getRequestURI());
        
        String detail = messageSource.getMessage(ex.getErrorCode(), ex.getMessageArgs(), locale);
        // ... build RFC 9457 response
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex,
                                         HttpServletRequest request,
                                         Locale locale) {
        // Handle @Valid errors with detailed field information
    }
}
```

### 4. Internationalization Configuration

```java
@Configuration
public class InternationalizationConfig {
    
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
    
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
}
```

### 5. Message Properties

```properties
# messages.properties
pokemon.not-found=Pokemon with ID {0} was not found
pokemon.already-exists.national-id=Pokemon with national ID {0} already exists
pokemon.name.required=Pokemon name is required
pokemon.national-id.min=National ID must be at least {0}

# Error reason codes
pokemon.not-found.reason=The requested Pokemon does not exist in the database
pokemon.already-exists.reason=A Pokemon with the same identifier already exists
```

### 6. Service Layer Integration

```java
@Service
@Slf4j
public class PokemonService {
    
    public Pokemon getPokemon(UUID id) {
        log.debug("Retrieving Pokemon with ID: {}", id);
        
        return pokemonRepository.findById(id)
                .map(persistenceMapper::toDomain)
                .orElseThrow(() -> new PokemonNotFoundException(id));
    }
    
    public Pokemon createPokemon(PokemonCreate request) {
        if (pokemonRepository.existsByNationalId(request.nationalId())) {
            throw new PokemonAlreadyExistsException(request.nationalId());
        }
        // ... creation logic
    }
}
```

### 7. Controller Integration

```java
@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PokemonDetails createPokemon(@Valid @RequestBody PokemonCreate req) {
        // @Valid handles format validation -> 400 with field errors
        // Service throws domain exceptions -> 409, 422, 500 with business context
        Pokemon created = pokemonService.createPokemon(req);
        return pokemonDtoMapper.toDetails(created);
    }
}
```

## HTTP Status Code Mapping

| Exception Type | HTTP Status | Usage |
|----------------|-------------|-------|
| `MethodArgumentNotValidException` | 400 Bad Request | @Valid format/constraint violations |
| `PokemonNotFoundException` | 404 Not Found | Entity does not exist |
| `PokemonAlreadyExistsException` | 409 Conflict | Business key conflicts |
| `PokemonValidationException` | 422 Unprocessable Entity | Business rule violations |
| `PokemonServiceException` | 500 Internal Server Error | System/integration failures |

## Logging Strategy

### Client/Business Errors (WARN level):
- Pokemon not found
- Validation failures
- Business rule violations
- No stack traces

### System Errors (ERROR level):
- Service exceptions
- Unexpected errors
- Include stack traces for debugging

### Example Log Output:
```
2024-10-05 05:00:00 WARN  - Pokemon not found: errorCode=pokemon.not-found, args=[12345-abc-def], request=/api/pokemon/12345-abc-def
2024-10-05 05:01:00 WARN  - Validation failed for request: /api/pokemon, errors: 2
2024-10-05 05:02:00 ERROR - Pokemon service error: errorCode=pokemon.service.external-failure, args=[PokeAPI, fetch], request=/api/pokemon/populate/first-generation
```

## Benefits

### For Developers:
- **Clean controllers**: No exception handling boilerplate
- **Consistent responses**: All errors follow RFC 9457 format
- **Easy debugging**: Structured logging with correlation
- **Type safety**: Specific exception types vs generic strings

### For API Consumers:
- **Standard format**: RFC 9457 compliance for tools/libraries
- **Machine readable**: Error codes for programmatic handling
- **Human readable**: Localized messages for user interfaces
- **Complete information**: All validation errors in single response

### For Operations:
- **Structured logging**: Easy parsing and alerting
- **No sensitive data**: Stack traces not exposed to clients
- **Correlation**: Request context in all error logs
- **Monitoring**: Specific error codes for metrics/alerts

## Migration Strategy

### Phase 1: Foundation
1. ✅ Create exception hierarchy and base classes
2. ✅ Implement global exception handler
3. ✅ Set up internationalization configuration
4. ✅ Create message properties

### Phase 2: Service Layer
1. ✅ Replace generic RuntimeException with domain exceptions
2. ✅ Add structured logging
3. ✅ Implement business validation logic

### Phase 3: Controller Layer
1. ✅ Add @Valid annotations to DTOs
2. ✅ Remove exception handling from controllers
3. ✅ Update API documentation

### Phase 4: Testing & Documentation
1. ⏳ Create unit tests for exception scenarios
2. ⏳ Integration tests for error responses
3. ⏳ Update API documentation with error examples

## Trade-offs

### Pros:
- **Standardized**: RFC 9457 compliance
- **Maintainable**: Centralized error handling
- **International**: Multi-language support
- **Debuggable**: Structured logging without exposure
- **Clean**: Controllers focus on business logic

### Cons:
- **Complexity**: More files and configuration
- **Learning curve**: Team needs to understand error codes and patterns
- **Performance**: Message resolution and exception creation overhead
- **Coupling**: Global handler coupled to all domain exceptions

## Examples in This Codebase

All examples can be found in the layer module:
- Exception hierarchy: `src/main/java/com/archetype/layer/domain/exception/`
- Global handler: `src/main/java/com/archetype/layer/config/LayerGlobalExceptionHandler.java`
- Message properties: `src/main/resources/messages.properties`
- Service integration: `src/main/java/com/archetype/layer/service/PokemonService.java`
- Controller integration: `src/main/java/com/archetype/layer/controller/PokemonController.java`

## Future Enhancements

### Potential Improvements:
- **Error correlation IDs**: Add request correlation for distributed tracing
- **Rate limiting errors**: Handle 429 Too Many Requests
- **Validation groups**: Different validation rules for different operations
- **Error templates**: Standardized error response builders
- **Metrics integration**: Count errors by type for monitoring
