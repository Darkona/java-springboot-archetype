package com.archetype.onion.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Trainer.
 * Used for REST API request/response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDTO {

    private String id;
    private String name;
    private Integer badges;

    @Builder.Default
    private List<PokemonOwnershipDTO> ownedPokemons = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant updatedAt;
}
