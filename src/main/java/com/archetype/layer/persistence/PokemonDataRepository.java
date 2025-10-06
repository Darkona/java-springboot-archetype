package com.archetype.layer.persistence;

import com.archetype.layer.domain.model.Species;

import java.util.List;


public interface PokemonDataRepository {


    List<Species> getAllSpecies();

    Species getSpeciesById(int id);

    Species getSpeciesByName(String name);

    Species save(Species species);

    void saveAll(List<Species> species);

    boolean existsById(int id);


}
