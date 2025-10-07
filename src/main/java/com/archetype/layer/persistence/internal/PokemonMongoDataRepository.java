package com.archetype.layer.persistence.internal;


import com.archetype.layer.domain.model.Pokemon;
import com.archetype.layer.domain.model.Species;
import com.archetype.layer.mapper.persistence.PokemonPersistenceMapper;
import com.archetype.layer.mapper.persistence.SpeciesPersistenceMapper;
import com.archetype.layer.persistence.PokemonDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class PokemonMongoDataRepository implements PokemonDataRepository {

    private final PokemonRepository pokemonRepo;
    private final SpeciesRepository speciesRepo;
    private final SpeciesPersistenceMapper speciesMapper;
    private final PokemonPersistenceMapper pokemonMapper;

    @Override
    public List<Species> getAllSpecies() {
        return speciesMapper.toDomain(speciesRepo.findAll());
    }

    @Override
    public Species getSpeciesById(int id) {
        return speciesMapper.toDomain(speciesRepo.findById(id).orElseThrow());
    }

    @Override
    public Species getSpeciesByName(String name) {
        return speciesMapper.toDomain(speciesRepo.findByName(name).orElseThrow());
    }

    @Override
    public Species save(Species species) {
        speciesRepo.save(speciesMapper.toDocument(species));
        return species;
    }

    @Override
    public Pokemon save(Pokemon pokemon) {
        pokemonRepo.save(pokemonMapper.toDocument(pokemon));
        return pokemon;
    }

    @Override
    public Pokemon getPokemonById(int id) {
        return null;
    }

    @Override
    public Pokemon getPokemonByName(String name) {
        return null;
    }

    @Override
    public void saveAll(List<Species> species) {
        speciesRepo.saveAll(speciesMapper.toDocuments(species));
    }

    @Override
    public boolean existsById(int id) {
        return speciesRepo.existsById(id);
    }

    @Override
    public boolean existsByNationalId(int i) {
        return false;
    }


}
