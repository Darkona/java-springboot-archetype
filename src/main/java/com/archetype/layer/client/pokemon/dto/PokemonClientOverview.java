package com.archetype.layer.client.pokemon.dto;

import java.util.UUID;

/**
 * Client-specific DTO for Pokemon overview from external service.
 * Separate from domain DTOs to maintain clear adapter boundaries.
 */
public record PokemonClientOverview(
        UUID id,
        int nationalId,
        String name,
        String species,
        String firstType,
        int level
) {
}
