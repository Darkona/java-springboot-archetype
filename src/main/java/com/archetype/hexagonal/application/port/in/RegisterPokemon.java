package com.archetype.hexagonal.application.port.in;

import com.archetype.hexagonal.domain.model.PokemonPet;

import java.util.List;

public interface RegisterPokemon {

    PokemonPet register(String name, List<String> types);

}

