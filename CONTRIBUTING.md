CONTRIBUTING — API documentation pattern (controller *Info interfaces)

This project uses an interface-based approach for OpenAPI/springdoc annotations to keep controller implementations free of documentation noise.
Follow this guidance when adding or updating controllers.

Pattern summary
---------------
- For every controller `XController` create a companion interface `XControllerInfo` in the same package.
- `XControllerInfo` declares the same public methods as `XController` and contains OpenAPI annotations (`@Tag`, `@Operation`, `@ApiResponse`, `@Parameter`, etc).
- `XController` implements `XControllerInfo` and retains Spring MVC routing annotations (`@RequestMapping`, `@GetMapping`, `@PostMapping`, `@RequestBody`, `@PathVariable`, etc).
- Keep only documentation annotations inside the `*Info` interface. Do not put business logic or default method implementations there.

Why
---
- Keeps implementation classes readable and focused on behavior.
- Keeps API documentation close to method signatures and type-checked by the compiler.
- Easier to auto-generate or template documentation metadata.

How to create a new controller with an Info interface
-----------------------------------------------------
1. Create `XController.java` with Spring MVC routing annotations and method implementations.
2. Create `XControllerInfo.java` in the same package, declare the same public methods and annotate them with OpenAPI annotations (examples: `@Operation`, `@ApiResponse`, `@Parameter`, `@Tag`).
3. Make `XController` implement `XControllerInfo`.
4. Run `gradlew build` and `gradlew architectureTest` to verify compilation and architecture rules.

Automated helper
----------------
A small helper exists to scaffold a starter `*Info` interface from a compiled controller class:

Tool: `com.skeletor.tools.InfoInterfaceGenerator`

Usage (from project root):
1. Compile the project so controller classes exist under `build/classes/java/main`:
   - Windows: `cmd /c gradlew.bat build`
   - Unix: `./gradlew build`

2. Run the generator:
   - Windows example:
     cmd /c java -cp "build/classes/java/main;build/resources/main" com.skeletor.tools.InfoInterfaceGenerator com.skeletor.layer.controller.PokemonController src/main/java/com/skeletor/layer/controller
   - Unix example:
     ./gradlew -q run --no-daemon --args="com.skeletor.layer.controller.PokemonController src/main/java/com/skeletor/layer/controller"

Notes:
- The generator emits method signatures and placeholder `@Operation/@ApiResponse` annotations; you must review and expand the documentation (parameters, response codes, descriptions).
- The generator uses reflection on the compiled class; ensure the project is compiled first.
- After generation, move the generated interface to the correct source tree if needed and commit.

Example
-------
- `PokemonController.java` — implemented controller (contains routing annotations and logic)
- `PokemonControllerInfo.java` — interface containing OpenAPI annotations and the same method signatures

Review and tests
----------------
- Add or update OpenAPI annotations in `*Info` interfaces as part of controller changes.
- Run `gradlew build` to verify compilation and `gradlew architectureTest` (the repo has ArchUnit checks) to ensure package rules are preserved.

Developer etiquette
-------------------
- Keep documentation concise and accurate.
- Avoid large examples in annotations; prefer referencing DTO schemas.
- When changing method signatures, update both controller and `*Info` interface (compiler will help if the controller implements the interface).
