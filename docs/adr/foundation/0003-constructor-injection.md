# ADR 0001 — Prefer constructor injection (Lombok @RequiredArgsConstructor) over field injection (@Autowired)

Date: 2025-10-02

Status: Accepted

Context
-------
The project contains multiple modules and styles across different architectures (hexagonal, onion, modulith, etc). Dependency injection is a core pattern used
throughout the codebase. There are two common approaches to injection in Spring:

- Field injection using @Autowired on fields (or using no annotation with reflection-based tools).
- Constructor injection, either by hand-coding constructors or by using Lombok's @RequiredArgsConstructor to generate constructors automatically.

Problem
-------
Field injection using @Autowired has several drawbacks:

- It hides dependencies (not visible from the class API), making the class harder to reason about or instantiate in tests.
- It prevents marking dependencies as final which increases the likelihood of mutable state and accidental reassignment.
- It complicates writing robust unit tests without Spring (constructor injection supports plain object construction).
- It is less compatible with immutable design patterns and best practices for DI.

Decision
--------
We will prefer constructor injection across the project. When Lombok is available in a module, use Lombok's @RequiredArgsConstructor on the class and declare
injected dependencies as final fields. If Lombok is not used in a module, add an explicit constructor that accepts required dependencies.

We will avoid using @Autowired on fields. Use @Autowired only in exceptional scenarios where constructor injection is infeasible and document the reason.

Rationale
---------

- Constructor injection makes required dependencies explicit in the class API.
- Declaring dependencies final improves immutability and intent.
- Classes become easier to test without Spring: simply new Class(dep1, dep2).
- Lombok reduces boilerplate while preserving constructor semantics via @RequiredArgsConstructor.
- Constructor injection is the recommended practice in the Spring community and in many style guides.

Consequences
------------
Positive:

- Clearer and safer code.
- Easier unit testing and fewer surprises at runtime.
- Fewer accidental nulls or partially-initialized beans.

Negative:

- Slight increase in constructor parameters for classes with many dependencies — consider creating facades/adapters or grouping dependencies where appropriate.

Examples
--------
Before (field injection):

```java

@Component
public class FooService {
    @Autowired
    private BarRepository barRepository;

    public void doThing() {
        //implementation
    }
}
```

After (Lombok + constructor injection):

```java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FooService {
    private final BarRepository barRepository;

    public void doThing() {
        //implementation
    }
}
```

After (explicit constructor — no Lombok):

```java

@Component
public class FooService {
    private final BarRepository barRepository;

    public FooService(BarRepository barRepository) {
        this.barRepository = Objects.requireNonNull(barRepository);
    }

    public void doThing() {
        //implementation
    }
}
```

Implementation notes / Migration steps
------------------------------------

1. Add this ADR to docs/adr.
2. Search the codebase for `@Autowired` and field injection. Replace them with:
    - `@RequiredArgsConstructor` + `private final` fields, or
    - an explicit constructor assigning `final` fields when Lombok cannot be used.
3. Ensure Lombok is available (it already is in modules that use lombok imports). If a module doesn't include Lombok, prefer explicit constructors.
4. Run `./gradlew build` to catch compile errors after the changes.
5. Update any contributing documentation or templates to mention this preference.

Notes
-----

- This ADR advocates for constructor injection as the default. Exceptions must be documented and approved.
