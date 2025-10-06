package com.archetype.layer.client.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokeApiAbility(int id,
                             String name,
                             @JsonProperty("effect_entries") List<EffectEntry> effects) {


    public record EffectEntry(String effect, EntryEffectLanguage language) {
    }

    public record EntryEffectLanguage(String name) {
    }
}
