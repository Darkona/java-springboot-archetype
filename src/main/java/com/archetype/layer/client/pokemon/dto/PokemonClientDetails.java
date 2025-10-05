package com.archetype.layer.client.pokemon.dto;

import java.util.List;
import java.util.UUID;

/**
 * Client-specific DTO for detailed Pokemon response from external service.
 * Separate from domain DTOs to maintain clear adapter boundaries.
 */
public record PokemonClientDetails(
        UUID id,
        int nationalId,
        String species,
        String firstType,
        String secondType,
        List<String> moves,  // Simplified to strings for client representation
        String name,
        int level,
        boolean shiny
) {
}
