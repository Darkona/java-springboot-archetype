## ADR arch-005 — Prefer Spring annotations over ResponseEntity in controllers

**Date:** 2025-10-05

**Status:** Accepted

### Decision
Do not use `ResponseEntity<T>` in controller methods unless absolutely necessary for complex response scenarios. Instead, prefer returning domain objects directly and utilize Spring annotations to manage HTTP status codes, headers, and other response aspects. This leads to cleaner, more readable controller code that emphasizes business logic rather than HTTP concerns.

### Context
Spring Boot controllers can handle HTTP responses in multiple ways:

1. Utilizing `ResponseEntity<T>` for full control over the response.
2. Returning domain objects directly while leveraging annotations for HTTP tasks.
3. Using `@ResponseStatus`, `@ResponseBody`, and other Spring annotations.

### Problem
- Utilizing `ResponseEntity<T>` adds boilerplate code to controller methods.
- It combines HTTP concerns with business logic within the method body.
- Results in verbose, less readable controller methods.
- Detracts focus from the actual business operation.
- Can complicate testing due to the additional wrapper.

### Rationale
- Spring Boot serializes return objects to JSON/XML automatically.
- Spring annotations provide declarative methods to control HTTP details.
- Cleaner separation between business logic and HTTP management.
- More readable and maintainable controller code.
- Simpler unit testing for methods returning domain objects directly.

### Guidelines

#### ✅ **DO: Use direct return types with annotations**
```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public PokemonDetails createPokemon(@RequestBody PokemonCreate request) {
    Pokemon created = pokemonService.createPokemon(request);
    return pokemonDtoMapper.toDetails(created);
}

@GetMapping("/{id}")
public PokemonDetails getPokemon(@PathVariable UUID id) {
    Pokemon pokemon = pokemonService.getPokemon(id);
    return pokemonDtoMapper.toDetails(pokemon);
}

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deletePokemon(@PathVariable UUID id) {
    pokemonService.deletePokemon(id);
}
```

#### ❌ **DON'T: Use ResponseEntity for simple cases**
```java
// Avoid this verbose approach
@PostMapping
public ResponseEntity<PokemonDetails> createPokemon(@RequestBody PokemonCreate request) {
    Pokemon created = pokemonService.createPokemon(request);
    PokemonDetails response = pokemonDtoMapper.toDetails(created);
    return ResponseEntity.status(201).body(response);
}
```

#### ⚠️ **Exception: When ResponseEntity IS appropriate**
Utilize `ResponseEntity<T>` only when you require:
- Dynamic status codes based on business logic.
- Custom headers that cannot be specified via annotations.
- Complex conditional responses.
- File downloads or streaming responses.

```java
// Acceptable use - dynamic status based on business logic
@PostMapping("/conditional")
public ResponseEntity<String> conditionalOperation(@RequestBody SomeRequest request) {
    if (someBusinessCondition) {
        return ResponseEntity.ok("Success");
    } else {
        return ResponseEntity.accepted().body("Pending");
    }
}
```

### Spring Annotations Reference
- `@ResponseStatus(HttpStatus.CREATED)` - Set 201 status.
- `@ResponseStatus(HttpStatus.NO_CONTENT)` - Set 204 status.
- `@ResponseStatus(HttpStatus.ACCEPTED)` - Set 202 status.
- `@ResponseBody` - Serialize return value (implicit in `@RestController`).
- `@Valid` - Enable validation.
- Returning `void` is acceptable for operations that don’t need a response body.

### Exception Handling
Employ `@ExceptionHandler` and `@ControllerAdvice` for consistent error responses:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(EntityNotFoundException ex) {
        return new ErrorResponse("Not found", ex.getMessage());
    }
}
```

### Implementation Steps
1. Review all existing controllers for `ResponseEntity<T>` usage.
2. Replace simple `ResponseEntity<T>` returns with direct domain objects + annotations.
3. Maintain `ResponseEntity<T>` usage only where complex response logic is needed.
4. Update controller tests to expect direct return types.
5. Document any remaining `ResponseEntity<T>` usage with clear justification.

### Benefits
- **Cleaner code:** Controllers are focused on business operations.
- **Better readability:** Less HTTP boilerplate cluttering method signatures.
- **Easier testing:** Directly test domain objects rather than HTTP wrappers.
- **Consistency:** A uniform approach throughout the codebase.
- **Spring idioms:** Leverages Spring’s annotation-driven approach effectively.

### Trade-offs
- **Less explicit:** HTTP status codes are defined in annotations rather than method bodies.
- **Learning curve:** Developers need to be familiar with Spring annotation options.
- **Flexibility:** Some complex scenarios might still necessitate `ResponseEntity<T>`.

### Migration Strategy
1. **New controllers:** Always use direct return types and annotations.
2. **Existing controllers:** Refactor during maintenance cycles.
3. **Exception cases:** Document and justify any ongoing `ResponseEntity<T>` usage.
4. **Team training:** Ensure the team understands the annotation-based approach.

### Examples in this Codebase
Refer to `PokemonController` for illustrations of the preferred annotation-based approach over `ResponseEntity<T>` usage.
