package com.archetype.hexagonal.application.port.in;

import com.archetype.hexagonal.domain.model.PokemonPet;

import java.util.UUID;

public interface AdoptPokemon {

    PokemonPet adopt(UUID id, String ownerId);

}

