package com.archetype.mvc.persistence.document;

import com.archetype.layer.domain.model.Species;
import com.archetype.layer.domain.model.Type;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
import java.util.Map;

@Document(collection = "pokemon-species")
public record SpeciesDocument(@MongoId int nationalId,
                              @Indexed(unique = true) String name,
                              Type firstType,
                              Type secondType,
                              List<Integer> abilities,
                              Map<Integer, String> moves,
                              Species.PokemonStats stats) {

}
