package com.archetype.layer.mapper.dto;

import com.archetype.layer.domain.dto.response.PokemonDetails;
import com.archetype.layer.domain.dto.response.PokemonOverview;
import com.archetype.layer.domain.model.Element;
import com.archetype.layer.domain.model.Move;
import com.archetype.layer.domain.model.Pokemon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO mapper: maps between domain model and API DTOs.
 * Following ADR 0002 - centralized mapper organization in mapper.dto package.
 * Important: this mapper MUST NOT reference persistence classes (keeps domain free of persistence).
 */
@Mapper(componentModel = "spring")
public interface PokemonDtoMapper {
    PokemonDtoMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(PokemonDtoMapper.class);

    // Map domain Pokemon -> API response DTO (full details)
    @Mapping(target = "secret", source = "pokemon")
    @Mapping(target = "nationalId", source = "species.nationalId")
    @Mapping(target = "species", source = "species.name")
    // Map Element -> String via elementToString helper method below
    @Mapping(target = "firstType", source = "species.firstType.element")
    @Mapping(target = "secondType", source = "species.secondType.element")
    // map MoveSet -> List<Move> via map(MoveSet) helper method below
    @Mapping(target = "moves", source = "moveSet")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "level", source = "level")
    @Mapping(target = "shiny", source = "shiny")
    PokemonDetails toDetails(Pokemon pokemon);

    // Map domain Pokemon -> API overview DTO (lightweight)
    @Mapping(target = "nationalId", source = "species.nationalId")
    @Mapping(target = "species", source = "species.name")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "level", source = "level")
    PokemonOverview toOverview(Pokemon pokemon);

    // Helper for converting Element -> String (MapStruct will use this automatically)
    default String elementToString(Element element) {
        return element == null ? null : element.name();
    }

    // Helper to convert internal MoveSet to a List<Move> for the DTO
    default List<Move> map(Pokemon.MoveSet moveSet) {
        if (moveSet == null) return List.of();
        List<Move> list = new ArrayList<>();
        if (moveSet.getMove1() != null) list.add(moveSet.getMove1());
        if (moveSet.getMove2() != null) list.add(moveSet.getMove2());
        if (moveSet.getMove3() != null) list.add(moveSet.getMove3());
        if (moveSet.getMove4() != null) list.add(moveSet.getMove4());
        return list;
    }

    // Note: Mapping from create DTO -> domain requires business logic (species lookup, level calc) and should be handled in service layer.
}
