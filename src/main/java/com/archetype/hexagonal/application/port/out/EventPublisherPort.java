package com.archetype.hexagonal.application.port.out;

import com.archetype.hexagonal.domain.model.PokemonPet;

public interface EventPublisherPort {

    void publishPokemonRegistered(PokemonPet pet);

    void publishPokemonAdopted(PokemonPet pet);

    void publishPokemonReturned(PokemonPet pet);

}

