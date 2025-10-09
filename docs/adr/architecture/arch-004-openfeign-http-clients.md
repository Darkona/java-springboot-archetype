## ADR Arch-004 — Use Spring Cloud OpenFeign for synchronous HTTP clients

**Date:** 2025-10-03

**Status:** Accepted

### Decision

No other HTTP/REST client libraries should be used. Spring Cloud OpenFeign (`spring-cloud-starter-openfeign`) is the exclusive choice for HTTP/REST clients
throughout this repository. Defines typed interfaces for remote services and employs Feign clients (`@FeignClient`) with Spring integration (
`componentModel = "spring"`) to ensure client code is declarative, testable, and consistent across modules. Any deviations from this rule require a documented
ADR-approved exception.

### Context

The repository acts as a template/archetype for various architectures. Numerous modules require calls to external HTTP services (internal microservices or
third-party APIs). Utilizing a standard, declarative client library across the project enhances consistency and simplifies the addition of cross-cutting
features such as retries, timeouts, metrics, and stubbing for tests.

### Problems

- Implementing custom REST clients in many locations increases duplication and leads to inconsistent error handling.
- Hand-written HTTP code tends to be imperative, blurring concerns such as serialization, headers, and retries.
- Maintaining tests and stubs becomes increasingly complicated.

### Rationale

- **Feign** offers an interface-first declarative client model that is intuitive and closely aligns with service contracts.
- **Spring Cloud OpenFeign** integrates seamlessly with Spring DI, configuration, load-balancing, and message converters.
- **Feign interfaces** are straightforward to mock or replace in tests (use WireMock and contract tests).
- Centralizing client behavior with Feign (interceptors, encoders, decoders, error handling) is more manageable.
- It promotes a distinct boundary for remote communication, aligning with ADRs that support mapping and separation of concerns.

### When to Use Feign

- Use Feign exclusively for all HTTP/REST client calls within this codebase; it is the mandated standard client across modules.
- Do not introduce alternative HTTP client libraries. Should a legitimate technical limitation of Feign arise, submit an ADR detailing the issue for explicit
  approval to introduce an exception.
- Employ Feign clients for both internal microservice-to-microservice calls and third-party HTTP APIs, centralizing shared concerns (interceptors, error
  decoders, timeouts) via configuration.

### Consequences

**Positive:**

- A consistent declarative client style across modules.
- Easier implementation of cross-cutting behaviors (interceptors, metrics, tracing).
- Simplified testing and local stubbing (as interfaces are mockable).

**Negative:**

- An additional abstraction layer that may obscure some low-level HTTP details.

### Guidelines & Examples

#### Client DTO Requirements

**MANDATORY:** Feign clients MUST define their own DTOs specific to the external service they call. Domain classes MUST NOT be directly employed in Feign client
interfaces.

- Craft service-specific DTOs aligning with the external API contract.
- Place these DTOs within the client package (e.g., `clients.pokemon.dto`).
- Use mappers or services to convert between client DTOs and domain objects.
- Do not expose client DTOs outside the client package boundary.

##### Example Client DTO Structure:

```java
// clients/pokemon/dto/PokemonClientResponse.java
public record PokemonClientResponse(
                Long id,
                String name,
                Integer height,
                Integer weight,
                List<String> types
        ) {
}

// clients/pokemon/dto/CreatePokemonClientRequest.java
public record CreatePokemonClientRequest(
        String name,
        Integer height,
        Integer weight,
        List<String> types
) {
}
```

1. **Add Dependency (Gradle) Example:**

```gradle
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
}
```

2. **Enable Feign Clients in Configuration:**

```java

@SpringBootApplication
@EnableFeignClients(basePackages = "com.archetype")
public class ArchetypeApplication {
    // implementation
}
```

3. **Define a Typed Feign Client with Service-Specific DTOs:**

```java

@FeignClient(name = "pokemon-service", url = "${clients.pokemon.url}")
public interface PokemonClient {
    @PostMapping("/api/pokemon")
    PokemonClientResponse createPokemon(@RequestBody CreatePokemonClientRequest request);

    @GetMapping("/api/pokemon/{id}")
    PokemonClientResponse getPokemon(@PathVariable UUID id);
}
```

4. **Organize Client Components in Encapsulated Packages:**

- Store all client-related code within the `clients.<service>` package (e.g., `clients.pokemon`).
- Include: client interface, DTOs, mappers, configuration, error decoders, and service classes.
- Maintain client service classes (`*ClientService`) in the same package to encapsulate usage patterns.
- This encapsulation facilitates mocking entire client modules and preserving clear boundaries.

5. **Implement Mappers and Services for DTO Transformation:**

- Create dedicated mappers to convert between client DTOs and domain objects.
- Employ a service layer to orchestrate client calls and domain processing.
- Keep mapping code separate (MapStruct preferred, per ADR 0002).

##### Example Mapper and Service Implementation:

```java
// clients/pokemon/mapper/PokemonClientMapper.java
@Mapper(componentModel = "spring")
public interface PokemonClientMapper {
    Pokemon toDomain(PokemonClientResponse response);

    CreatePokemonClientRequest toClientRequest(CreatePokemonCommand command);
}

// clients/pokemon/PokemonClientService.java
@Service
public class PokemonClientService {
    private final PokemonClient pokemonClient;
    private final PokemonClientMapper mapper;

    public Pokemon fetchPokemon(UUID id) {
        PokemonClientResponse response = pokemonClient.getPokemon(id);
        return mapper.toDomain(response);
    }

    public Pokemon createPokemon(CreatePokemonCommand command) {
        CreatePokemonClientRequest request = mapper.toClientRequest(command);
        PokemonClientResponse response = pokemonClient.createPokemon(request);
        return mapper.toDomain(response);
    }
}
```

6. **Centralize Configuration for Shared Aspects:**

- Configure interceptors (e.g., to add auth headers).
- Implement error decoders to translate HTTP errors into domain or application exceptions.
- Set up timeouts and connection pool settings via properties.
- Facilitate tracing and metrics (OpenTelemetry / Micrometer).

7. **Error Handling / Fallbacks:**

- Favor explicit error decoding and domain-specific exceptions over blind fallbacks.
- If fallback behavior is required, provide an explicit fallback implementation bean and document the behavior. Consider resilience patterns (resilience4j) for
  retries/circuit breakers and ensure these aspects remain configurable via properties.

8. **Testing and Stubbing:**

- For unit tests, mock the Feign interfaces.
- For integration/contract tests, utilize WireMock or contract-based testing to stub remote services.
- During local development, consider employing a property profile that points Feign clients at local stubs.

9. **Security:**

- Centralize authentication in a request interceptor that attaches Authorization headers.
- Refrain from logging full credentials; process request/response bodies through the project's Redactor/SafeLogger per ADR 0005.

### Implementation Notes

This ADR is a project policy: Feign is the mandated HTTP/REST client. Any module seeking to deviate must file an ADR and acquire approval that documents the
technical justification and mitigations. The repository already includes the Spring Cloud BOM; simply add the `spring-cloud-starter-openfeign` dependency and
enable `@EnableFeignClients`. Provide an example `FeignConfiguration` to declare common encoders/decoders, a shared error decoder, and request interceptors.

#### Example Feign Configuration Bean:

```java

@Configuration
public class FeignConfiguration {
    @Bean
    public RequestInterceptor authorizationInterceptor(TokenProvider provider) {
        return template -> template.header("Authorization", "Bearer " + provider.getToken());
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new MyFeignErrorDecoder();
    }
}
```

### Implementation / Migration Steps

1. Add the dependency to `build.gradle` when you want to use Feign:
    - `implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'`
2. Enable Feign in the application:
    - Place `@EnableFeignClients` on the main application class or a configuration class.
3. Define Feign client interfaces for external services, documenting expected DTOs and mapping rules.
4. Establish shared Feign configuration for interceptors, decoders, and timeouts in a central `config` package.
5. Incorporate unit and integration tests (mocked clients and WireMock stubs).
6. Document usage in `CONTRIBUTING.md` or module-specific README files.

### Notes

- Feign is the accepted HTTP/REST client throughout this repository.
- Maintain separation where client DTOs are distinct from domain models and persistence models, ensuring mapping is clear (as per ADR 0002).
- Integrate monitoring, tracing, and safe-logging to Feign clients to uphold observability consistency.
