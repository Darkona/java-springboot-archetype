# ADR pattern-003 — Preferred project/package layout for Onion Architecture

Date: 2025-10-09

Status: Accepted

Decision
--------
Adopt the Onion Architecture package layout for modules where the domain model must be the central, most-stable part of the codebase and outer layers (application, infrastructure, presentation) must depend inward only. Structure packages so that the core domain and its policies sit at the center; application services, ports and interfaces sit in the next ring; and infrastructure and presentation live at the outermost ring. Maintain dependency inversion by keeping interfaces on the inside and implementations on the outside.

Context
-------
Onion Architecture emphasizes a domain-centered design: the domain (entities, value objects, domain services) is the most important element and must not depend on infrastructure, frameworks or UI. This repository includes an `onion` module under `src/main/java/com/archetype/onion` which demonstrates these principles and is used here as canonical guidance.

Rationale
---------
- Centers the domain model, improving conceptual clarity and making business rules easier to reason about.
- Enforces a strong dependency rule: outer layers may depend on inner layers but inner layers must not depend on outer ones.
- Facilitates unit testing of domain logic without loading infrastructure/framework code.
- Makes it straightforward to replace infrastructure (persistence, external APIs, UI) without changing domain code.

Package layout (canonical)
--------------------------
Base package example: `com.archetype.<module>.onion` or `com.archetype.onion.<submodule>`

Preferred top-level package structure under the module:

- `domain`
  - `domain.model` — entities, value objects, domain exceptions, domain-level policies and domain services (pure POJOs)
  - `domain.service` — rich domain services containing domain logic that doesn't naturally belong to an entity
- `application`
  - `application.port` — interfaces (ports) used by application services and implemented by outer layers
    - `application.port.in` — inbound ports (use-case interfaces) called by presentation/adapters
    - `application.port.out` — outbound ports (repository, external client interfaces) implemented by infrastructure
  - `application.service` — application/use-case services that coordinate domain operations and orchestrate transactions
- `infrastructure`
  - `infrastructure.persistence` — persistence implementations (repositories, DAOs, persistence models such as `*Entity` or `*Document`)
  - `infrastructure.messaging` — message adapters, event publishers/subscribers
  - `infrastructure.client` — HTTP clients, external service adapters
  - `infrastructure.config` — infrastructure-specific configuration (data sources, client beans, connection properties)
- `presentation`
  - `presentation.web` — controllers, REST endpoints, view controllers (Spring MVC / WebFlux)
  - `presentation.cli` — CLI entry points if any
- `config` — module-level configuration to wire application ports to infrastructure implementations (prefer constructor injection)

Naming conventions
------------------
- Keep domain classes simple and framework free: `Order`, `OrderId`, `PricingPolicy`.
- Ports: `*Port`, `*RepositoryPort`, `*ClientPort` (e.g., `OrderRepositoryPort`).
- Infrastructure implementations: suffix with `Repository`, `Adapter`, `Client` (e.g., `JpaOrderRepository`, `KafkaOrderPublisher`).
- Application services: `*Service` or `*UseCase` (e.g., `CreateOrderService`, `CancelOrderUseCase`).

Examples (based on repository)
------------------------------
The repository contains an onion module under `src/main/java/com/archetype/onion`. Example package usage:

- `com.archetype.onion.domain.model.Order` (domain entity)
- `com.archetype.onion.application.port.out.OrderRepositoryPort` (outbound port interface)
- `com.archetype.onion.application.service.CreateOrderService` (implements inbound use-case and orchestrates domain)
- `com.archetype.onion.infrastructure.persistence.JpaOrderRepository` (implements `OrderRepositoryPort`)
- `com.archetype.onion.presentation.web.OrderController` (HTTP controller that calls inbound ports)

ASCII tree (canonical)
----------------------
```
src/main/java/com/archetype/onion
├── domain
│   ├── model
│   │   └── Order.java
│   └── service
│       └── PricingPolicy.java
├── application
│   ├── port
│   │   ├── in
│   │   │   └── CreateOrderPort.java
│   │   └── out
│   │       └── OrderRepositoryPort.java
│   └── service
│       └── CreateOrderService.java
├── infrastructure
│   ├── persistence
│   │   └── JpaOrderRepository.java
│   └── client
│       └── PaymentGatewayClientAdapter.java
├── presentation
│   └── web
│       └── OrderController.java
└── config
    └── OnionModuleConfig.java
```

When to use Onion
-----------------
- When business rules are complex and must be executed independently of transport or persistence concerns.
- When you want the domain to be the most-stable artifact that can be reused across delivery mechanisms (API, batch jobs, CLI).
- When you want to make dependency rules explicit and enforceable by tests (ArchUnit).

Positions (alternatives considered)
----------------------------------
- Layered architecture: easier to start with but often results in domain leakage as frameworks spread into domain classes.
- Hexagonal architecture: similar goals; Onion emphasizes concentric rings and often uses slightly different naming. Either is acceptable — choose the one that best fits team vocabulary and mental model.

Argument
--------
- Onion's concentric model reduces accidental dependencies into domain code and clarifies where to put code.
- It scales cleanly: small features live near the center; integration and infrastructure are clearly separated in outer rings.
- It supports gradual refactoring: extract domain core first, then move services and ports outward as needed.

Implications
------------
- Domain classes must remain framework-free and should not reference infrastructure packages.
- Application ports and domain services become the primary extension points; teams must design ports thoughtfully.
- More initial design work is required to define correct ports and domain services.
- Some modules may feel verbose for small utilities — apply the repository's granularity rule to avoid unnecessary subpackages.

Implementation / migration steps
-------------------------------
1. Start by identifying and extracting domain entities and policies into `domain.*`.
2. Create application ports for interactions that cross the domain boundary.
3. Implement application services that orchestrate domain behavior and implement inbound ports.
4. Move concrete persistence and external service code into `infrastructure.*` and implement outbound ports.
5. Wire ports to implementations in `config` using constructor injection or configuration classes.
6. Add ArchUnit tests (optional) to assert dependency rules:
   - `domain` must not depend on `infrastructure` or `presentation`.
   - `infrastructure` may depend on `application.port` interfaces, not concrete application classes.
7. When migrating from layered modules:
   - Remove framework/persistence annotations from domain classes and introduce persistence models in `infrastructure.persistence`.
   - Introduce ports and change repositories/controllers to depend on ports rather than domain persistence types.

Trade-offs & notes
------------------
- Pros: domain-centric clarity, better testability, safe refactoring boundaries.
- Cons: additional indirection and upfront interface design; possible boilerplate.
- If the module remains small and unlikely to swap infrastructure, prefer a lighter layout.

Related decisions
-----------------
- ADR pattern-001 — Preferred project/package layout for layered architectures.
- ADR pattern-002 — Hexagonal architecture package structure (related approach; differs mainly by naming and emphasis).

Related artifacts
-----------------
- Example module: `src/main/java/com/archetype/onion`
- `docs/adr/architecture/arch-001-domain-separation-and-mapping.md`

Notes
-----
- Use this ADR as a guide — teams may adapt naming conventions, but must preserve the directional dependency rule (outer -> inner only).
- For small modules, consolidate packages to avoid over-engineering: follow the repository rule of creating subpackages only when >3 classes exist.
