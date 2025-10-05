package com.archetype.layer.client.pokeapi.mapper;

import com.archetype.layer.client.pokeapi.dto.PokeApiPokemon;
import com.archetype.layer.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting PokeAPI DTOs to domain models.
 * Maps between PokeAPI client DTOs and domain models following ADR 0002 (Domain separation and mapping).
 */
@Mapper(componentModel = "spring")
public interface PokeApiMapper {

    /**
     * Convert PokeAPI Pokemon to domain Pokemon model.
     * Maps the essential Pokemon data from PokeAPI to our domain representation.
     */
    @Mapping(target = "id", ignore = true) // Domain ID will be generated
    @Mapping(target = "nationalId", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "height", source = "height")
    @Mapping(target = "weight", source = "weight")
    @Mapping(target = "baseExperience", source = "baseExperience")
    @Mapping(target = "types", expression = "java(mapTypes(pokeApiPokemon.types()))")
    @Mapping(target = "abilities", expression = "java(mapAbilities(pokeApiPokemon.abilities()))")
    @Mapping(target = "moves", expression = "java(mapMoves(pokeApiPokemon.moves()))")
    @Mapping(target = "species", expression = "java(mapSpecies(pokeApiPokemon.species()))")
    @Mapping(target = "hp", expression = "java(getStatValue(pokeApiPokemon.stats(), \"hp\"))")
    @Mapping(target = "attack", expression = "java(getStatValue(pokeApiPokemon.stats(), \"attack\"))")
    @Mapping(target = "defense", expression = "java(getStatValue(pokeApiPokemon.stats(), \"defense\"))")
    @Mapping(target = "specialAttack", expression = "java(getStatValue(pokeApiPokemon.stats(), \"special-attack\"))")
    @Mapping(target = "specialDefense", expression = "java(getStatValue(pokeApiPokemon.stats(), \"special-defense\"))")
    @Mapping(target = "speed", expression = "java(getStatValue(pokeApiPokemon.stats(), \"speed\"))")
    @Mapping(target = "frontSpriteUrl", source = "sprites.frontDefault")
    @Mapping(target = "backSpriteUrl", source = "sprites.backDefault")
    Pokemon toDomainPokemon(PokeApiPokemon pokeApiPokemon);

    /**
     * Map PokeAPI types to domain Type objects.
     */
    default List<Type> mapTypes(List<PokeApiPokemon.PokeApiType> pokeApiTypes) {
        if (pokeApiTypes == null) {
            return List.of();
        }
        return pokeApiTypes.stream()
                .map(pokeApiType -> new Type(
                        pokeApiType.type().name(),
                        pokeApiType.slot()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Map PokeAPI abilities to domain Ability objects.
     */
    default List<Ability> mapAbilities(List<PokeApiPokemon.PokeApiAbility> pokeApiAbilities) {
        if (pokeApiAbilities == null) {
            return List.of();
        }
        return pokeApiAbilities.stream()
                .map(pokeApiAbility -> new Ability(
                        pokeApiAbility.ability().name(),
                        pokeApiAbility.isHidden(),
                        pokeApiAbility.slot()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Map PokeAPI moves to domain Move objects.
     * Takes first 4 moves to avoid overwhelming the database.
     */
    default List<Move> mapMoves(List<PokeApiPokemon.PokeApiMove> pokeApiMoves) {
        if (pokeApiMoves == null) {
            return List.of();
        }
        return pokeApiMoves.stream()
                .limit(4) // Limit to first 4 moves
                .map(pokeApiMove -> new Move(
                        pokeApiMove.move().name(),
                        50,   // Default power
                        100,  // Default accuracy
                        20    // Default PP
                ))
                .collect(Collectors.toList());
    }

    /**
     * Map PokeAPI species to domain Species object.
     */
    default Species mapSpecies(PokeApiPokemon.PokeApiSpecies pokeApiSpecies) {
        if (pokeApiSpecies == null) {
            return null;
        }
        return new Species(
                pokeApiSpecies.name(),
                List.of() // Empty egg groups for now
        );
    }

    /**
     * Extract stat value by name from PokeAPI stats list.
     */
    default Integer getStatValue(List<PokeApiPokemon.PokeApiStat> stats, String statName) {
        if (stats == null) {
            return 0;
        }
        return stats.stream()
                .filter(stat -> statName.equals(stat.stat().name()))
                .findFirst()
                .map(PokeApiPokemon.PokeApiStat::baseStat)
                .orElse(0);
    }
}
