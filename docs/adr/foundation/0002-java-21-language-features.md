# ADR 0017 — Java 21 language features and modern coding practices

Date: 2025-10-05

Status: Accepted

## Decision

Leverage Java 21 language features consistently across the archetype to demonstrate modern, idiomatic Java development. Prefer records, switch expressions, pattern matching, and other Java 21 features where they improve code clarity, safety, and maintainability.

## Context

This archetype uses Java 21 (per ADR 0008) but lacks guidance on leveraging modern language features. Teams scaffolding from this archetype should see best practices for:
- Records for immutable data structures
- Switch expressions for cleaner control flow
- Pattern matching for type-safe operations
- Enhanced Stream API features
- Text blocks for multi-line content

Modern Java features reduce boilerplate, improve type safety, and enhance code readability when used appropriately.

## Problem

Current codebase shows inconsistent adoption of Java 21 features:
- Some DTOs use traditional classes with Lombok instead of records
- Switch statements use old-style syntax where expressions would be clearer
- Missing opportunities for pattern matching and instanceof improvements
- Stream API usage doesn't leverage Java 21 enhancements

Without clear guidance, teams may:
- Continue using outdated patterns from older Java versions
- Miss opportunities for more concise, type-safe code
- Create inconsistent codebases mixing old and new approaches

## Architecture

### Records for Data Transfer and Value Objects

**Use records for:**
- DTOs (Data Transfer Objects) - request/response objects
- Value objects - immutable data holders
- Configuration objects - settings and parameters
- Event objects - domain events and messages

**Example transformation:**
```java
// Before (traditional class)
@Getter @Setter
public class PokemonCreateRequest {
    private String name;
    private List<String> types;
    // constructors, equals, hashCode, toString...
}

// After (Java 21 record)
public record PokemonCreateRequest(
    @NotBlank String name,
    @NotEmpty List<String> types
) {}
```

### Switch Expressions for Control Flow

**Use switch expressions for:**
- Mapping operations (enum to string, type to behavior)
- Multi-branch return values
- Complex conditional logic with clear outcomes

**Example:**
```java
// Before (traditional switch)
String typeDescription;
switch (pokemonType) {
    case FIRE:
        typeDescription = "Strong against Grass, weak against Water";
        break;
    case WATER:
        typeDescription = "Strong against Fire, weak against Electric";
        break;
    default:
        typeDescription = "Unknown type";
}

// After (switch expression)
String typeDescription = switch (pokemonType) {
    case FIRE -> "Strong against Grass, weak against Water";
    case WATER -> "Strong against Fire, weak against Electric";
    default -> "Unknown type";
};
```

### Pattern Matching for Type Operations

**Use pattern matching for:**
- Type checking and casting operations
- Destructuring complex objects
- Guard conditions in switch expressions

**Example:**
```java
// Before (traditional instanceof)
if (pokemon instanceof EvolutionPokemon) {
    EvolutionPokemon evolution = (EvolutionPokemon) pokemon;
    return evolution.getEvolutionLevel();
}

// After (pattern matching)
if (pokemon instanceof EvolutionPokemon(var level, var form)) {
    return level;
}
```

### Stream API Guidelines

**Team choice principle:** Use streams when they result in **easier or more efficient** code.

**Prefer streams for:**
- Complex data transformations with multiple steps
- Filtering and mapping operations
- Parallel processing of large datasets
- Functional-style data processing

**Prefer traditional loops for:**
- Simple iterations with side effects
- Early termination logic
- When debugging is critical
- Performance-sensitive hot paths

**Example of good stream usage:**
```java
// Good: Clear transformation pipeline
List<PokemonOverview> overviews = pokemons.stream()
    .filter(pokemon -> pokemon.getLevel() > 50)
    .map(pokemonMapper::toOverview)
    .sorted(Comparator.comparing(PokemonOverview::name))
    .toList();

// Avoid: Overly complex stream with side effects
// Better as traditional loop for clarity
```

### Text Blocks for Multi-line Content

**Use text blocks for:**
- SQL queries
- JSON templates
- HTML fragments
- Multi-line string constants

**Example:**
```java
// Before (string concatenation)
String query = "SELECT p.id, p.name, p.level " +
               "FROM pokemon p " +
               "WHERE p.type = ? " +
               "ORDER BY p.level DESC";

// After (text block)
String query = """
    SELECT p.id, p.name, p.level
    FROM pokemon p
    WHERE p.type = ?
    ORDER BY p.level DESC
    """;
```

## Implementation Guidelines

### Records Best Practices

1. **Validation on records:**
```java
public record PokemonCreate(
    @NotBlank(message = "pokemon.name.required")
    @Size(max = 50, message = "pokemon.name.max-length")
    String name,
    
    @NotNull(message = "pokemon.national-id.required") 
    @Min(value = 1, message = "pokemon.national-id.min")
    @Max(value = 1010, message = "pokemon.national-id.max")
    Integer nationalId
) {
    // Compact constructor for additional validation
    public PokemonCreate {
        if (name != null && name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }
}
```

2. **Records with computed fields:**
```java
public record PokemonStats(int hp, int attack, int defense) {
    public int totalStats() {
        return hp + attack + defense;
    }
    
    public boolean isStrongPokemon() {
        return totalStats() > 500;
    }
}
```

### Migration Strategy

1. **Convert DTOs first** - Lowest risk, highest impact
2. **Update control flow** - Switch expressions where appropriate
3. **Apply pattern matching** - Gradual adoption in new code
4. **Review stream usage** - Optimize for clarity and performance

### Compatibility Considerations

- **Jackson serialization:** Records work seamlessly with Spring Boot's JSON handling
- **MapStruct mapping:** Full support for record mapping since MapStruct 1.5+
- **Bean Validation:** Standard annotations work on record components
- **Spring Framework:** Records supported as @RequestBody, @ResponseBody, etc.

## Benefits

### For Developers:
- **Less boilerplate:** Records eliminate getter/setter/equals/hashCode code
- **Immutable by default:** Reduces bugs from unexpected mutations
- **Pattern matching:** More expressive and type-safe code
- **Modern syntax:** Cleaner, more readable control flow

### For Maintainability:
- **Consistent patterns:** Clear guidelines for language feature usage
- **Type safety:** Compile-time guarantees with pattern matching
- **Reduced cognitive load:** Less code to read and understand
- **Future-ready:** Prepared for upcoming Java language features

### For Performance:
- **Optimized records:** JVM optimizations for record types
- **Switch expressions:** Better JVM optimization than traditional switch
- **Reduced allocations:** Records can be more memory-efficient

## Examples in This Codebase

### Good Examples (Already Implemented):
- `PokemonCreate` (layer) - Record with validation annotations
- `PokeApiPokemon` (client) - Nested records for complex API responses
- `PokemonDetails` (layer) - Simple data transfer record

### Opportunities for Improvement:
- `PokemonCreateRequest` (hexagonal) → Convert to record
- `PokemonResponse` (hexagonal) → Convert to record
- `TrainerDTO` (onion) → Convert to record
- Switch statements in service classes → Use switch expressions
- Complex conditional logic → Apply pattern matching

## Trade-offs

### Pros:
- **Modern and idiomatic:** Showcases Java 21 capabilities
- **Reduced boilerplate:** Less code to write and maintain
- **Type safety:** Compile-time guarantees with modern features
- **Performance:** JVM optimizations for newer language features

### Cons:
- **Learning curve:** Teams need familiarity with Java 21 features
- **IDE support:** Some IDEs may have limited support for newest features
- **Debugging:** Records and switch expressions may be harder to debug in some tools
- **Migration effort:** Converting existing code requires careful testing

## Migration Guidelines

### Phase 1: Records (Low Risk)
1. Convert simple DTOs to records
2. Update validation annotations
3. Verify JSON serialization works
4. Test MapStruct mappings

### Phase 2: Control Flow (Medium Risk)
1. Convert simple switch statements to expressions
2. Apply pattern matching in new code
3. Use text blocks for multi-line strings

### Phase 3: Stream Optimization (Team Choice)
1. Review existing stream usage
2. Apply Java 21 stream improvements where beneficial
3. Convert complex streams to traditional loops where clearer

## Future Enhancements

### Potential Java 21+ Features:
- **Sealed classes:** For controlled type hierarchies
- **Foreign Function & Memory API:** For native integrations
- **Virtual threads:** For improved concurrency (Project Loom)
- **String templates:** When standardized in future Java versions

## Validation

Teams should verify modern Java features work correctly with:
- JSON serialization/deserialization
- Bean validation (@Valid, @NotNull, etc.)
- MapStruct mapping
- Spring Framework integration
- Architecture tests (ArchUnit)

Run `./gradlew build` and `./gradlew architectureTest` to ensure compatibility after applying modern language features.
