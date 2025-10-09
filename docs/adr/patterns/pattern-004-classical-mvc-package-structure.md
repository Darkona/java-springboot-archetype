# ADR pattern-004 — Preferred project/package layout for Classical MVC (Model-View-Controller)

Date: 2025-10-09

Status: Accepted

Decision
--------
Adopt the Classical MVC package layout for modules that are primarily server-rendered web applications or small services where simplicity and direct mapping between controllers, views and models improves developer productivity. Structure packages around `controller`, `model`, `view` and supporting concerns (service, persistence, templates), keeping domain and persistence concerns separated but favoring a straightforward flow: controller -> service -> model -> persistence -> view/template.

Context
-------
Classical MVC is a well-known, pragmatic architecture for web applications where controllers handle HTTP requests, models represent application data and business logic, and views render HTML (or other representations). This repository contains an `mvc` module under `src/main/java/com/archetype/mvc` and server-side templates under `src/main/resources/templates/pokedex` which serve as canonical examples.

Rationale
---------
- Simplicity: MVC is easy to understand and quick to implement for small-to-medium web features.
- Direct mapping: Developers familiar with Spring MVC or similar frameworks can readily locate controllers, models and templates.
- Performance: For server-rendered pages it minimizes client-side complexity and leverages server-side rendering strengths.
- Low ceremony: Less upfront interface/adapter boilerplate compared to Hexagonal or Onion for simple use-cases.

Package layout (canonical)
--------------------------
Base package example: `com.archetype.<module>.mvc` or `com.archetype.mvc.<submodule>`

Preferred top-level package structure under the module:

- `controller` — HTTP controllers, request mapping, input validation, and mapping to service layer DTOs
- `service` — Application/business services that implement use-cases (may call repositories/persistence). Use constructor injection.
- `model` — Domain or view-model classes used by services and views (entities, DTOs, form objects)
- `persistence` — Repositories, DAOs, persistence models (`*Entity`/`*Document`) and persistence mappers
- `view` — Server-side view models, view helpers, and view-specific utilities (if needed)
- `templates` (resources) — Server-side templates (Thymeleaf, Freemarker, etc.) under `src/main/resources/templates`
- `config` — Spring configuration classes and Web MVC configuration
- `exception` — Controllers/advice classes and exception handlers (e.g., `@ControllerAdvice`)

Naming conventions
------------------
- Controllers: `*Controller` (e.g., `PokedexController`, `PokemonController`)
- Services: `*Service` (e.g., `PokedexService`, `PokemonService`)
- Persistence models: `*Entity` or `*Document` depending on persistence technology (e.g., `PokemonEntity`, `PokemonDocument`)
- Templates: organize under logical folders matching controllers (e.g., `templates/pokedex/list.html`, `templates/pokedex/detail.html`)
- View models/DTOs: named to indicate their role (`PokemonView`, `PokemonForm`, `PokemonDto`)

Examples (based on repository)
------------------------------
The repository contains an MVC module and templates which illustrate the layout and conventions:

- `com.archetype.mvc.controller.PokedexController` — Spring controller that handles requests and returns view names
- `com.archetype.mvc.service.PokedexService` — encapsulates business logic for listing and retrieving Pokédex entries
- `com.archetype.mvc.model.Pokemon` — model used by service and views
- `com.archetype.mvc.persistence.PokedexRepository` — persistence repository
- Templates:
  - `src/main/resources/templates/pokedex/list.html`
  - `src/main/resources/templates/pokedex/detail.html`

ASCII tree (canonical)
----------------------
```
src/main/java/com/archetype/mvc
├── controller
│   └── PokedexController.java
├── service
│   └── PokedexService.java
├── model
│   └── Pokemon.java
├── persistence
│   ├── PokemonEntity.java
│   └── PokedexRepository.java
├── view
│   └── PokemonView.java
├── exception
│   └── MvcGlobalExceptionHandler.java
└── config
    └── WebMvcConfig.java

src/main/resources/templates/pokedex
├── list.html
└── detail.html
```

When to use Classical MVC
-------------------------
- Small-to-medium server-rendered web applications where direct mapping between controllers and views simplifies development.
- Projects where SEO and server-side rendering are primary concerns.
- Rapid prototyping or modules with straightforward request/response flows and limited infrastructure variability.

Positions (alternatives considered)
----------------------------------
- Layered architecture (controller/service/repository): overlapping with MVC; MVC emphasizes views and templating for server-rendered apps.
- Hexagonal/Onion: provide stronger separation and testability for complex domains; heavier than necessary for simple UIs.
- SPA + API: for highly interactive UIs prefer a separate front-end rather than server-rendered MVC.

Argument
--------
- MVC reduces friction when implementing server-rendered features and maps directly to Spring MVC templates in this repository.
- It requires less upfront structural overhead than Hexagonal or Onion while keeping reasonable separation between controllers, services and persistence.
- Using MVC for appropriate modules keeps the architecture pragmatic and easier to onboard new contributors.

Implications
------------
- Domain and persistence code may still leak into controllers if discipline is not enforced; use DTOs and view models to avoid this.
- Not ideal for modules that must support multiple adapters or require strong domain isolation; prefer Hexagonal or Onion there.
- Server-rendered modules will have template assets in resources that must be maintained alongside Java code.

Implementation / migration steps
-------------------------------
1. Scaffold `controller`, `service`, `model`, `persistence`, `view`, and `templates` when creating a new MVC module.
2. Keep controllers thin: delegate business logic to services and map service outputs to view models for templates.
3. Use DTOs/form objects for controller inputs and view models for outputs to avoid exposing domain entities directly to views.
4. Place templates under `src/main/resources/templates/<module>` and organize by controller responsibility.
5. Add tests:
   - Controller tests (MockMvc or WebTestClient) for request/response mapping and view resolution.
   - Service unit tests for business logic.
   - Integration tests for template rendering and end-to-end flows if needed.
6. When migrating from other architectures:
   - Map domain entities to view models before rendering templates.
   - If moving from a hexagonal/onion layout to MVC for a small feature, keep domain core separate and reuse services rather than reintroducing framework annotations in domain objects.

Trade-offs & notes
------------------
- Pros: straightforward, low ceremony, quick to implement server-rendered pages.
- Cons: risk of domain fragmentation and controller/business logic mixing if not disciplined.
- Prefer MVC for modules where templates are a core requirement. For modules requiring multiple adapters, favor Hexagonal/Onion.

Related decisions
-----------------
- ADR pattern-001 — Preferred project/package layout for layered architectures.
- ADR pattern-002 — Hexagonal architecture package structure (consider for adapter-heavy modules).
- ADR pattern-003 — Onion architecture package structure (consider for domain-centric modules).

Related artifacts
-----------------
- Example module: `src/main/java/com/archetype/mvc`
- Templates: `src/main/resources/templates/pokedex/*`
- Controller/view examples: `src/main/java/com/archetype/mvc/controller` and `src/main/resources/templates/pokedex`

Notes
-----
- Keep templates small and focused; prefer server-side helpers or fragments for repeated UI patterns.
- Maintain separation by mapping domain entities to view models before exposing them to templates.
