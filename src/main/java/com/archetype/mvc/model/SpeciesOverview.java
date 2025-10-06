package com.archetype.mvc.model;

import io.github.darkona.logged.utils.Transformer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * View model for a species overview shown in the pokedex list.
 */
public record SpeciesOverview(Integer id, String name, List<String> types) {

    public SpeciesOverview {
        name = Transformer.capitalize(name);
    }

    public String typesJoined() {
        return types.stream().map(t -> Transformer.capitalize(t.toLowerCase())).collect(Collectors.joining(", "));
    }
}

