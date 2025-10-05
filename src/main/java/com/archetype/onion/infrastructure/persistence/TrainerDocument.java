package com.archetype.onion.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB document representing a trainer.
 * Infrastructure layer entity for persistence.
 */
@Document(collection = "trainers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDocument {
    
    @Id
    private String id;
    private String name;
    private Integer badges;
    
    @Builder.Default
    private List<PokemonOwnershipDocument> ownedPokemons = new ArrayList<>();
    
    private Instant createdAt;
    private Instant updatedAt;
    
    /**
     * Embedded document for Pokemon ownership.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PokemonOwnershipDocument {
        private String pokemonId;
        private String nickname;
        private Instant acquiredAt;
    }
}
