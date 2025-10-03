package com.archetype.clients.pokemon.mapper;

import com.archetype.clients.pokemon.dto.PokemonClientCreate;
import com.archetype.clients.pokemon.dto.PokemonClientDetails;
import com.archetype.layer.persistence.document.PokemonDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper between client-side DTOs and the local persistence document.
 * Keeps client DTOs separated from domain/persistence models at the adapter boundary.
 */
@Mapper(componentModel = "spring")
public interface PokemonClientMapper {

    PokemonClientMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(PokemonClientMapper.class);

    // Map client details returned by external service -> local persistence document
    PokemonDocument toDocument(PokemonClientDetails details);

    // Map local persistence document -> client details (useful when proxying responses)
    PokemonClientDetails fromDocument(PokemonDocument document);

    // Map client create DTO -> document (ignore id since it is assigned by persistence)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "baseExperience", source = "nationalId")
    @Mapping(target = "types", ignore = true)
    @Mapping(target = "abilities", ignore = true)
    @Mapping(target = "height", ignore = true)
    @Mapping(target = "weight", ignore = true)
    PokemonDocument toDocument(PokemonClientCreate create);
}

