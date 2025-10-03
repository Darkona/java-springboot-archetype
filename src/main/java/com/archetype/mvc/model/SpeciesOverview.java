package com.archetype.mvc.model;

import java.util.List;
import java.util.UUID;

/**
 * View model for a species overview shown in the pokedex list.
 */
public record SpeciesOverview(UUID id, String name, List<String> types) {
}

