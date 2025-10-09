## ADR found-004 — Use Gradle Version Catalog (TOML) for Centralized Dependency Management

**Date:** 2025-10-03

**Status:** Accepted

### Context

Managing dependencies across multiple Gradle build files (main build.gradle, test.gradle, integrationTest.gradle, etc.) can become inconsistent and error-prone:

- Version numbers scattered across multiple files
- Risk of version conflicts between test types
- Difficult to see all project dependencies at a glance
- No IDE autocomplete for dependency declarations
- Prone to typos in dependency coordinates
- Hard to maintain consistent versions across modules

Gradle introduced Version Catalogs (TOML format) as a modern solution for centralized dependency management that provides type-safe accessors and better IDE support.

### Decision

We will use Gradle's Version Catalog feature with a `gradle/libs.versions.toml` file to manage all project dependencies, versions, and plugins centrally.

All Gradle build files (`build.gradle`, `gradle/*.gradle`) must reference dependencies through the version catalog using the `libs.*` accessor.

### Structure

The version catalog is organized in `gradle/libs.versions.toml` with three main sections:

### 1. **[versions]** - All version numbers

Organized by category:

- Java & Gradle
- Spring Framework
- Security & OAuth
- Data & Persistence
- Code Generation & Processing
- Testing
- Observability & Monitoring
- API Documentation
- Agents
- Plugins
- Build Tools

### 2. **[libraries]** - Dependency declarations

Organized by category with clear comments:

- Spring Boot Starters
- Spring Security
- Spring Cloud
- Spring Modulith
- Data & Persistence
- Code Generation
- API Documentation
- Observability & Monitoring
- Testing frameworks (Core, ArchUnit, Testcontainers, Spock)
- Mutation Testing
- Agents

### 3. **[plugins]** - Plugin declarations

All Gradle plugins with versions.

### Usage Examples

#### In build.gradle:

```gradle
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation libs.spring.boot.starter.web
    implementation libs.lombok
    testImplementation libs.junit.platform.launcher
}

wrapper {
    gradleVersion = libs.versions.gradle.get()
}
```

#### In test configuration files:

```gradle
dependencies {
    testImplementation libs.spring.boot.starter.test
    testImplementation libs.archunit.junit5
}

java {
    compileTestJava {
        options.release = libs.versions.java.get().toInteger()
    }
}
```

### Benefits

**Centralized Management:**

- All versions in one location
- Single source of truth for dependencies
- Easy to update versions project-wide

**Type Safety:**

- IDE autocomplete for `libs.*` references
- Compile-time validation of dependency references
- Reduces typos and errors

**Better Maintainability:**

- Clear dependency organization with categories
- Easy to see all project dependencies
- Consistent naming conventions

**Consistency:**

- Same dependency referenced identically everywhere
- No version conflicts between modules
- Guaranteed version alignment

**IDE Support:**

- IntelliJ IDEA provides autocomplete for catalog entries
- Quick navigation to version definitions
- Inline documentation

### Rules

1. **All new dependencies** must be added to `gradle/libs.versions.toml` first.
2. **Never hardcode versions** in build.gradle files.
3. **Use descriptive names** for library aliases (e.g., `spring.boot.starter.web`, not `sb-web`).
4. **Organize by category** with clear section comments.
5. **No bundles** - prefer individual library declarations for maximum flexibility.
6. **Document unusual versions** with inline comments if needed.

### Migration Notes

When adding dependencies:

1. Add version to `[versions]` section with descriptive key.
2. Add library to `[libraries]` section with `module` and optional `version.ref`.
3. Reference in build files as `libs.category.name`.

When updating versions:

1. Update single version number in `[versions]` section.
2. Changes automatically propagate to all references.

### Naming Conventions

**Versions:** Use kebab-case

- `spring-boot = "3.5.5"`
- `opentelemetry-bom = "2.20.1"`

**Libraries:** Use dot notation matching package/module structure

- `spring.boot.starter.web`
- `testcontainers.junit.jupiter`
- `archunit.junit5`

**Plugins:** Use dot notation matching plugin ID

- `spring.boot`
- `spring.dependency.management`

### Consequences

**Positive:**

- Dramatically improved maintainability
- Reduced version conflicts
- Better IDE experience
- Easier dependency audits
- Type-safe dependency references
- Clear project dependency overview

**Negative:**

- Initial migration effort (one-time cost)
- Slight learning curve for developers unfamiliar with version catalogs
- Need to maintain TOML file organization

**Neutral:**

- Additional file to maintain (`gradle/libs.versions.toml`)
- Build files slightly more verbose with `libs.` prefix

### Implementation Status

✅ **Completed:**

- Created `gradle/libs.versions.toml` with all project dependencies.
- Migrated `build.gradle` to use version catalog.
- Migrated `gradle/test.gradle` to use version catalog.
- Migrated `gradle/integrationTest.gradle` to use version catalog.
- Migrated `gradle/architectureTest.gradle` to use version catalog.
- Migrated `gradle/spockTest.gradle` to use version catalog.
- All builds verified to work with version catalog.

### References

- [Gradle Version Catalogs Documentation](https://docs.gradle.org/current/userguide/platforms.html)
- [Version Catalogs Best Practices](https://docs.gradle.org/current/userguide/platforms.html#sub:conventional-dependencies-toml)

## Notes

- This ADR establishes the standard for all future projects.
- Version catalog is a Gradle 7.0+ feature (we're using Gradle 9.1.0).
- TOML format is widely supported with excellent tooling.
- Future: Consider adding dependency update automation tools (e.g., Renovate, Dependabot).
