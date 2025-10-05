# ADR 0007 — Prefer Spring Cloud OpenFeign for synchronous HTTP clients

Date: 2025-10-03

Status: Accepted

Decision
--------
Do not use any other HTTP/REST client library. Use Spring Cloud OpenFeign (`spring-cloud-starter-openfeign`) exclusively for HTTP/REST clients across this repository. Define typed interfaces for remote services and use Feign clients (`@FeignClient`) with Spring integration (`componentModel = "spring"`) so client code is declarative, testable, and consistent across modules. Any deviation from this rule requires a documented ADR-approved exception.

Context
-------
The repository is a template / archetype for multiple architectures. Many modules need to call external HTTP services (internal microservices or third-party APIs). Using a standard, declarative client library across the project improves consistency and makes it easier to add cross-cutting features like retries, timeouts, metrics, and stubbing for tests.

Problem
-------
- Rolling custom REST clients in many places increases duplication and inconsistent error handling.
- Hand-written HTTP code tends to be imperative and tends to mix concerns (serialization, headers, retries).
- Tests and stubs become harder to maintain.

Rationale
---------
- Feign provides an interface-first declarative client model that reads well and maps closely to service contracts.
- Spring Cloud OpenFeign integrates with Spring DI, configuration, load-balancing and message converters.
- Feign interfaces are easy to mock or replace for tests (WireMock and contract tests).
- Centralizing client behavior (interceptors, encoders, decoders, error handling) is easier with Feign.
- It encourages a clear boundary for remote communication and aligns with the ADRs that encourage mapping and separation of concerns.

When to use Feign
-----------------
- Use Feign for all HTTP/REST client calls in this codebase; it is the mandated, standard client across modules.
- Do not introduce alternative HTTP client libraries. If a genuine technical limitation of Feign is encountered, open an ADR describing the problem and obtain explicit approval to introduce an exception.
- Use Feign clients for internal microservice-to-microservice calls and third-party HTTP APIs, and centralize shared concerns (interceptors, error decoders, timeouts) via configuration.

Consequences
------------
Positive:
- Consistent declarative client style across modules.
- Easier to add cross-cutting behaviors (interceptors, metrics, tracing).
- Simpler testing and local stubbing (interfaces are mockable).

Negative:
- Adds a dependency on Spring Cloud OpenFeign.
- Feign is synchronous — not ideal for reactive stacks.
- Extra abstraction layer that may obscure some low-level HTTP details.

Guidelines & Examples
---------------------
1. Add dependency (Gradle) — example:
```gradle
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
}
```
Ensure dependency management BOM imports (Spring Cloud) are configured in build.gradle (this project already imports spring-cloud BOM).

2. Enable Feign clients in configuration:
```java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.archetype")
public class ArchetypeApplication { ... }
```

3. Define a typed Feign client:
```java
@FeignClient(name = "pokemon-service", url = "${clients.pokemon.url}")
public interface PokemonClient {
    @PostMapping("/api/pokemon")
    PokemonDetails createPokemon(@RequestBody PokemonCreate create);

    @GetMapping("/api/pokemon/{id}")
    PokemonDetails getPokemon(@PathVariable UUID id);
}
```

4. Organize client components in encapsulated packages:
- Place all client-related code under `clients.<service>` package (e.g., `clients.pokemon`)
- Include: client interface, DTOs, mappers, configuration, error decoders, and service classes
- Keep client service classes (`*ClientService`) in the same package to encapsulate usage patterns
- This encapsulation makes it easier to mock entire client modules and maintain clear boundaries

5. Use a mapper at the adapter boundary:
- Map incoming domain model or DTO to the Feign client DTO and vice versa. Keep mapping code separate (MapStruct preferred, per ADR 0002).

6. Configure shared aspects centrally:
- Interceptors (add auth headers)
- Error decoder to translate HTTP errors into domain or application exceptions
- Timeouts and connection pool settings via properties
- Tracing and metrics (OpenTelemetry / Micrometer)

7. Error handling / fallbacks:
- Prefer explicit error decoding and domain-specific exceptions rather than blind fallbacks.
- If fallback behavior is required, provide an explicit fallback implementation bean and document the behavior. Consider resilience patterns (resilience4j) for retries/circuit-breakers and keep those concerns configurable via properties.

8. Testing and stubbing:
- Unit tests: mock the Feign interfaces.
- Integration / contract tests: use WireMock or contract-based tests to stub remote services.
- For local development, consider a property profile that points Feign clients at local stubs.

9. Security:
- Centralize authentication in a request interceptor that adds Authorization headers.
- Never log full credentials; run request/response bodies through the project's Redactor/SafeLogger per ADR 0005.

Implementation Notes
--------------------
- This ADR is a project policy: Feign is the required HTTP/REST client. Any module seeking to deviate must file an ADR and obtain approval that documents the technical justification and mitigations.
- The repository already includes the Spring Cloud BOM; add the `spring-cloud-starter-openfeign` dependency and enable `@EnableFeignClients`.
- Provide an example `FeignConfiguration` to declare common encoders/decoders, a shared error decoder, and request interceptors.

Example Feign configuration bean:
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

Implementation / Migration Steps
-------------------------------
1. Add the dependency to build.gradle when you intend to use Feign:
   - `implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'`
2. Enable Feign in the application:
   - `@EnableFeignClients` on the main application class or a configuration class.
3. Define Feign client interfaces for external services and document their expected DTOs and mapping rules.
4. Provide shared Feign configuration (interceptors, decoders, timeouts) in a central `config` package.
5. Add unit and integration tests (mocked clients and WireMock stubs).
6. Document usage in CONTRIBUTING.md or module README.

Notes
-----
- Feign is the required HTTP/REST client across this repository.
- Keep Feign client DTOs separate from domain models and persistence models and ensure mapping is explicit (per ADR 0002).
- Add monitoring, tracing and safe-logging to Feign clients to keep observability consistent.
