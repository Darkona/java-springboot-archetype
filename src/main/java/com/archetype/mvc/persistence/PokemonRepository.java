package com.archetype.mvc.persistence;

import com.archetype.mvc.persistence.document.PokemonDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("mvcPokemonRepository")
public interface PokemonRepository extends MongoRepository<PokemonDocument, UUID> {
}

