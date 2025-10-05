package com.archetype.layer.client.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO representing a Pokemon from PokeAPI.
 * Maps the essential fields we need from the PokeAPI response.
 */
public record PokeApiPokemon(
        int id,
        String name,
        int height,
        int weight,
        @JsonProperty("base_experience") Integer baseExperience,
        List<PokeApiType> types,
        List<PokeApiAbility> abilities,
        List<PokeApiMove> moves,
        List<PokeApiStat> stats,
        PokeApiSprites sprites,
        PokeApiSpecies species
) {

    public record PokeApiType(
            int slot,
            PokeApiTypeInfo type
    ) {
        public record PokeApiTypeInfo(String name, String url) {
        }
    }

    public record PokeApiAbility(
            PokeApiAbilityInfo ability,
            @JsonProperty("is_hidden") boolean isHidden,
            int slot
    ) {
        public record PokeApiAbilityInfo(String name, String url) {
        }
    }

    public record PokeApiMove(
            PokeApiMoveInfo move
    ) {
        public record PokeApiMoveInfo(String name, String url) {
        }
    }

    public record PokeApiStat(
            @JsonProperty("base_stat") int baseStat,
            int effort,
            PokeApiStatInfo stat
    ) {
        public record PokeApiStatInfo(String name, String url) {
        }
    }

    public record PokeApiSprites(
            @JsonProperty("front_default") String frontDefault,
            @JsonProperty("front_shiny") String frontShiny,
            @JsonProperty("back_default") String backDefault,
            @JsonProperty("back_shiny") String backShiny
    ) {
    }

    public record PokeApiSpecies(
            String name,
            String url
    ) {
    }
}
