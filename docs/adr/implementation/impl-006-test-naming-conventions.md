# ADR Impl-006 - Use DisplayName Annotation for Test Descriptions

## Status

Accepted

## Context

We maintain multiple test types across the project (unit, integration, and architecture tests). Currently, there is no standardized approach to documenting test
cases. As projects grow, unclear test names become harder to interpret when identifying failures. Our tests should prioritize human-readable descriptions that
explain *what* the test is verifying, rather than how the test method is named.

## Decision

We adopt the following convention for test cases across the codebase:

- Use `@Test` (JUnit 5) as the standard annotation for test cases
- Use `@DisplayName("...")` to specify a clear, expressive description of what the test verifies
- Avoid encoding test rules in long method names (e.g., `shouldNotAllowControllerToDependOnPersistenceWhenUsingMvc()`)
- All test classes will use this convention unless the test framework does not support annotations (e.g., legacy Spock or Spock-like tests)

## Rationale

- `@DisplayName` allows for more natural, business-friendly descriptions
- Keeps test method names neutral (e.g., `domainModelMustNotDependOnOtherPackages`) while letting the display name be expressive
- Consistent approach across all test types (unit, integration, architecture)
- Better alignment with test runners and IDE displays that support display names
- Complements test structure by separating technical identifiers (method names) from human-readable intent (display names)

## Consequences

- New contributors must know to look at `@DisplayName` for actual test intent
- CI logs will reflect display names (e.g., "FAILED com.archetype.layer.DomainModelTests.domainModelMustNotDependOnOtherPackages" becomes "FAILED Domain models
  must not depend on any other project package")
- Easier to refactor test method names without changing display names
- More verbose assertion error messages aligned with display names

## Examples

### Good - Using @Test and @DisplayName

```java
@Test
@DisplayName("Domain models must not depend on any other project package")
void domainModelMustNotDependOnOtherPackages() {
    ArchRule rule = classes().that().resideInAPackage("com.archetype.layer.domain.model..")
            .should().onlyDependOnClassesThat().resideInAPackage("..com.archetype.layer.domain.model..")
            .orShould().resideInAPackage("..junit..")
            .orShould().resideInAPackage("java..")
            .orShould().resideInAPackage("kotlin..");

    rule.check(importedClasses);
}
```

### Bad - Encoding rule in method name

```java
@Test
void domainModelMustNotDependOnAnyOtherProjectPackageBecauseTheyArePureDomainEntities() {
    // implementation here
}
