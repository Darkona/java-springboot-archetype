package com.archetype.mvc.persistence;


import com.archetype.mvc.model.Species;

import java.util.List;



public interface PokemonMvcDataRepository {


    List<Species> getAllSpecies();

    Species getSpeciesById(int id);

    Species getSpeciesByName(String name);

    Species save(Species species);

    void saveAll(List<Species> species);

    boolean existsById(int id);


}
