# ADR 0009 — Controller OpenAPI annotations through interface-based "Info" classes

Date: 2025-10-03

Status: Accepted

Decision
--------
Use springdoc / OpenAPI annotations to document controllers, but keep those annotations out of controller implementation classes. For each controller create a companion interface named `<SomeController>Info` that declares the same methods as the controller and contains the OpenAPI annotations (e.g., `@Operation`, `@ApiResponse`, `@Parameter`, and `@Tag`). Controller implementation classes will implement the corresponding `*Info` interface while remaining focused on request handling and behavior.

This enforces separation of concerns: documentation/contract metadata is co-located with the controller signature but kept out of business-logic code to improve readability and make automated generation of API docs predictable.

Context
-------
Springdoc/OpenAPI annotations are useful to generate API documentation (Swagger UI / OpenAPI JSON). However embedding a large number of informational annotations directly in controller implementation classes clutters business logic and increases maintenance friction. Separating annotations into interfaces (one per controller) yields a clean implementation class while keeping the documentation contract explicit and type-checked by the compiler.

Rationale
---------
- Keeps controller implementations focused and readable.
- Documents remain close to the controller API (same method signatures) and are checked during compilation.
- Easier to reuse or generate documentation metadata programmatically (e.g., from templates or tools).
- Encourages consistent API documentation across modules.

Template and naming
-------------------
For a controller class named:
- `SomeController` (implementation)

Create an interface named:
- `SomeControllerInfo` (in the same package)

Contract:
- `SomeControllerInfo` declares the same public methods as `SomeController`.
- `SomeControllerInfo` contains OpenAPI annotations for each method and may include a `@Tag` at the interface level.
- `SomeController` implements `SomeControllerInfo` and provides the method bodies. Controller implementation may have Spring annotations required for routing (`@RestController`, `@RequestMapping`, etc.). Method-level Spring MVC annotations (`@GetMapping`, `@PostMapping`, etc.) remain on controller method implementations — documentation annotations live in the `*Info` interface.

Example
-------
SomeControllerInfo.java
```java
@Tag(name = "Some API", description = "Operations for something")
public interface SomeControllerInfo {

    @Operation(summary = "Create something", description = "Creates a resource")
    @ApiResponse(responseCode = "201", description = "Created")
    SomeResponse create(@Parameter(description = "Create DTO") SomeCreate req);
}
```

SomeController.java
```java
@RestController
@RequestMapping("/api/some")
public class SomeController implements SomeControllerInfo {

    @PostMapping
    public ResponseEntity<SomeResponse> create(@RequestBody SomeCreate req) {
         // implementation
    }
}
```

Guidelines
----------
- Place the `*Info` interface in the same package as the controller to keep them discoverable.
- Keep only documentation annotations in the `*Info` interface. Do not add implementation logic or default methods there (except for constants or doc-only helpers).
- Keep Spring MVC route annotations (`@GetMapping`, `@PostMapping`, etc.) in the implementation class so routing configuration remains explicit and close to the implementation.
- Use `@Tag` at the `*Info` interface level to group endpoints.
- Keep OpenAPI examples concise; avoid duplicating large request/response examples inside interfaces — prefer referencing shared DTO docs or schema annotations.

Consequences
------------
Positive:
- Cleaner controllers and centralized, consistent API documentation.
- Easier to auto-generate or template documentation interfaces for new controllers.
- Documentation changes can be made without touching business logic (and vice versa).

Negative:
- Slightly more files per controller (controller + controllerInfo).
- Developers must remember to keep signatures in sync (IDE/compiler help makes this straightforward since controllers implement the interface).

Implementation / migration steps
-------------------------------
1. When adding a controller, also add the `*Info` interface alongside it and annotate the interface methods with OpenAPI annotations.
2. Update existing controllers incrementally by extracting OpenAPI annotations to `*Info` interfaces and making the controller implement them.
3. Ensure `springdoc-openapi` is added to the project dependencies (this archetype already includes `org.springdoc:springdoc-openapi-starter-webmvc-ui`).
4. Add a brief note in CONTRIBUTING.md showing the `*Info` pattern and linking to this ADR.

Notes
-----
- This ADR is an advisory/standard for this archetype and all generated projects from it; agents scaffolding projects should follow this pattern when creating controllers and API documentation.
- If an exceptional case requires annotations on implementation (very rare), document the reason in the module ADR.
