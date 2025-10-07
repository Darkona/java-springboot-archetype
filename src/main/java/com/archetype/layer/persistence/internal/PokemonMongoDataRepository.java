package com.archetype.layer.persistence.internal;


import com.archetype.layer.domain.model.Pokemon;
import com.archetype.layer.domain.model.Species;
import com.archetype.layer.exception.PokemonNotFoundException;
import com.archetype.layer.mapper.persistence.PokemonPersistenceMapper;
import com.archetype.layer.mapper.persistence.SpeciesPersistenceMapper;
import com.archetype.layer.persistence.PokemonDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


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
    public Pokemon getPokemonById(UUID id) {
        var doc = pokemonRepo.findById(id).orElseThrow(() -> new PokemonNotFoundException(id));
        return pokemonMapper.toDomain(doc);
    }

    @Override
    public List<Pokemon> getPokemonByName(String name) {
        var docs = pokemonRepo.findAllByName(name);
        return pokemonMapper.toDomain(docs);
    }

    @Override
    public void saveAll(List<Species> species) {
        speciesRepo.saveAll(speciesMapper.toDocuments(species));
    }

    @Override
    public boolean pokemonExistsById(UUID id) {
        return pokemonRepo.existsById(id);
    }

    @Override
    public boolean speciesExistsById(int id) {
        return speciesRepo.existsById(id);
    }

    @Override
    public boolean existsByNationalId(int i) {
        return false;
    }

    @Override
    public void deletePokemon(UUID id) {
        pokemonRepo.deleteById(id);
    }

    @Override
    public List<Pokemon> getAllPokemon() {
        return pokemonMapper.toDomain(pokemonRepo.findAll());
    }

    @Override
    public List<Pokemon> getAllPokemonOfNationalId(int nationalId) {
        var species = speciesRepo.getByNationalIdIs(nationalId);
        return pokemonMapper.toDomain(pokemonRepo.getAllBySpeciesName(species.name()));
    }


}
