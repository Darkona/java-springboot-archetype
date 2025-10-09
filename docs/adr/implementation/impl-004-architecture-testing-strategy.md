# ADR Impl-004 - Architecture Testing Strategy for Layered Module

## Status

Accepted

## Context

The project contains multiple architectural patterns (layered, hexagonal, onion, MVC) that require comprehensive validation to ensure architectural boundaries
are maintained. The current architecture tests for the layered module provide basic dependency validation but lack comprehensive coverage of:

- Component role enforcement (controllers, services, repositories)
- Data flow validation (DTOs, domain models, mappers)
- Naming convention compliance
- Annotation requirements
- Internal vs. external dependency management

As this is an archetype for generating new projects, architecture tests must be flexible enough to allow external dependencies while strictly enforcing internal
architectural rules.

## Decision

We adopt a comprehensive architecture testing strategy focused on **smart internal dependency validation** with the following principles:

### Core Principles

1. **Internal-Only Validation**: Architecture rules only apply to dependencies within `com.archetype.layer.*` → `com.archetype.layer.*`
2. **External Dependency Freedom**: Allow unrestricted dependencies on external libraries, frameworks, and other modules
3. **Component Role Enforcement**: Validate that components follow naming conventions and annotation requirements
4. **Data Flow Validation**: Ensure proper DTO/Domain model boundaries and mapper responsibilities
5. **Pattern-Based Rules**: Use naming conventions and annotations rather than explicit package allowlists

### Test Structure

- **Method-Based Testing**: Follow ADR 0013 using `@Test` + `@DisplayName` pattern
- **Modular Test Classes**: Separate concerns into focused test classes
- **Reusable Conditions**: Custom ArchConditions for common validation patterns
- **Archetype-Friendly**: Easy to customize when generating new projects

## Rationale

- **Maintainability**: No need to update architecture tests when adding new external dependencies
- **Flexibility**: Rules adapt to actual module structure without breaking when libraries change
- **Clarity**: Separate test classes for different architectural concerns improve readability
- **Consistency**: Follows established testing patterns from ADR 0013
- **Archetype Compatibility**: Rules are generic enough for project generation while being specific enough for validation

## Consequences

### Positive

- Architecture tests focus on what matters: internal module structure
- No maintenance overhead when adding new external dependencies
- Clear separation of architectural concerns in test classes
- Consistent with project testing standards
- Easy to extend for new architectural patterns

### Potential Risks

- Requires understanding of which dependencies are "internal" vs "external"
- Custom ArchConditions need good documentation
- New team members must understand the internal-only validation approach

## Implementation

### Test Class Structure

```
src/architectureTest/java/com/archetype/
├── LayerArchitectureTests.java      // Enhanced layer dependency rules
├── LayerComponentTests.java         // Component role and annotation validation
├── LayerDataFlowTests.java          // DTO/Domain model flow validation
└── ArchitectureConditions.java     // Reusable custom conditions
```

### Example Smart Internal Rule

```java
@Test
@DisplayName("Persistence layer should not depend on upper layers within the module")
void persistence_should_not_depend_on_upper_layers() {
    noClasses()
        .that().resideInAPackage("..layer.persistence..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "..layer.service..", 
            "..layer.controller.."
        )
        .andShould().onlyAccessClassesThat(
            areNotInternalUpperLayers() // Custom condition
        )
        .check(classes);
}
```

### External Dependency Approach

- Allow any dependency outside `com.archetype.layer.*`
- Focus validation only on internal module relationships
- Use custom conditions to filter out external dependencies from validation

## Examples

### Good - Smart Internal Validation

```java

// Only validates internal layer dependencies, ignores Spring Framework, etc.
noClasses()
    .that().resideInAPackage("..layer.persistence..")
    .should().dependOnClassesThat().resideInAnyPackage(
        "..layer.service..", "..layer.controller.."
    )
    .check(classes);
```

### Bad - Restrictive External Dependency Lists

```java
// Maintenance nightmare - breaks when adding new libraries
noClasses()
    .that().resideInAPackage("..layer.persistence..")
    .should().onlyDependOnClassesThat().resideInAnyPackage(
        "..layer.domain..", "..layer.persistence..", 
        "org.springframework..", "javax.persistence..", 
        "java.util..", "org.slf4j..", // ... endless list
    )
    .check(classes);
```