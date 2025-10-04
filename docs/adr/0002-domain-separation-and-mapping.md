# ADR 0002 — Domain model separation, DTOs, persistence models and mapping (prefer MapStruct)

Date: 2025-10-02

Status: Accepted

Decision
--------
Always keep domain model classes as clean as possible. Domain model classes should contain domain logic and not be coupled to framework or persistence annotations. Lombok is acceptable for reducing boilerplate (e.g., getters, builders, constructors).

For controllers, always use specialized DTOs (Data Transfer Objects). Controllers must not expose or accept domain model classes directly.

For the data access layer, create dedicated persistence classes that include framework-specific annotations (for example: `@Document` for MongoDB or `@Entity` for JPA/Hibernate). These classes represent how the data is persisted and must be separate from domain model classes.

Always map between layers:
- Controller DTO -> Domain model
- Domain model -> Persistence model
- Persistence model -> Domain model
- Domain model -> Controller DTO

Prefer using MapStruct for mapping whenever practical: it produces compile-time generated mappers, performs well, and keeps mapping code declarative and easy to maintain.

Rationale
---------
- Keeps domain model free of infrastructure/persistence concerns, making it reusable across contexts (batch jobs, services, tests, hexagonal adapters).
- DTOs for controllers allow API evolution without affecting domain logic; DTOs are tailored to transport and validation concerns.
- Persistence classes should reflect storage concerns (indexes, column types, document structure) and therefore belong to persistence layer.
- Explicit mapping enforces boundaries, avoids accidental leakage of persistence details into domain or APIs, and makes transformations explicit and testable.
- MapStruct automates mapping with minimal runtime overhead and better maintainability than handwritten mappers in many cases.

Consequences
------------
Positive:
- Strong separation of concerns.
- Domain model remains resilient and reusable.
- Easier to replace persistence implementations (e.g., switch Mongo -> Relational DB) because only persistence models and mappers change.
- Better testability: domain objects can be constructed and used in isolation.

Negative / Trade-offs:
- More classes and mapping code to maintain.
- Mapping tooling (MapStruct) adds a compile-time dependency and generated sources to the build.
- Small overhead in writing mappers for trivial passthrough fields (mitigated by MapStruct).

Guidelines & Examples
---------------------
1. Domain model (clean, contains logic):
```java
// src/main/java/com/example/domain/model/Pokemon.java
public class Pokemon {
    private final PokemonId id;
    private String name;
    // domain behavior methods here
}
```

2. Controller DTOs (specialized for API):
```java
// src/main/java/com/example/layer/domain/dto/request/PokemonCreate.java
public record PokemonCreate(String name, int nationalId) {}
```

3. Persistence model (storage-specific annotations):
```java
// src/main/java/com/example/layer/persistence/document/PokemonDocument.java
import org.springframework.data.mongodb.core.mapping.Document;

@Document("pokemons")
public class PokemonDocument {
    private UUID id;
    private String name;
    private int nationalId;
    // getters/setters
}
```

4. Mapping flow (use MapStruct when practical):
- Controller DTO -> Domain
- Domain -> Persistence
- Persistence -> Domain
- Domain -> Controller DTO

Example MapStruct mapper (domain <-> persistence):
```java
@Mapper(componentModel = "spring")
public interface PokemonPersistenceMapper {
    Pokemon toDomain(PokemonDocument doc);
    PokemonDocument toDocument(Pokemon domain);
}
```

Example MapStruct mapper (domain <-> controller DTO):
```java
@Mapper(componentModel = "spring")
public interface PokemonApiMapper {
    Pokemon toDomain(PokemonCreate dto);
    PokemonDetails toDetailsDto(Pokemon domain);
}
```

Implementation / Migration Steps
------------------------------
1. Add this ADR to docs/adr.
2. Ensure domain model classes do not carry persistence or framework annotations. If they do, create corresponding persistence classes and remove persistence annotations from domain classes.
3. Ensure controllers use DTOs exclusively. If controllers currently use domain or persistence classes, introduce DTOs and add mappers.
4. Implement mappers:
   - Prefer MapStruct with `componentModel = "spring"` where practical.
   - For trivial mappings, MapStruct reduces boilerplate; for complex mappings, implement custom mapping methods.
5. Add unit tests for mappers to ensure mapping correctness.
6. Update project build configuration to include MapStruct annotation processing if not already enabled.
7. Optionally add a linting/ArchUnit test that enforces controllers do not directly reference persistence annotations or persistence models.

Current project status (at time of ADR creation)
------------------------------------------------
- The project already has:
  - Controller DTOs in `src/main/java/com/skeletor/layer/domain/dto/request` and `response`.
  - A persistence document `src/main/java/com/skeletor/layer/persistence/document/PokemonDocument.java`.
  - A mapper at `src/main/java/com/skeletor/layer/domain/dto/mapper/PokemonMapper.java`.
- Based on a quick scan, the project appears to follow the separation rules in core places (controllers accept/return DTOs, persistence classes are separate). A follow-up scan can validate full compliance and identify any files that need to be refactored.

Notes
-----
- MapStruct requires annotation processing. Ensure `mapstruct` and `mapstruct-processor` are added to build and annotation processing is enabled.
- When MapStruct is not practical (e.g., extremely custom mapping), write a hand-coded mapper and document why.
