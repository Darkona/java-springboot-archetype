package com.archetype.onion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value object representing a Pokemon owned by a trainer.
 * Contains information about the ownership relationship.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokemonOwnership {

    private String pokemonId;
    private String nickname;
    private Instant acquiredAt;

    /**
     * Validate Pokemon ownership data.
     *
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (pokemonId == null || pokemonId.trim().isEmpty()) {
            throw new IllegalArgumentException("Pokemon ID cannot be empty");
        }

        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("Pokemon nickname cannot be empty");
        }

        if (nickname.length() > 50) {
            throw new IllegalArgumentException("Pokemon nickname cannot exceed 50 characters");
        }
    }
}
