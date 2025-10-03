package com.archetype.layer.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.archetype.layer.persistence.document.PokemonDocument;
import java.util.UUID;

@Repository
public interface PokemonRepository extends MongoRepository<PokemonDocument, UUID> {
}

