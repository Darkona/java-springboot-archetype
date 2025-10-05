package com.archetype.layer.domain.model;

import java.util.UUID;

public record PokemonId(UUID uuid) {

    public PokemonId() {
        this(UUID.randomUUID());
    }

}

