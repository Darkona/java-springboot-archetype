package com.archetype.layer.domain.dto.response;

import jakarta.annotation.Nullable;

import java.util.List;

public record PokemonDetails(
        int nationalId,
        String species,
        String firstType,
        String secondType,
        List<String> moves,
        String name,
        int level,
        boolean shiny,
        @Nullable PokemonSecret secret
) {
}

