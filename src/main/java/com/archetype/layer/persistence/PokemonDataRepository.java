package com.archetype.layer.persistence;

import com.archetype.layer.domain.model.Pokemon;
import com.archetype.layer.domain.model.Species;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PokemonDataRepository {


    List<Species> getAllSpecies();

    Species getSpeciesById(int id);

    Species getSpeciesByName(String name);

    Species save(Species species);

    Pokemon save(Pokemon pokemon);

    Pokemon getPokemonById(UUID id);

    List<Pokemon> getPokemonByName(String name);

    void saveAll(List<Species> species);

    boolean speciesExistsById(int id);

    boolean pokemonExistsById(UUID id);

    boolean existsByNationalId(@NotNull(message = "pokemon.national-id.required") @Min(value = 1, message = "pokemon.national-id.min") @Max(value = 151, message = "pokemon.national-id.max") int i);

    void deletePokemon(UUID id);

    List<Pokemon> getAllPokemon();

    List<Pokemon> getAllPokemonOfNationalId(int nationalId);
}
