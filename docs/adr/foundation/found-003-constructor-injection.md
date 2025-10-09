## ADR Found-003 — Prefer constructor injection (Lombok @RequiredArgsConstructor) over field injection (@Autowired)


**Date:** 2025-10-02

**Status:** Accepted

### Context
The project encompasses various modules and design styles across different architectures (hexagonal, onion, modulith, etc.). Dependency injection is a vital pattern utilized throughout the codebase. There are two prevalent strategies for injection in Spring:

- Field injection using `@Autowired` on fields (or through reflection-based tools without annotation).
- Constructor injection, through hand-coded constructors or the use of Lombok's `@RequiredArgsConstructor` to generate constructors automatically.

### Problem
Field injection using `@Autowired` presents several disadvantages:

- It conceals dependencies (not visible from the class API), increasing the difficulty of reasoning about or instantiating the class in tests.
- It prevents marking dependencies as final, which enhances the risk of mutable state and accidental reassignment.
- It complicates the creation of robust unit tests without Spring (constructor injection supports plain object instantiation).
- It is less compatible with immutable design patterns and best practices for dependency injection.

### Decision
We will favor constructor injection throughout the project. When Lombok is available in a module, apply Lombok's `@RequiredArgsConstructor` on the class and declare the injected dependencies as final fields. If Lombok is not present, implement an explicit constructor that accepts necessary dependencies.

We will refrain from using `@Autowired` on fields, permitting it only in exceptional scenarios where constructor injection is not feasible, and the reason must be documented.

### Rationale
- **Constructor injection** makes required dependencies explicit in the class API.
- Declaring dependencies as **final** enhances immutability and intent.
- Classes become easier to test without Spring: simply create a new instance using `new Class(dep1, dep2)`.
- Lombok reduces boilerplate while retaining constructor semantics through `@RequiredArgsConstructor`.
- Constructor injection is the recommended practice within the Spring community and in numerous style guides.

### Consequences
**Positive:**
- Clearer and safer code.
- Easier unit testing and fewer surprises during runtime.
- Reduced likelihood of accidental nulls or partially-initialized beans.

**Negative:**
- A slight increase in constructor parameters for classes with many dependencies — consider creating facades/adapters or grouping dependencies where applicable.

### Examples
**Before (field injection):**
```java
@Component
public class FooService {
    @Autowired
    private BarRepository barRepository;

    public void doThing() {
        // implementation
    }
}
```

**After (Lombok + constructor injection):**
```java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FooService {
    private final BarRepository barRepository;

    public void doThing() {
        // implementation
    }
}
```

**After (explicit constructor — no Lombok):**
```java
@Component
public class FooService {
    private final BarRepository barRepository;

    public FooService(BarRepository barRepository) {
        this.barRepository = Objects.requireNonNull(barRepository);
    }

    public void doThing() {
        // implementation
    }
}
```

### Implementation Notes / Migration Steps
1. Add this ADR to `docs/adr`.
2. Search the codebase for `@Autowired` and field injection. Replace them with:
    - `@RequiredArgsConstructor` + `private final` fields, or
    - An explicit constructor assigning `final` fields if Lombok cannot be utilized.
3. Ensure that Lombok is available (it already is in modules that use it). If any module does not include Lombok, prefer explicit constructors.
4. Run `./gradlew build` to catch any compile errors.

### Notes
This ADR advocates for constructor injection as the default strategy. Exceptions must be documented and approved.
