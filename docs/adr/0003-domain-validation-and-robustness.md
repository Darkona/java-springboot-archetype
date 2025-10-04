# ADR 0003 — Domain object validation, invariants, and domain-specific exceptions

Date: 2025-10-02

Status: Accepted

Decision
--------
Domain model classes must encapsulate domain logic and enforce invariants. Constructors and setters must validate input so that it is impossible to create or mutate a domain entity/aggregate into an invalid state. Utility methods are allowed in domain classes to support domain behavior. Each domain model should have associated domain-specific exception types (or a clear hierarchy) that indicate which domain object failed and why.

Context
-------
The domain layer is the heart of the application: it models business rules and must be resilient, self-validating, and reusable. Allowing invalid instances to exist increases the risk of subtle bugs, runtime failures and scattered validation logic elsewhere (controllers, services, persistence).

Problem
-------
- Creating domain objects with invalid state can propagate errors and make it hard to reason about behavior.
- Validation spread across layers (controllers, services) leads to duplication and inconsistent rules.
- Throwing generic exceptions hides which domain object and which constraint failed.

Rationale
---------
- Fail fast: validate at point of creation / mutation to catch errors early.
- Single source of truth: domain classes own their invariants, reducing duplication.
- Readability and intent: constructors with validation make required fields explicit.
- Testability: domain invariants are tested in isolation.
- Diagnostics: domain-specific exceptions make it easier to react to different failure causes.

Rules
-----
1. Constructor validation
   - All constructors that create domain objects must validate parameters and throw a domain-specific exception when invalid.
   - Use defensive copies for mutable inputs (lists, maps) where appropriate.
   - Prefer factory methods or static named constructors if multiple creation semantics are required.

2. Setter validation
   - Setters (or mutation methods) must validate inputs and preserve invariants.
   - Prefer explicit mutation methods that express domain intent (e.g., `changeName(...)`, `withdraw(amount)`) over generic setters, when possible.

3. Immutability & final fields
   - Prefer making required fields final. If full immutability is impractical for an aggregate, keep invariants enforced by methods and validate all mutations.
   - Use defensive immutability for collections: expose unmodifiable views.

4. Utility & behavior methods
   - Provide domain utility methods that operate on the object's internal state (for example `isAdult()`, `applyDamage(int)`, `promote()`).
   - Keep behavior in the domain model where it naturally belongs — do not move core business logic to DTOs or persistence classes.

5. Domain-specific exceptions
   - Define exceptions that are specific enough to identify the failing domain object and the nature of the problem.
   - Exception naming pattern: `<DomainObjectName>ValidationException`, or a hierarchy like `DomainException` -> `PokemonException` -> `PokemonValidationException`.
   - Exceptions should contain meaningful messages and optional contextual data (e.g., invalid field name, provided value).

6. Validation strategy
   - Prefer explicit validation in the domain code. Lightweight helper validation utilities may be used, but they should throw domain exceptions, not generic runtime exceptions.
   - Avoid relying exclusively on external frameworks (e.g., Bean Validation on DTOs) to enforce domain invariants — DTO validation is complementary, not a substitute.

Examples
--------
Constructor with validation and domain exception:

```java
package com.skeletor.layer.domain.model;

import com.skeletor.layer.domain.exception.PokemonValidationException;
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

Domain exception example:

```java
package com.skeletor.layer.domain.exception;

public class PokemonValidationException extends RuntimeException {
    public PokemonValidationException(String message) {
        super(message);
    }

    public PokemonValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

Notes on design choices
-----------------------
- Use unchecked domain exceptions (RuntimeException subclasses) when callers are not expected to recover and the bug indicates invalid input that should be fixed in code or upstream validation. Use checked exceptions only when recovery is a normal part of flow.
- Keep DTO/Controller-level validation (e.g., @Valid, bean validation) for fast feedback at API boundaries; still enforce domain invariants inside domain code.
- Consider creating small helper validation utilities inside the domain package (e.g., `DomainPreconditions`) which throw domain exceptions, to reduce repeated boilerplate while keeping exceptions meaningful.
- Add unit tests for domain constructors, mutation methods and utility methods to exercise invariants and exception messages.

Implementation / Migration Steps
-------------------------------
1. Add this ADR to docs/adr.
2. Scan domain package(s) for domain classes that:
   - lack constructor validation
   - expose raw setters that can violate invariants
   - do not have domain-specific exceptions
3. For each class:
   - Add validation to constructors and mutation methods.
   - Replace generic setters with intention-revealing methods where appropriate.
   - Add or reuse a domain-specific exception class.
   - Add unit tests that assert invalid states throw the expected domain exception.
4. Keep controller DTO validation (Bean Validation) but do not rely on it for domain invariants.
5. If using Lombok, avoid relying on Lombok-generated all-args constructors without adding explicit validation wrappers or factory methods. Lombok can be used for getters/equals/hashCode/toString and builders, but validation must still execute.

Consequences / Trade-offs
-------------------------
- Positive: domain invariants are centralized and enforced; systems are more robust and easier to debug.
- Negative: additional validation code and tests; potential duplication of similar checks (mitigated by domain helper utilities).
- Risk: If using Lombok-generated constructors blindly, validation may be bypassed. Use factories or explicit constructors when validation is necessary.
