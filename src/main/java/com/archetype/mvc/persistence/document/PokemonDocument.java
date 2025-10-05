package com.archetype.mvc.persistence.document;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(collection = "pokedex")
public record PokemonDocument(@MongoId int nationalId,
                              String name,
                              List<String> types,
                              List<String> abilities,
                              Integer baseExperience,
                              Integer height,
                              Integer weight
) {
}

