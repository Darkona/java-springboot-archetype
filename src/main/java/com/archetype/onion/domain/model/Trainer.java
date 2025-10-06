package com.archetype.onion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Trainer domain entity representing a Pokemon trainer in the onion architecture.
 * This is a pure domain model with no framework dependencies.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {

    /**
     * Domain rule: Maximum number of Pokemon a trainer can own
     */
    private static final int MAX_POKEMON_COUNT = 6;
    private String id;
    private String name;
    private Integer badges;
    @Builder.Default
    private List<PokemonOwnership> ownedPokemons = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Add a Pokemon to the trainer's collection.
     * Enforces domain rule: maximum 6 Pokemon.
     *
     * @param ownership the Pokemon ownership to add
     * @throws IllegalStateException if trainer already has maximum Pokemon
     */
    public void addPokemon(PokemonOwnership ownership) {
        if (ownedPokemons.size() >= MAX_POKEMON_COUNT) {
            throw new IllegalStateException(
                    String.format("Trainer %s already owns maximum number of Pokemon (%d)", name, MAX_POKEMON_COUNT)
            );
        }

        // Check if Pokemon with same nickname already exists
        boolean nicknameExists = ownedPokemons.stream()
                                              .anyMatch(p -> p.getNickname().equalsIgnoreCase(ownership.getNickname()));

        if (nicknameExists) {
            throw new IllegalStateException(
                    String.format("Trainer %s already has a Pokemon with nickname '%s'", name, ownership.getNickname())
            );
        }

        ownedPokemons.add(ownership);
        this.updatedAt = Instant.now();
    }

    /**
     * Validate trainer data before persistence.
     *
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Trainer name cannot be empty");
        }

        if (badges != null && badges < 0) {
            throw new IllegalArgumentException("Badges count cannot be negative");
        }

        if (ownedPokemons != null && ownedPokemons.size() > MAX_POKEMON_COUNT) {
            throw new IllegalArgumentException(
                    String.format("Trainer cannot own more than %d Pokemon", MAX_POKEMON_COUNT)
            );
        }
    }
}
