package com.archetype.layer.client.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO representing a Pokemon from PokeAPI.
 * Maps the essential fields we need from the PokeAPI response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PokeApiPokemon(
        int id,
        String name,
        int height,
        int weight,
        @JsonProperty("base_experience") Integer baseExperience,
        List<PokeApiType> types,
        List<PokeApiAbility> abilities,
        List<PokeApiMove> moves,
        List<PokeApiStat> stats
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public int stat(String stat){
        return stats.stream().filter(s -> s.stat.name != null && s.stat.name.equals(stat)).findFirst().
             orElseGet(() -> new PokeApiStat(0, 0, new PokeApiStat.PokeStat(stat)))
             .baseStat();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokeApiType(
            int slot,
            PokeApiTypeInfo type
    ) {
        public record PokeApiTypeInfo(String name) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokeApiAbility(
            PokeApiAbilityInfo ability,
            @JsonProperty("is_hidden") boolean isHidden

    ) {
        public record PokeApiAbilityInfo(String name, String url) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokeApiMove(
            PokeApiMoveId move,
            @JsonProperty("version_group_details") List<PokeApiMoveDetail> details
    ) {

        public boolean isValid(){
            return details.stream().anyMatch(detail -> validVersion(detail.version.name));
        }

        private boolean validVersion(String version){
            if(version == null) return false;
            return "red-blue".equals(version) || "yellow".equals(version) || "gold-silver".equals(version);
        }
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record PokeApiMoveId(String name, String url) {

            public int number() {
                if (url == null || url.isBlank()) return -1;
                String[] parts = url.split("/");
                for (int i = parts.length - 1; i >= 0; i--) {
                    if (!parts[i].isBlank()) {
                        try {
                            return Integer.parseInt(parts[i]);
                        } catch (NumberFormatException ignored) {
                            return -1;
                        }
                    }
                }
                return -1;
            }
        }

        public record PokeApiMoveDetail(@JsonProperty("level_learned_at") int levelLearn,
                                        @JsonProperty("version_group") PokeApiMoveVersion version) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record PokeApiMoveVersion(String name) {
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokeApiStat(
            @JsonProperty("base_stat") int baseStat,
            int effort,
            @JsonProperty("stat") PokeStat stat
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record PokeStat(String name){

        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokeApiSprites(
            @JsonProperty("front_default") String frontDefault,
            @JsonProperty("front_shiny") String frontShiny,
            @JsonProperty("back_default") String backDefault,
            @JsonProperty("back_shiny") String backShiny
    ) {
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokeApiSpecies(
            String name,
            String url
    ) {
    }
}
