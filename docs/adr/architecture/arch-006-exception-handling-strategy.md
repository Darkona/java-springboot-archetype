## ADR Arch-006 — Exception handling strategy with RFC 9457 compliance

**Date:** 2025-10-05

**Status:** Accepted

### Decision

Implement a comprehensive exception handling strategy for the layer module using:

1. **Domain-specific exceptions** with error codes and internationalization support.
2. **Global exception handler** that produces RFC 9457 compliant error responses.
3. **Hybrid validation approach** combining Bean Validation (`@Valid`) for format/constraint validation and domain exceptions for business logic validation.
4. **Configurable error messages** utilizing Spring's MessageSource with locale support.
5. **Structured logging** with contextual information without exposing internal details in responses.

### Context

Spring Boot applications require consistent error handling that:

- Delivers meaningful error responses to API consumers.
- Adheres to international standards (RFC 9457 Problem Details).
- Supports multiple languages and locales.
- Separates format validation from business logic validation.
- Allows effective debugging without disclosing sensitive information.
- Preserves clean controller code per ADR 0015.

The existing approach of throwing generic `RuntimeException` and employing `ResponseStatusException` inconsistently has resulted in poor error responses and debugging challenges.

### Problem

Current issues include:

- **Generic exceptions:** `RuntimeException("Pokemon not found")` lacks context.
- **No standardization:** Different controllers handle errors inconsistently.
- **Lack of internationalization:** Error messages are hardcoded in English.
- **Mixed validation concerns:** Format validation is intertwined with business logic.
- **Poor API experience:** Inconsistent error response formats.
- **Debugging difficulties:** No structured logging or error correlation.

### Requirements

- RFC 9457 compliant error responses.
- Configurable error messages with internationalization support.
- Clear separation between format and business validation.
- Comprehensive logging without disclosing internal details.
- Clean controller code devoid of exception handling boilerplate.

### Architecture

#### Exception Hierarchy

```
LayerDomainException (base)
├── PokemonNotFoundException (404)
├── PokemonAlreadyExistsException (409)
├── PokemonValidationException (422)
└── PokemonServiceException (500)
```

#### Validation Strategy (Hybrid Approach)

**Technical/Format Validation (`@Valid`)**

- Required fields (`@NotNull`, `@NotBlank`).
- Format constraints (`@Size`, `@Min`, `@Max`, `@Pattern`).
- Data type validation.
- Results in HTTP 400 Bad Request.

**Business Logic Validation (Domain Exceptions)**

- Duplicate business keys.
- Complex business rules.
- Cross-entity validations.
- Results in appropriate HTTP status (409, 422, etc.).

#### Response Format (RFC 9457)

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

For validation errors involving multiple fields:

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

### Implementation

1. **Domain Exception Base Class:**
```java
public abstract class LayerDomainException extends RuntimeException {
    private final String errorCode;
    private final Object[] messageArgs;

    protected LayerDomainException(String errorCode, Object... messageArgs) {
        super(errorCode);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs != null ? messageArgs.clone() : new Object[0];
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getMessageArgs() {
        return messageArgs.clone();
    }

    public String getReasonCode() {
        return errorCode.toUpperCase().replace('.', '_').replace('-', '_');
    }
}
```

2. **Specific Domain Exceptions:**
```java
public class PokemonNotFoundException extends LayerDomainException {
    public PokemonNotFoundException(UUID id) {
        super("pokemon.not-found", id);
    }
}

public class PokemonAlreadyExistsException extends LayerDomainException {
    public PokemonAlreadyExistsException(int nationalId) {
        super("pokemon.already-exists", nationalId);
    }
}
```

3. **Global Exception Handler:**
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
        log.warn("Pokemon not found: errorCode={}, args={}, request={}",
                ex.getErrorCode(), Arrays.toString(ex.getMessageArgs()), request.getRequestURI());

        String detail = messageSource.getMessage(ex.getErrorCode(), ex.getMessageArgs(), locale);
        // Construct RFC 9457 response...
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

4. **Internationalization Configuration:**
```java
@Configuration
public class InternationalizationConfig {
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
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

5. **Message Properties:**
```properties
# messages.properties
pokemon.not-found=Pokemon with ID {0} was not found
pokemon.already-exists=Pokemon with national ID {0} already exists
pokemon.name.required=Pokemon name is required
pokemon.national-id.min=National ID must be at least {0}
# Error reason codes
pokemon.not-found.reason=The requested Pokemon does not exist in the database
pokemon.already-exists.reason=A Pokemon with the same identifier already exists
```

6. **Service Layer Integration:**
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
        // Creation logic...
    }
}
```

7. **Controller Integration:**
```java
@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PokemonDetails createPokemon(@Valid @RequestBody PokemonCreate req) {
        Pokemon created = pokemonService.createPokemon(req);
        return pokemonDtoMapper.toDetails(created);
    }
}
```

### HTTP Status Code Mapping
| Exception Type                    | HTTP Status               | Usage                               |
|-----------------------------------|---------------------------|-------------------------------------|
| `MethodArgumentNotValidException` | 400 Bad Request           | @Valid format/constraint violations |
| `PokemonNotFoundException`        | 404 Not Found             | Entity does not exist               |
| `PokemonAlreadyExistsException`   | 409 Conflict              | Business key conflicts              |
| `PokemonValidationException`      | 422 Unprocessable Entity   | Business rule violations            |
| `PokemonServiceException`         | 500 Internal Server Error  | System/integration failures         |

### Logging Strategy

#### Client/Business Errors (WARN level):
- Pokemon not found
- Validation failures
- Business rule violations
- No stack traces should be revealed.

#### System Errors (ERROR level):
- Service exceptions
- Unexpected errors
- Stack traces for debugging should be included.

### Example Log Output:
```
2024-10-05 05:00:00 WARN  - Pokemon not found: errorCode=pokemon.not-found, args=[12345-abc-def], request=/api/pokemon/12345-abc-def
2024-10-05 05:01:00 WARN  - Validation failed for request: /api/pokemon, errors: 2
2024-10-05 05:02:00 ERROR - Pokemon service error: errorCode=pokemon.service.external-failure, args=[PokeAPI, fetch], request=/api/pokemon/populate/first-generation
```

### Benefits
#### For Developers:
- **Clean Controllers:** No exception handling boilerplate.
- **Consistent Responses:** All errors adhere to RFC 9457 format.
- **Easy Debugging:** Structured logging with correlation.
- **Type Safety:** Specific exception types instead of generic strings.

#### For API Consumers:
- **Standard Format:** Compliance with RFC 9457.
- **Machine Readable:** Error codes for automated handling.
- **Human Readable:** Localized messages for UI.
- **Complete Information:** All validation errors captured in a single response.

#### For Operations:
- **Structured Logging:** Facilitates easy parsing and alerting.
- **No Sensitive Data:** Stack traces are concealed from clients.
- **Correlation:** Request context maintained in all error logs.
- **Monitoring:** Specific error codes assist in metrics and alerts.

### Migration Strategy
#### Phase 1: Foundation
1. Create an exception hierarchy and base classes.
2. Implement a global exception handler.
3. Establish internationalization configuration.
4. Create message properties.

#### Phase 2: Service Layer
1. Replace generic RuntimeExceptions with domain exceptions.
2. Add structured logging.
3. Implement business validation logic.

#### Phase 3: Controller Layer
1. Introduce @Valid annotations to DTOs.
2. Eliminate exception handling from controllers.
3. Update API documentation.

#### Phase 4: Testing & Documentation
1. Create unit tests for exceptional scenarios.
2. Integration tests for error responses.
3. Update API documentation with error examples.

### Trade-offs
#### Pros:
- **Standardized:** Compliance with RFC 9457.
- **Maintainable:** Centralized error management.
- **International:** Support for multiple languages.
- **Debuggable:** Structured logging without exposing internals.
- **Clear:** Controllers focus on business logic.

#### Cons:
- **Complexity:** Increased number of files and configurations.
- **Learning Curve:** Team needs familiarity with error codes and patterns.
- **Performance:** Some overhead for message resolution and exception creation.
- **Coupling:** Global handler tied to all domain exceptions.

### Examples in This Codebase
Numerous examples exist within the layer module:
- **Exception hierarchy:** Located in `src/main/java/com/archetype/layer/domain/exception/`.
- **Global handler:** Implemented in `src/main/java/com/archetype/layer/config/LayerGlobalExceptionHandler.java`.
- **Message properties:** Contained within `src/main/resources/messages.properties`.
- **Service integration:** Found in `src/main/java/com/archetype/layer/service/PokemonService.java`.
- **Controller integration:** Available at `src/main/java/com/archetype/layer/controller/PokemonController.java`.

### Future Enhancements
#### Potential Improvements:
- **Error Correlation IDs:** Introduce request correlation for distributed tracing.
- **Rate Limiting Errors:** Manage 429 Too Many Requests responses.
- **Validation Groups:** Different rules for various operations.
- **Error Templates:** Standardized error response builders.
- **Metrics Integration:** Count errors by type for monitoring purposes.
