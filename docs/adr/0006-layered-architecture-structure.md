# ADR 0006 — Preferred project/package layout for layered architectures (example: `layer` package)

Date: 2025-10-02

Status: Accepted

Decision
--------
When using a layered architecture, prefer the package/layout style demonstrated by this repository's `layer` module. Use the structure below as the canonical example for organizing controllers, DTOs, services, domain models, persistence, mappers and supporting utilities. If the project is a modulith, keep the same organization and expose an `api` package for public-facing module symbols while configuring the modulith visibility rules.

This ADR documents the canonical package layout, the "when to create a subpackage" rule (only when >3 classes of the same kind exist), and conventions for pub/sub asynchronous packages.

Context
-------
This repository intentionally illustrates multiple architectures and a recommended organization style that balances discoverability, separation of concerns and practical granularity. Different projects may have different needs; this ADR codifies the recommended organization used across the project so contributors and new modules follow the same conventions.

Rationale
---------
- Consistency reduces cognitive load for new contributors and for cross-module reuse.
- Clear separation of DTOs, domain, persistence and mappers prevents accidental leakage of persistence or framework concerns into the domain model.
- Minimal granularity (create subpackages only when necessary) avoids excessive nesting for small modules while keeping larger modules tidy.
- Explicit pub/sub package shapes make event-driven code discoverable and consistent.

Package layout (canonical)
--------------------------
Example base package: `com.example.<module>.layer` or `com.skeletor.layer`

Top-level packages under module `layer`:

- `controller` — Spring MVC / web controllers.
- `service` — Application/business services (use constructor injection).
- `domain.model` — Domain model classes (entities, value objects, domain interfaces).
- `domain.dto`
  - `domain.dto.request` — DTOs used as request bodies.
  - `domain.dto.response` — DTOs used as responses.
  - `domain.dto.mapper` — Mappers for DTO <-> domain conversions (prefer MapStruct).
- `persistence`
  - `persistence.document` or `persistence.entity` — persistence models (e.g., `@Document` for Mongo, `@Entity` for JPA).
  - `persistence.mapper` — Mappers for persistence <-> domain conversions.
  - `persistence.repository` — Spring Data repositories (if many repositories, keep them in `persistence.repository`).
- `repositories` — (alternative) put repository interfaces here if you prefer separation from persistence models. In this project, repositories are inside `persistence`.
- `pubsub` (optional, for pub/sub)
  - `pubsub.events` — Event classes (payloads) and contracts.
  - `pubsub.publisher` — Publisher components.
  - `pubsub.subscriber` — Subscriber components (message handlers).
- `config` — Spring configuration classes (profiles, beans, modules config).
- `clients` — Declarative HTTP clients (Feign interfaces) for calling remote services. Organize clients by external service: `clients.<service>` (for example `com.skeletor.clients.pokemon`). Each service folder should contain:
  - Feign client interfaces (`*Client`).
  - Client-side DTOs (`dto` package) separate from domain/persistence DTOs.
  - Client-specific mappers (`mapper` package) to translate between client DTOs and the application's persistence/domain models.
  - Optional client-specific configuration (e.g., a configuration class for timeouts, decoders, interceptors).
  This keeps remote-contract code isolated, makes it easy to swap implementations or mock clients in tests, and preserves clear adapter boundaries.
- `utils` — small helpers, generators, custom annotations, shared utilities. Keep this small and focused.
- `mapper` — (if you prefer global mappers; otherwise keep mappers under dto/persistence subpackages)
- `api` — (modulith only) Public API package exposed to other modules. Configure modulith visibility to allow other modules to read this package only if intended.

Granularity rule — when to create subpackages
---------------------------------------------
- Create explicit subpackages (like `request`, `response`, `mapper`, `events`, `publisher`, `subscriber`) only when you have more than three classes of that type.
- If a module has <= 3 controller classes, keep them directly in `controller`. Same for DTOs, mappers and repositories.
- This keeps small modules flat and avoids over-organization while enabling tidy structure as modules grow.

Mapping and separation rules (aligned with ADR 0002)
---------------------------------------------------
- Always keep domain model classes framework- and persistence-free.
- Controllers must use DTOs exclusively: controller DTO -> domain model -> persistence model.
- Persistence classes must contain persistence annotations (`@Document`, `@Entity`) and live under `persistence.*`.
- Keep mappers in `domain.dto.mapper` and `persistence.mapper` (or a unified `mapper` package) and prefer MapStruct where practical.

Pub/Sub conventions
-------------------
- If the module uses pub/sub or messaging, create a `pubsub` package with the `events`, `publisher`, and `subscriber` subpackages.
- Only create these explicit subpackages when you have more than three events/publishers/subscribers; otherwise keep event classes in a single `async` package.
- Events are plain payload/DTO classes; do not couple them to domain entities. Translate between domain and event payloads with mappers.

Modulith specifics
------------------
- If the project is a modulith (Spring Modulith), keep the package structure above inside each module.
- Add an `api` package for types meant to be consumed by other modules; configure modulith module visibility rules to expose only the `api` package.
- Keep internal classes in non-api packages so modulith can protect encapsulation.

Examples (based on this repository)
-----------------------------------
- `com.skeletor.layer.controller.PokemonController`
- `com.skeletor.layer.service.PokemonService`
- `com.skeletor.layer.domain.model.Pokemon`
- `com.skeletor.layer.domain.dto.request.PokemonCreate`
- `com.skeletor.layer.domain.dto.mapper.PokemonMapper`
- `com.skeletor.layer.persistence.document.PokemonDocument`
- `com.skeletor.layer.persistence.PokemonRepository` (repositories live under persistence)

Directory tree example
----------------------
A canonical directory/tree example (ASCII) for the `layer` module:

```
src/main/java/com/skeletor/layer
├── controller
│   ├── PokemonController.java
│   └── PokemonControllerInfo.java
├── service
│   └── PokemonService.java
├── domain
│   ├── model
│   │   ├── Pokemon.java
│   │   ├── PokemonId.java
│   │   └── ...
│   └── dto
│       ├── request
│       │   └── PokemonCreate.java
│       ├── response
│       │   ├── PokemonDetails.java
│       │   └── PokemonOverview.java
│       └── mapper
│           └── PokemonMapper.java
├── persistence
│   ├── document
│   │   └── PokemonDocument.java
│   ├── repository
│   │   └── PokemonRepository.java
│   └── mapper
│       └── PokemonPersistenceMapper.java
├── config
│   └── (optional Spring configuration classes)
├── clients
│   ├── pokemon
│   │   ├── dto
│   │   │   ├── PokemonClientCreate.java
│   │   │   └── PokemonClientDetails.java
│   │   ├── mapper
│   │   │   └── PokemonClientMapper.java
│   │   ├── PokemonClient.java
│   │   └── FeignConfiguration.java
│   └── (other service folders follow same pattern: `dto`, `mapper`, `client`, `config`)
├── pubsub
│   ├── events
│   ├── publisher
│   └── subscriber
├── utils
│   └── (helpers, generators, annotations)
└── api
    └── (public types for modulith exposure)
```

Notes:
- Create subpackages (e.g., `request`, `response`, `mapper`, `events`) only when you have more than ~3 classes of that responsibility.
- Keep domain model classes free of persistence/framework annotations; persistence models live under `persistence.*`.
- For modulith modules, add an `api` package to expose only intended types and configure modulith visibility accordingly.
Naming conventions
------------------
- Use `request` and `response` subpackages for DTOs.
- Name mappers `*Mapper` and prefer MapStruct `@Mapper(componentModel = "spring")`.
- Persistence model classes: `*Document` (Mongo) or `*Entity` (JPA) as suffixes to signal intent.
- Domain models: no persistence suffix; use plain names (e.g., `Pokemon`).

Implementation / migration steps
-------------------------------
1. When creating a new module, scaffold the recommended top-level packages.
2. If a module has few classes (<=3) for a responsibility, keep them in the parent package to avoid noise.
3. If refactoring an existing module, follow ADR 0002: extract persistence models and mappers if domain classes currently carry annotations.
4. Add ArchUnit checks (optional) to enforce high-level rules:
   - Controllers should not reference persistence classes.
   - Domain model packages should not depend on persistence packages.
5. Document the structure in CONTRIBUTING.md and module readme templates so new modules follow the pattern.

Trade-offs & notes
------------------
- The "create subpackage only when >3 classes" rule is a practical heuristic — teams may adapt the number if needed.
- This ADR is advisory for consistency across modules and as an example for non-modulith and modulith projects.
- Use judgement: organization should help discoverability, not create rigid bureaucracy.
