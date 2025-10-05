package com.archetype.hexagonal.application.port.out;

import com.archetype.hexagonal.domain.model.PokemonPet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PokemonRepositoryPort {

    PokemonPet save(PokemonPet pet);

    Optional<PokemonPet> findById(UUID id);

    List<PokemonPet> findAvailable();

}

