package com.archetype.layer.client.pokemon.dto;

/**
 * Client-specific DTO for creating Pokemon via external service.
 * Separate from domain DTOs to maintain clear adapter boundaries.
 */
public record PokemonClientCreate(
        int nationalId,
        String name
) {
}
