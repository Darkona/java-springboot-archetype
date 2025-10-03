package com.archetype.clients.pokemon.dto;

import java.util.List;
import java.util.UUID;

/**
 * Client-side DTO for Pokemon details returned by the external service.
 * This DTO is intentionally separate from domain and persistence models to keep boundaries clear.
 */
public record PokemonClientDetails(
        UUID id,
        String name,
        List<String> types,
        List<String> abilities,
        Integer baseExperience,
        Integer height,
        Integer weight
) {
}

