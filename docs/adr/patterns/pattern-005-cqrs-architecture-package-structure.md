# ADR pattern-005 — Preferred project/package layout for CQRS (Command Query Responsibility Segregation)

Date: 2025-10-09

Status: Accepted

Decision
--------
Adopt a CQRS package layout for modules that benefit from separating command (write) and query (read) responsibilities. Structure the code so command-side logic (commands, command handlers, domain changes) is separated from query-side logic (read models, query handlers, projections), support clear patterns for synchronizing read models (event-driven projections, eventual consistency), and optionally support event sourcing where appropriate. Keep domain invariants and transactionality on the command side; design read models for efficient queries.

Context
-------
CQRS helps scale read and write workloads independently, simplifies complex read-side projections, and clarifies where consistency boundaries exist. This repository includes an illustrative CQRS structure under `src/main/java/com/archetype/cqrs` which this ADR uses as canonical guidance. The project contains separate `command` and `query` packages that demonstrate the intended split.

Rationale
---------
- Separation of concerns: distinct responsibilities for modifying state and serving queries reduce coupling and simplify design.
- Scalability: read models can be optimized and scaled independently from the write side.
- Optimized data models: read models may be denormalized for query performance without affecting the command model.
- Event-driven projection: using events to populate read models provides a clear audit trail and enables multiple independent read models.
- Event sourcing (optional): when used, the event store becomes the authoritative source for state reconstruction and auditing.

Package layout (canonical)
--------------------------
Base package example: `com.archetype.<module>.cqrs` or `com.archetype.cqrs.<submodule>`

Preferred top-level package structure under the module:

- `command` — write-side concerns
  - `command.api` — command DTOs/requests (e.g., `CreateOrderCommand`, `CancelOrderCommand`)
  - `command.handler` — command handlers that implement transactions and enforce invariants (e.g., `CreateOrderHandler`)
  - `command.domain` — command-side domain model, aggregates, domain events (if not using separate `events` package)
  - `command.infrastructure` — persistence for aggregates (repositories, event stores), transactional configuration
  - `command.service` — application services / use-cases coordinating domain operations
- `query` — read-side concerns
  - `query.api` — query DTOs / request objects (e.g., `GetOrderDetailsQuery`, `SearchOrdersQuery`)
  - `query.handler` — query handlers that read from read-models (e.g., `GetOrderDetailsHandler`)
  - `query.readmodel` — read-model representations optimized for queries (denormalized DTOs)
  - `query.infrastructure` — read-model persistence (read repositories, specialized DB schemas), caching adapters
- `events` — domain/event messages shared between command and query sides and between services
  - `events.model` — event payload classes (e.g., `OrderCreatedEvent`, `OrderCancelledEvent`)
  - `events.publisher` — event publishing components used by the command side
  - `events.subscriber` — generic subscribers for projection wiring (if not implemented in `query` adapters)
- `projection` (optional) — projector classes that consume events and update read models (may live under `query.infrastructure` or `events.subscriber`)
- `config` — wiring configuration (connect command handlers, event publishers, projection subscriptions)
- `mapper` — mappers between domain/aggregate state and read-models, DTOs, or event payloads
- `saga` (optional) — long-running process orchestrators/coordinators (if using sagas for eventual consistency across modules)

Naming conventions
------------------
- Commands: `*Command` (e.g., `CreateOrderCommand`)
- Command handlers: `*Handler` or `*CommandHandler` (e.g., `CreateOrderHandler`)
- Queries: `*Query` (e.g., `GetOrderDetailsQuery`)
- Query handlers: `*Handler` or `*QueryHandler` (e.g., `GetOrderDetailsHandler`)
- Events: `*Event` (e.g., `OrderCreatedEvent`)
- Aggregates/Entities (command-side): domain names without persistence suffixes (e.g., `OrderAggregate`, `OrderId`)
- Read models: `*View`, `*ReadModel`, or `*Dto` (e.g., `OrderDetailsReadModel`)
- Repositories: `*Repository` or `*EventStore` (e.g., `OrderRepository`, `OrderEventStore`)

Examples (based on repository)
------------------------------
Repository examples under `src/main/java/com/archetype/cqrs`:

- Command side:
  - `com.archetype.cqrs.command.api.CreatePokemonCommand`
  - `com.archetype.cqrs.command.handler.CreatePokemonHandler`
  - `com.archetype.cqrs.command.domain.PokemonAggregate`
  - `com.archetype.cqrs.command.infrastructure.PokemonEventStore` (or repository)
- Query side:
  - `com.archetype.cqrs.query.api.GetPokemonDetailsQuery`
  - `com.archetype.cqrs.query.handler.GetPokemonDetailsHandler`
  - `com.archetype.cqrs.query.readmodel.PokemonDetailsReadModel`
  - `com.archetype.cqrs.query.infrastructure.PokemonReadRepository`
- Events & projections:
  - `com.archetype.cqrs.events.model.PokemonCreatedEvent`
  - `com.archetype.cqrs.projection.PokemonProjection` (consumes `PokemonCreatedEvent` and updates read model)

ASCII tree (canonical)
----------------------
```
src/main/java/com/archetype/cqrs
├── command
│   ├── api
│   │   └── CreatePokemonCommand.java
│   ├── handler
│   │   └── CreatePokemonHandler.java
│   ├── domain
│   │   └── PokemonAggregate.java
│   └── infrastructure
│       └── PokemonEventStore.java
├── query
│   ├── api
│   │   └── GetPokemonDetailsQuery.java
│   ├── handler
│   │   └── GetPokemonDetailsHandler.java
│   ├── readmodel
│   │   └── PokemonDetailsReadModel.java
│   └── infrastructure
│       └── PokemonReadRepository.java
├── events
│   ├── model
│   │   └── PokemonCreatedEvent.java
│   └── publisher
│       └── DomainEventPublisher.java
├── projection
│   └── PokemonProjection.java
├── saga
│   └── PokemonSyncSaga.java
└── config
    └── CqrsModuleConfig.java
```

When to use CQRS
----------------
- Systems with heavy read/write workload separation needs or where read models require specialized denormalized schemas for performance.
- When you need to scale read and write paths independently.
- When the domain requires clear auditability and an event-driven model or when multiple read models must be derived from write-side events.
- Use event sourcing only when the business requires a full, append-only event log, auditing, or the ability to rebuild state from events.

Positions (alternatives considered)
----------------------------------
- Single model (read/write combined): simpler for small systems but less flexible for scale and sometimes harder to optimize read queries.
- Layered/Hexagonal/Onion: those patterns focus on dependency direction and separation of concerns; CQRS can be combined with those (e.g., Hexagonal command-side + CQRS query-side).
- Event sourcing: strong auditability and rebuildability but increases system complexity and operational cost.

Argument
--------
- CQRS clarifies responsibilities and allows read models to evolve independently from write-side aggregates, improving query performance and enabling different storage technologies tuned for reads and writes.
- Event-driven projection enables near-real-time synchronization between the command and query models and provides an audit trail.
- CQRS is compatible with Hexagonal/Onion principles: maintain domain purity on the command side and keep adapters to both sides well-defined.

Implications
------------
- Increased complexity: more components (commands, handlers, projections, event publishers) and infrastructure (message brokers, event stores, read-model stores).
- Eventual consistency: read models are often eventually consistent; clients must tolerate or handle stale reads.
- Operational concerns: monitoring, tracing, and dealing with replays, duplicate events, idempotency, and eventual reconciliation.
- Testing: requires testing projections and synchronization flows; contract tests between command events and projection handlers are recommended.
- Schema management: read-model schemas and migrations require separate processes from write-side migrations.

Implementation / migration steps
-------------------------------
1. Identify candidate modules or features where CQRS adds value (complex reads, high query load, multiple read representations).
2. Introduce a command-api and handlers for write operations; keep transactions and domain invariants on the command side.
3. Define domain events emitted by command handlers (or by aggregates) and publish them via a domain event publisher.
4. Implement projections that subscribe to domain events and update read-model stores; choose storage optimized for queries (e.g., document DB, materialized views).
5. Implement query handlers that read from read-model stores and expose efficient query DTOs.
6. Consider idempotency, duplicate handling, and reconciliation strategies for projections and event consumers.
7. If adopting event sourcing:
   - Introduce an event store and migrate aggregate persistence to event appends.
   - Provide projection replays for rebuilding read models.
8. Add tests:
   - Unit tests for command handlers and aggregates.
   - Integration/contract tests that ensure events produced by commands lead to correct read-model updates.
   - End-to-end tests validating eventual consistency SLAs where required.
9. Migrate incrementally:
   - Start with a single feature: implement command side, emit events, and create projection/read model for queries while keeping existing read paths operational.
   - Gradually switch clients to read from new read models once confidence is established.

Trade-offs & notes
------------------
- Pros: scalable read/write separation, optimized queries, auditability, clear separation of responsibilities.
- Cons: increased architectural and operational complexity, eventual consistency semantics, higher testing surface.
- Event sourcing adds benefits for traceability but requires investment in operational tooling and careful modeling of events.

Related decisions
-----------------
- ADR pattern-001 — Preferred project/package layout for layered architectures (contrasting approach).
- ADR pattern-002 — Hexagonal architecture package structure (CQRS command or query sides can be implemented following hexagonal principles).
- ADR arch-001 — Domain separation and mapping (ensures domain purity when emitting events or mapping to read models).

Related artifacts
-----------------
- Example module: `src/main/java/com/archetype/cqrs`
- Command/query examples: `src/main/java/com/archetype/cqrs/command/*` and `src/main/java/com/archetype/cqrs/query/*`
- Projections and events: `src/main/java/com/archetype/cqrs/events/*` and `src/main/java/com/archetype/cqrs/projection/*`

Notes
-----
- Use CQRS where the benefits outweigh the complexity. For many modules, combining patterns (e.g., Hexagonal command-side with CQRS split) yields a pragmatic, maintainable solution.
- Where the repository lacks full implementations for every package, create minimal adapter/projection examples to illustrate intended structure and behavior.
