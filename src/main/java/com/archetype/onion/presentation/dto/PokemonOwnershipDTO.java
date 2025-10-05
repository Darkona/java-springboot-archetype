package com.archetype.onion.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Data Transfer Object for Pokemon Ownership.
 * Used for REST API request/response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokemonOwnershipDTO {
    
    private String pokemonId;
    private String nickname;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant acquiredAt;
}
