package com.archetype.layer.persistence.document;

import com.archetype.layer.domain.model.PokemonId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(collection = "pokemons")

public record PokemonDocument(
        @MongoId PokemonId id,
        @Indexed String name,
        List<String> types,
        List<String> abilities,
        Integer level,
        Boolean shiny,
        @Indexed Integer nationalId,
        String speciesName
) {


}
