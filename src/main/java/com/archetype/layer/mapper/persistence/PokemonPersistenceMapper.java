package com.archetype.layer.mapper.persistence;

import com.archetype.layer.domain.model.Pokemon;
import com.archetype.layer.persistence.document.PokemonDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PokemonPersistenceMapper {


    @Mapping(target = "types", expression = "java(pokemon.getSpecies().typesAsString())")
    @Mapping(target = "speciesName", source = "pokemon.species.name")
    @Mapping(target = "nationalId", source = "pokemon.species.nationalId")
    @Mapping(target = "id", expression = "java(new PokemonId())")
    @Mapping(target = "abilities", source = "pokemon.species.abilities")
    PokemonDocument toDocument(Pokemon pokemon);



    @Mapping(target = "species", source = "doc.speciesName")
    Pokemon toDomain(PokemonDocument doc);
}
