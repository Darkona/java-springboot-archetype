package com.archetype.layer.mapper.persistence;

import com.archetype.layer.domain.model.Pokemon;
import com.archetype.layer.domain.model.Species;
import com.archetype.layer.persistence.document.PokemonDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Persistence mapper: maps between domain model and persistence document.
 * Following ADR 0002 - centralized mapper organization in mapper.persistence package.
 * This mapper works with domain models and persistence documents, NOT DTOs.
 */
@Mapper(componentModel = "spring")
public interface PokemonPersistenceMapper {
    PokemonPersistenceMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(PokemonPersistenceMapper.class);

    // Map domain Pokemon -> MongoDB document
    @Mapping(target = "id", ignore = true) // Let repository handle ID generation
    @Mapping(target = "name", source = "name")
    @Mapping(target = "level", source = "level")
    @Mapping(target = "shiny", source = "shiny")
    @Mapping(target = "nationalId", source = "species.nationalId")
    @Mapping(target = "speciesName", source = "species.name")
    PokemonDocument toDocument(Pokemon pokemon);

    // Map document -> domain Pokemon
    @Mapping(target = "species", expression = "java(createSpeciesFromDocument(document))")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "level", source = "level")
    Pokemon toDomain(PokemonDocument document);

    // Helper method to create Species from document data
    default Species createSpeciesFromDocument(PokemonDocument document) {
        // This would typically involve looking up the full species data
        // For now, create a minimal species with the stored data
        return new Species(
            document.getNationalId(),
            document.getSpeciesName(),
            null, // firstType
            null, // secondType
            null, // abilities
            null, // eggGroups
            50,   // baseHp
            null, // evolutions
            null  // moves
        );
    }
}
