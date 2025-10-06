package com.archetype.layer.persistence.internal;

import com.archetype.layer.persistence.document.PokemonDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PokemonRepository extends MongoRepository<PokemonDocument, UUID> {

    /**
     * Check if a Pokemon exists by its national ID.
     */
    boolean existsByNationalId(int nationalId);

    /**
     * Find a Pokemon by its national ID.
     */
    Optional<PokemonDocument> findByNationalId(int nationalId);
}
