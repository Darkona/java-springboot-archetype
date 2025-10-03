package com.archetype.layer.persistence.mapper;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.persistence.document.PokemonDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PokemonPersistenceMapper {
    PokemonPersistenceMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(PokemonPersistenceMapper.class);

    // Map create DTO -> MongoDB document
    @Mapping(target = "name", source = "name")
    @Mapping(target = "baseExperience", source = "nationalId")
    PokemonDocument toDocument(PokemonCreate pokemonCreate);

    // Map document -> create DTO (may be useful in some flows)
    @Mapping(target = "nationalId", source = "baseExperience")
    @Mapping(target = "name", source = "name")
    PokemonCreate fromDocument(PokemonDocument document);
}

