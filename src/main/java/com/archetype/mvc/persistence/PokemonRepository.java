package com.archetype.mvc.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.archetype.mvc.persistence.document.PokemonDocument;
import java.util.UUID;

@Repository("mvcPokemonRepository")
public interface PokemonRepository extends MongoRepository<PokemonDocument, UUID> {
}

