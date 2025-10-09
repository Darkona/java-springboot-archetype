## ADR Arch-002 Domain Validation Strategy

**Date:** 2025-10-02

**Status:** Accepted

### Decision
Domain model classes must encapsulate domain logic and enforce invariants. Constructors and setters must validate inputs to ensure that domain entities/aggregates cannot be created or mutated into an invalid state. Utility methods may be included in domain classes to support domain behavior. Each domain model should also have associated domain-specific exception types (or a clear hierarchy) that indicate which domain object failed and why.

### Context
The domain layer is the core of the application, modeling business rules and ensuring resilience, self-validation, and reusability. Allowing invalid instances to exist poses risks such as subtle bugs, runtime failures, and scattered validation logic elsewhere (controllers, services, persistence).

### Problems
- Creating domain objects with invalid states can propagate errors and complicate reasoning about behavior.
- Validation scattered across layers (controllers, services) leads to duplication and inconsistent rules.
- Throwing generic exceptions obscures which domain object and which constraint failed.

### Rationale
- **Fail fast:** Validate at the point of creation/mutation to catch errors early.
- **Single source of truth:** Domain classes own their invariants, reducing duplication.
- **Readability and intent:** Constructors with validation clearly indicate required fields.
- **Testability:** Domain invariants can be tested independently.
- **Diagnostics:** Domain-specific exceptions make it easier to identify different failure causes.

### Rules
1. **Constructor validation**
   - All constructors that create domain objects must validate parameters and throw a domain-specific exception when invalid.
   - Use defensive copies for mutable inputs (e.g., lists, maps) when appropriate.
   - Prefer factory methods or static named constructors if multiple creation semantics are required.

2. **Setter validation**
   - Setters (or mutation methods) must validate inputs and preserve invariants.
   - Prefer explicit mutation methods that express domain intent (e.g., `changeName(...)`, `withdraw(amount)`) over generic setters.

3. **Immutability & final fields**
   - Prefer making required fields final. If full immutability is impractical for an aggregate, keep invariants enforced through methods and validate all mutations.
   - Use defensive immutability for collections: expose unmodifiable views.

4. **Utility & behavior methods**
   - Provide utility methods that operate on the object's internal state (e.g., `isAdult()`, `applyDamage(int)`, `promote()`).
   - Keep behavior in domain models where it belongs — do not shift core business logic to DTOs or persistence classes.

5. **Domain-specific exceptions**
   - Define exceptions that are specific enough to identify the failing domain object and the nature of the problem.
   - Exception naming pattern: `<DomainObjectName>ValidationException`, or a hierarchy like `DomainException` → `PokemonException` → `PokemonValidationException`.
   - Exceptions should include meaningful messages and optional contextual data (e.g., invalid field name, provided value).

6. **Validation strategy**
   - Prefer explicit validation in domain code. Lightweight helper validation utilities may be used, but they should throw domain exceptions, not generic runtime exceptions.
   - Avoid relying solely on external frameworks (e.g., Bean Validation on DTOs) to enforce domain invariants — DTO validation should complement, not substitute, domain validation.

### Examples
**Constructor with Validation and Domain Exception:**
```java
package com.archetype.layer.domain.model;

import com.archetype.layer.domain.exception.PokemonValidationException;
import java.util.Objects;
import java.util.UUID;

public class Pokemon {
    private final UUID id;
    private String name;
    private final int nationalId;

    public Pokemon(UUID id, String name, int nationalId) {
        this.id = Objects.requireNonNull(id, "id is required");
        setName(name); // reuse validation
        if (nationalId <= 0) {
            throw new PokemonValidationException("nationalId must be positive: " + nationalId);
        }
        this.nationalId = nationalId;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new PokemonValidationException("name cannot be null or blank");
        }
        this.name = name.trim();
    }

    // domain utility
    public boolean isStarter() {
        return this.nationalId == 1 || this.nationalId == 4 || this.nationalId == 7;
    }
}
```

**Domain Exception Example:**
```java
package com.archetype.layer.domain.exception;

public class PokemonValidationException extends RuntimeException {
    public PokemonValidationException(String message) {
        super(message);
    }

    public PokemonValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Notes on Design Choices
- Use unchecked domain exceptions (subclasses of RuntimeException) when callers are not expected to recover and when the error indicates invalid input needing correction in code or during upstream validation. Use checked exceptions only when recovery is a regular part of the workflow.
- Maintain DTO/Controller-level validation (e.g., `@Valid`, bean validation) for quick feedback at API boundaries; continue enforcing domain invariants within the domain code.
- Consider creating small helper validation utilities inside the domain package (e.g., `DomainPreconditions`) which throw domain exceptions to minimize repetitive boilerplate while keeping exceptions meaningful.
- Add unit tests for domain constructors, mutation methods, and utility methods to verify invariants and exception messages.

### Implementation / Migration Steps
1. Integrate this ADR into `docs/adr`.
2. Scan domain packages for classes that:
    - Lack constructor validation.
    - Expose raw setters that could violate invariants.
    - Do not contain domain-specific exception types.
3. For each class:
    - Implement validation in constructors and mutation methods.
    - Replace generic setters with intention-revealing methods where necessary.
    - Add or reuse a domain-specific exception class.
    - Introduce unit tests to assert that invalid states throw the expected domain exceptions.
4. Maintain controller DTO validation (Bean Validation) but avoid reliance on it for domain invariants.
5. If using Lombok, do not depend on Lombok-generated all-args constructors without validation wrappers or factory methods. Lombok is suitable for getters, equals, hashCode, toString, and builders, but validation must always be executed.

### Consequences / Trade-offs
- **Positive:** Domain invariants are centralized and enforced; systems become more robust and easier to debug.
- **Negative:** More validation code and tests; potential duplication of similar checks (reduced through domain helper utilities).
- **Risk:** If relying blindly on Lombok-generated constructors, validation may be bypassed. Utilize factories or explicit constructors when validation is necessary.
