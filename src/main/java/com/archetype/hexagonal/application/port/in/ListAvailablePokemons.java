package com.archetype.hexagonal.application.port.in;

import com.archetype.hexagonal.domain.model.PokemonPet;

import java.util.List;

public interface ListAvailablePokemons {

    List<PokemonPet> listAvailable();

}

