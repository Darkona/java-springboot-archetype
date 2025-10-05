# Architectural Decision Records (ADRs)

This directory contains all architectural decisions for the Java Spring Boot archetype, organized by theme with independent numbering per category.

## Directory Structure

### 📁 **foundation/** - Core Standards
Fundamental standards that affect all code regardless of architecture:

- [0001-build-and-dependency-standards](foundation/0001-build-and-dependency-standards.md) - Gradle, Java 21, dependency policy
- [0002-java-21-language-features](foundation/0002-java-21-language-features.md) - Records, switch expressions, modern patterns
- [0003-constructor-injection](foundation/0003-constructor-injection.md) - Dependency injection standards
- [0004-gradle-version-catalog](foundation/0004-gradle-version-catalog.md) - Version management approach

### 🏗️ **architecture/** - Domain Design & Cross-cutting Patterns  
Architectural patterns that apply across different implementation styles:

- [0001-domain-separation-and-mapping](architecture/0001-domain-separation-and-mapping.md) - Layer separation with MapStruct
- [0002-domain-validation-strategy](architecture/0002-domain-validation-strategy.md) - Validation strategies and error handling
- [0003-testing-strategy-and-tdd](architecture/0003-testing-strategy-and-tdd.md) - Testing pyramid and TDD practices
- [0004-openfeign-http-clients](architecture/0004-openfeign-http-clients.md) - HTTP client integration standards
- [0005-spring-annotations-over-responseentity](architecture/0005-spring-annotations-over-responseentity.md) - Controller design patterns
- [0006-exception-handling-strategy](architecture/0006-exception-handling-strategy.md) - RFC 9457 compliant error responses

### ⚙️ **implementation/** - Tool Configurations & Specific Conventions
Implementation details and tool-specific configurations:

- [0001-logging-standards-and-obfuscation](implementation/0001-logging-standards-and-obfuscation.md) - Structured logging with obfuscation
- [0002-observability-with-opentelemetry](implementation/0002-observability-with-opentelemetry.md) - OpenTelemetry integration
- [0003-openapi-documentation-standards](implementation/0003-openapi-documentation-standards.md) - API documentation standards
- [0004-architecture-testing-strategy](implementation/0004-architecture-testing-strategy.md) - ArchUnit testing strategy
- [0005-aspectj-weaving-configuration](implementation/0005-aspectj-weaving-configuration.md) - AspectJ for cross-cutting concerns
- [0006-test-naming-conventions](implementation/0006-test-naming-conventions.md) - Readable test names

### 🎯 **patterns/** - Architecture-Specific Guidance
Guidance specific to particular architectural patterns:

- [0001-layered-architecture-package-structure](patterns/0001-layered-architecture-package-structure.md) - Layered architecture package organization

## Navigation Tips

### For AI Agents
- **Core standards**: Start with `foundation/` for fundamental patterns
- **Domain design**: Check `architecture/` for cross-cutting design decisions
- **Tool setup**: Reference `implementation/` for specific configurations
- **Pattern specifics**: Use `patterns/` for architecture-specific guidance

### For Humans
- **New to the archetype?** Start with `foundation/0001` (build standards)
- **Designing domain logic?** Focus on `architecture/` directory
- **Setting up tools?** Browse `implementation/` directory
- **Using specific architecture?** Check `patterns/` directory

## Benefits of This Structure

1. **Logical Organization**: Each directory tells a complete story
2. **Independent Numbering**: New ADRs don't affect existing ones
3. **AI-Friendly**: Clear topic boundaries and priorities
4. **Scalable**: Each theme can grow independently
5. **Self-Documenting**: Directory names indicate content scope

## Contributing

When adding new ADRs:
1. Choose the appropriate theme directory
2. Use the next available number in that directory
3. Follow the naming convention: `NNNN-descriptive-title.md`
4. Update this README with the new ADR
5. Ensure cross-references use relative paths

## Legacy References

This structure was migrated from a flat numbering system. All content is preserved - only organization and numbering have changed for better discoverability and maintainability.
