package com.archetype.mvc.model;

import java.util.List;

/**
 * View model for a species overview shown in the pokedex list.
 */
public record SpeciesOverview(int id, String name, List<String> types) {
}

