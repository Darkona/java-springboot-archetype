# ADR pattern-002 — Preferred project/package layout for Hexagonal Architecture (Ports & Adapters)

Date: 2025-10-09

Status: Accepted

Decision
--------
Adopt the Hexagonal Architecture (Ports & Adapters) package layout as the canonical organization for modules that require clear boundaries between the domain core and external concerns (UI, persistence, messaging, external APIs). Structure packages to make ports (interfaces) and adapters (implementations) explicit, keep the domain model free of framework and persistence annotations, and prefer dependency inversion so outer layers depend on abstractions defined by the application/domain layer.

Context
-------
This repository demonstrates several architectural styles. For modules where we need explicit adapter boundaries (for testability, swap-ability of infrastructure, or to emphasize domain primary logic), Hexagonal/Ports & Adapters provides a clear and well-understood pattern. The project already contains an example module under `src/main/java/com/archetype/hexagonal` which this ADR uses as canonical guidance.

Rationale
---------
- Explicit ports make dependencies and intent visible: it is easy to see "what the domain needs" vs "how the outside world satisfies it".
- Adapters are isolated and therefore easier to test, replace, or mock.
- Keeping the domain pure reduces coupling to frameworks and persistence technologies and improves portability and maintainability.
- The layout works well for both synchronous (HTTP) and asynchronous (messaging) adapters.
- This style maps cleanly to automated integration tests and to contract tests between ports and adapters.

Package layout (canonical)
--------------------------
Base package example: `com.archetype.<module>.hexagonal` or `com.archetype.hexagonal.<submodule>`

Preferred top-level package structure under the module:

- `adapter`
  - `adapter.in` — inbound adapters (e.g., HTTP controllers, message listeners, CLI entry points)
    - `adapter.in.http` — Spring MVC / WebFlux controllers or REST adapters
    - `adapter.in.messaging` — message listener/consumer adapters (e.g., Kafka, Rabbit)
  - `adapter.out` — outbound adapters (e.g., persistence, external HTTP clients, caches)
    - `adapter.out.persistence` — repositories, DAOs, persistence mappers (Mongo `Document`, JPA `Entity` kept here)
    - `adapter.out.client` — HTTP clients for calling external services
    - `adapter.out.cache` — cache adapters
- `application`
  - `application.port` — port interfaces used by the application layer (both inbound and outbound ports)
    - `application.port.in` — inbound use-case ports (e.g., `CreatePetPort`, `ListPetsPort`) — these are interfaces the adapters.in call
    - `application.port.out` — outbound ports (e.g., `PetRepositoryPort`, `PaymentClientPort`) — these are interfaces adapters.out implement
  - `application.service` — use-case implementations that implement inbound ports and orchestrate domain operations (sometimes called Application Services)
- `domain`
  - `domain.model` — domain entities, value objects, domain exceptions, domain services (pure POJOs, no framework annotations)
  - `domain.policy` (optional) — domain rules, policies or strategy implementations that are domain-focused
- `config` — configuration classes (wiring adapters to ports, bean definitions)
- `mapper` — mappers between domain and persistence/event/DTO representations (or keep mappers under `adapter.*` if they are adapter-local)
- `pubsub` or `events` — (optional) event payloads if the module publishes domain events; keep event payloads decoupled from domain entities and map explicitly in adapters

Naming conventions
------------------
- Ports (interfaces) name: `*Port` or `*RepositoryPort` or `*ClientPort` as appropriate (e.g., `PetRepositoryPort`, `CreatePetUseCase`).
- Adapters implement ports and should be suffixed with `Adapter`, `Controller`, `Repository`, or `Client` (e.g., `MongoPetRepositoryAdapter`, `PokemonFeignClientAdapter`).
- Application services that implement inbound ports can be named `*Service` or `*UseCase` (e.g., `CreatePetService`).
- Domain classes are plain names (e.g., `Pet`, `PetId`, `PetRepositoryException`).

Examples (based on repository)
------------------------------
The repository contains an illustrative hexagonal module under `src/main/java/com/archetype/hexagonal`. Example package usage:

- `com.archetype.hexagonal.adapter.in.http.PetController` (controller accepts HTTP requests and calls an inbound port)
- `com.archetype.hexagonal.adapter.out.persistence.MongoPetRepositoryAdapter` (implements `application.port.out.PetRepositoryPort`)
- `com.archetype.hexagonal.application.port.in.CreatePetPort` (inbound port)
- `com.archetype.hexagonal.application.port.out.PetRepositoryPort` (outbound persistence port)
- `com.archetype.hexagonal.application.service.CreatePetService` (implements `CreatePetPort` and orchestrates the domain)
- `com.archetype.hexagonal.domain.model.Pet` (pure domain entity)

ASCII tree (canonical)
----------------------
```
src/main/java/com/archetype/hexagonal
├── adapter
│   ├── in
│   │   └── http
│   │       └── PetController.java
│   └── out
│       ├── persistence
│       │   └── MongoPetRepositoryAdapter.java
│       └── client
│           └── ExternalInventoryClientAdapter.java
├── application
│   ├── port
│   │   ├── in
│   │   │   └── CreatePetPort.java
│   │   └── out
│   │       └── PetRepositoryPort.java
│   └── service
│       └── CreatePetService.java
├── domain
│   └── model
│       └── Pet.java
├── config
│   └── HexagonalModuleConfig.java
└── mapper
    └── PetPersistenceMapper.java
```

When to use Hexagonal
---------------------
- When you need to clearly separate domain logic from infrastructure concerns.
- When multiple adapters for the same port are expected (e.g., different persistence strategies or multiple API adapters).
- When testability and contract testing between ports and adapters are important.
- When designing a module as an explicit bounded context with clear inbound/outbound boundaries.

Positions (alternatives considered)
----------------------------------
- Layered architecture (standard controller/service/repository): simpler, but less explicit about adapter boundaries and harder to swap infrastructure without touching domain.
- Onion architecture: similar intent (domain-centered), differences are mostly naming and layering organization — chosen Hexagonal where ports/adapters vocabulary is clearer.
- Clean architecture: essentially aligned with Hexagonal; differences are naming/packaging preferences.

Argument
--------
- Hexagonal explicitly models ports and adapters which reduces accidental coupling of domain to frameworks.
- It maps well to automated tests: unit tests for domain and application services, and contract/integration tests for adapters.
- It supports gradual migration — you can extract ports and adapters incrementally from an existing layered module.

Implications
------------
- Developers must define ports (interfaces) for every external dependency the domain/application requires — this increases upfront design work.
- Slight increase in boilerplate (interfaces and adapters) but yields higher testability and clearer responsibilities.
- Requires discipline to keep domain classes free of framework/persistence annotations.
- Some small modules may become more verbose; apply the repository's "do not create subpackages until >3 classes" rule where appropriate (for tiny modules you may keep adapters in fewer packages).

Implementation / migration steps
-------------------------------
1. When creating a new module, scaffold the `adapter`, `application`, and `domain` packages.
2. Define inbound ports for use-cases and outbound ports for dependencies before implementing adapters.
3. Implement application services that implement inbound ports and orchestrate domain operations.
4. Add adapter implementations for persistence and external clients under `adapter.out`.
5. Keep mappers in `mapper` or adapter-local mapper packages to translate between domain and external representations.
6. Add ArchUnit tests (optional) to enforce dependency rules:
   - Domain packages must not depend on adapter packages.
   - `application.service` may depend on `domain` but adapters must depend on `application.port` interfaces.
7. Refactor existing layered modules incrementally:
   - Extract domain model from controllers/services if annotated with persistence or framework annotations.
   - Introduce ports and adapt current services to implement inbound ports.
   - Introduce adapters that implement the new outbound ports.

Trade-offs & notes
------------------
- Pros: clear separation, testability, swap-able infrastructure, better portability of domain code.
- Cons: more interfaces/indirection and slightly more initial design effort; potential for over-abstraction if applied indiscriminately.
- For small/simple modules, the extra ceremony may not be worth it — prefer simpler layouts in those cases.

Related decisions
-----------------
- ADR pattern-001 — Preferred project/package layout for layered architectures (this ADR complements pattern-001 by prescribing an alternative layout for hexagonal modules).
- ADR arch-001 — Domain separation and mapping (ensures domain purity across patterns).

Related artifacts
-----------------
- Example module: `src/main/java/com/archetype/hexagonal`
- Tests showing ports/adapters in use: `src/test/java/com/archetype/hexagonal/*` (where present)
- `docs/adr/architecture/arch-001-domain-separation-and-mapping.md`

Notes
-----
- Use this ADR as guidance, not a dogma; teams may adapt naming to their conventions but should keep the core principle: domain/core must not depend on infrastructure.
- If an adapter requires significant logic beyond simple mapping, consider creating a small application-level helper or service rather than placing complex logic in the adapter itself.
