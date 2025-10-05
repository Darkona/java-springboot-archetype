# ADR 0015 — Prefer Spring annotations over ResponseEntity in controllers

Date: 2025-10-05

Status: Accepted

## Decision

Do not use `ResponseEntity<T>` in controller methods unless absolutely necessary for complex response scenarios. Instead, prefer returning domain objects directly and use Spring annotations to control HTTP status codes, headers, and other response aspects. This leads to cleaner, more readable controller code that focuses on business logic rather than HTTP plumbing.

## Context

Spring Boot controllers can handle HTTP responses in multiple ways:
1. Using `ResponseEntity<T>` for full control over the response
2. Returning domain objects directly and using annotations for HTTP concerns
3. Using `@ResponseStatus`, `@ResponseBody`, and other Spring annotations

Many developers default to using `ResponseEntity<T>` for all controller methods, even when simple scenarios only need to return data with standard HTTP status codes.

## Problem

- `ResponseEntity<T>` adds boilerplate code to controller methods
- It mixes HTTP concerns with business logic in the method body
- Makes controller methods more verbose and harder to read
- Reduces focus on the actual business operation being performed
- Can make testing more complex due to the additional wrapper

## Rationale

- Spring Boot automatically serializes return objects to JSON/XML
- Spring annotations provide declarative ways to control HTTP aspects
- Cleaner separation between business logic and HTTP concerns
- More readable and maintainable controller code
- Easier to unit test methods that return domain objects directly

## Guidelines

### ✅ **DO: Use direct return types with annotations**

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

### ❌ **DON'T: Use ResponseEntity for simple cases**

```java
// Avoid this verbose approach
@PostMapping
public ResponseEntity<PokemonDetails> createPokemon(@RequestBody PokemonCreate request) {
    Pokemon created = pokemonService.createPokemon(request);
    PokemonDetails response = pokemonDtoMapper.toDetails(created);
    return ResponseEntity.status(201).body(response);
}
```

### ⚠️ **Exception: When ResponseEntity IS appropriate**

Use `ResponseEntity<T>` only when you need:
- Dynamic status codes based on business logic
- Custom headers that can't be set via annotations
- Complex conditional responses
- File downloads or streaming responses

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

## Spring Annotations Reference

- `@ResponseStatus(HttpStatus.CREATED)` - Set 201 status
- `@ResponseStatus(HttpStatus.NO_CONTENT)` - Set 204 status  
- `@ResponseStatus(HttpStatus.ACCEPTED)` - Set 202 status
- `@ResponseBody` - Serialize return value (implicit in `@RestController`)
- `@Valid` - Enable validation
- Return `void` for operations that don't need response body

## Exception Handling

Use `@ExceptionHandler` and `@ControllerAdvice` for consistent error responses:

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

## Implementation Steps

1. Review all existing controllers for `ResponseEntity<T>` usage
2. Replace simple `ResponseEntity<T>` returns with direct objects + annotations
3. Keep `ResponseEntity<T>` only where complex response logic is needed
4. Update controller tests to expect direct return types
5. Document any remaining `ResponseEntity<T>` usage with clear justification

## Benefits

- **Cleaner code**: Controllers focus on business operations
- **Better readability**: Less HTTP boilerplate in method signatures
- **Easier testing**: Test domain objects directly, not HTTP wrappers
- **Consistency**: Uniform approach across the codebase
- **Spring idioms**: Leverages Spring's annotation-driven approach

## Trade-offs

- **Less explicit**: HTTP status codes are in annotations, not method body
- **Learning curve**: Developers need to know Spring annotation options
- **Flexibility**: Some complex scenarios still require `ResponseEntity<T>`

## Migration Strategy

1. **New controllers**: Always start with direct return types + annotations
2. **Existing controllers**: Refactor during maintenance cycles
3. **Exception cases**: Document and justify any `ResponseEntity<T>` usage
4. **Team training**: Ensure team understands annotation-based approach

## Examples in this Codebase

See `PokemonController` for examples of the preferred annotation-based approach over `ResponseEntity<T>` usage.
