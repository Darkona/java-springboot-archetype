## ADR Arch-001 Domain Separation And Mapping

**Date:** 2025-10-02

**Status:** Accepted

### Decision
Always keep domain model classes as clean as possible. Domain model classes should contain domain logic and not be coupled to framework or persistence annotations. Lombok may be used to reduce boilerplate (e.g., getters, builders, constructors).

For controllers, always use specialized DTOs (Data Transfer Objects). Controllers must not expose or accept domain model classes directly.

For the data access layer, create dedicated persistence classes that include framework-specific annotations (for example: `@Document` for MongoDB or `@Entity` for JPA/Hibernate). These classes represent how the data is persisted and must be separate from domain model classes.

Always ensure mapping between layers:

- **Controller DTO** → Domain model
- **Domain model** → Persistence model
- **Persistence model** → Domain model
- **Domain model** → Controller DTO

Prefer using **MapStruct** for mapping whenever practical, as it produces compile-time generated mappers, performs well, and maintains mapping code declarative and easy to manage.

### Rationale
- Keeps the domain model free of infrastructure/persistence concerns, making it reusable across contexts (batch jobs, services, tests, hexagonal adapters).
- DTOs for controllers allow API evolution without affecting domain logic; DTOs are tailored for transport and validation concerns.
- Persistence classes should reflect storage concerns (indexes, column types, document structure) and thus belong to the persistence layer.
- Explicit mapping enforces boundaries, avoiding accidental leakage of persistence details into domain or APIs, and keeps transformations explicit and testable.
- MapStruct automates mapping with minimal runtime overhead and better maintainability than handwritten mappers in many cases.

### Consequences
**Positive:**
- Strong separation of concerns.
- Domain model remains resilient and reusable.
- Easier to replace persistence implementations (e.g., switching from MongoDB to a relational DB) since only persistence models and mappers change.
- Improved testability, as domain objects can be constructed and used in isolation.

**Negative / Trade-offs:**
- More classes and mapping code to maintain.
- Mapping tooling (MapStruct) adds a compile-time dependency and generated sources to the build.
- Minimal overhead in writing mappers for trivial passthrough fields (mitigated by MapStruct).

### Guidelines & Examples

#### Mapper Package Organization
All mappers should be organized under a centralized `mapper` package with subpackages by mapping concern:

```
src/main/java/com/archetype/layer/mapper/
├── dto/
│   └── PokemonMapper.java              // DTO ↔ Domain Model
├── persistence/
│   └── PokemonPersistenceMapper.java   // Domain Model ↔ Persistence Document
└── client/
    └── PokeApiDtoMapper.java   // External Service DTO ↔ Domain Model
```

**Benefits of centralized mapper organization:**
- Centralizes all mapping logic in one location for easy discovery.
- Simplifies architectural test rules (no exceptions needed).
- Makes mapper discovery and maintenance easier while ensuring a clear separation between DTO and persistence mapping concerns.

### Mapper Package Rules:
- `*.mapper.dto.*` - DTO ↔ Domain Model mapping only
- `*.mapper.persistence.*` - Domain Model ↔ Persistence Document mapping only
- Each mapper should possess a single, well-defined mapping responsibility.

1. **Domain Model (clean, contains logic):**
```java
// src/main/java/com/example/domain/model/Pokemon.java
public class Pokemon {
    private final PokemonId id;
    private String name;
    // domain behavior methods here
}
```

2. **Controller DTOs (specialized for API):**
```java
// src/main/java/com/example/layer/domain/dto/request/PokemonCreate.java
public record PokemonCreate(String name, int nationalId) {
}
```

3. **Persistence Model (storage-specific annotations):**
```java
// src/main/java/com/example/layer/persistence/document/PokemonDocument.java
import org.springframework.data.mongodb.core.mapping.Document;

@Document("pokemons")
public class PokemonDocument {
    private UUID id;
    private String name;
    private int nationalId;
    // getters and setters
}
```

4. **Mapping Flow (use MapStruct when practical):**
- Controller DTO → Domain
- Domain → Persistence
- Persistence → Domain
- Domain → Controller DTO

**Example of a MapStruct Mapper (domain ↔ persistence):**
```java
// src/main/java/com/example/layer/mapper/persistence/PokemonPersistenceMapper.java
@Mapper(componentModel = "spring")
public interface PokemonPersistenceMapper {
    Pokemon toDomain(PokemonDocument doc);
    PokemonDocument toDocument(Pokemon domain);
}
```

**Example of a MapStruct Mapper (domain ↔ controller DTO):**
```java
// src/main/java/com/example/layer/mapper/dto/PokemonMapper.java
@Mapper(componentModel = "spring")
public interface PokemonMapper {
    Pokemon toDomain(PokemonCreate dto);
    PokemonDetails toDetailsDto(Pokemon domain);
}
```

### Implementation / Migration Steps
1. Add this ADR to `docs/adr`.
2. Ensure domain model classes do not carry persistence or framework annotations. If they do, create corresponding persistence classes and remove persistence annotations from domain classes.
3. Ensure controllers use DTOs exclusively. If controllers currently use domain or persistence classes, introduce DTOs and add mappers.
4. Implement mappers:
    - Prefer MapStruct with `componentModel = "spring"` where practical.
    - For trivial mappings, MapStruct reduces boilerplate; for complex mappings, implement custom mapping methods.
5. Add unit tests for mappers to ensure mapping correctness.
6. Update project build configuration to include MapStruct annotation processing if not already enabled.
7. Optionally, add a linting/ArchUnit test that enforces controllers do not directly reference persistence annotations or persistence models.

### Current Project Status (at time of ADR creation)
The project already includes:
- Controller DTOs in `src/main/java/com/archetype/layer/domain/dto/request` and `response`.
- A persistence document at `src/main/java/com/archetype/layer/persistence/document/PokemonDocument.java`.
- A mapper at `src/main/java/com/archetype/layer/domain/dto/mapper/PokemonMapper.java`.

### Notes
- MapStruct requires annotation processing. Ensure `mapstruct` and `mapstruct-processor` are added to the build, and annotation processing is enabled.
- When MapStruct is unfeasible (e.g., highly custom mappings), write a hand-coded mapper and document the reason why.
