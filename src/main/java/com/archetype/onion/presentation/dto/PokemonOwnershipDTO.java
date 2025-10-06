package com.archetype.onion.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

/**
 * Data Transfer Object for Pokemon Ownership.
 * Used for REST API request/response.
 * 
 * Follows ADR 0017 (Java 21 language features) by using records for DTOs.
 */
public record PokemonOwnershipDTO(
    String pokemonId,
    String nickname,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant acquiredAt
) {}
