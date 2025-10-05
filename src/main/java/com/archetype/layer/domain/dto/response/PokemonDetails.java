package com.archetype.layer.domain.dto.response;

import com.archetype.layer.domain.model.Move;
import jakarta.annotation.Nullable;

import java.util.List;

public record PokemonDetails(
        int nationalId,
        String species,
        String firstType,
        String secondType,
        List<Move> moves,
        String name,
        int level,
        boolean shiny,
        @Nullable PokemonSecret secret
) {
}

