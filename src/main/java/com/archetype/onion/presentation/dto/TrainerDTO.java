package com.archetype.onion.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Trainer.
 * Used for REST API request/response.
 * 
 * Follows ADR 0017 (Java 21 language features) by using records for DTOs.
 */
public record TrainerDTO(
    String id,
    String name,
    Integer badges,
    List<PokemonOwnershipDTO> ownedPokemons,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant createdAt,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant updatedAt
) {
    /**
     * Compact constructor to handle default values.
     */
    public TrainerDTO {
        // Ensure ownedPokemons is never null
        if (ownedPokemons == null) {
            ownedPokemons = new ArrayList<>();
        }
    }
    
    /**
     * Convenience constructor for creating TrainerDTO with default empty pokemon list.
     */
    public TrainerDTO(String id, String name, Integer badges, Instant createdAt, Instant updatedAt) {
        this(id, name, badges, new ArrayList<>(), createdAt, updatedAt);
    }
}
