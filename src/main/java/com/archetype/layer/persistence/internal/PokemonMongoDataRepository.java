package com.archetype.layer.persistence.internal;


import com.archetype.layer.domain.model.Species;
import com.archetype.layer.mapper.persistence.PokemonPersistenceMapper;
import com.archetype.layer.persistence.PokemonDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class PokemonMongoDataRepository implements PokemonDataRepository {

    private final PokemonRepository pokemonRepo;
    private final SpeciesRepository speciesRepo;
    private final PokemonPersistenceMapper mapper;

    @Override
    public List<Species> getAllSpecies() {
        return mapper.toDomain(speciesRepo.findAll());
    }

    @Override
    public Species getSpeciesById(int id) {
        return mapper.toDomain(speciesRepo.findById(id).orElseThrow());
    }

    @Override
    public Species getSpeciesByName(String name) {
        return mapper.toDomain(speciesRepo.findByName(name).orElseThrow());
    }

    @Override
    public Species save(Species species) {
        speciesRepo.save(mapper.toDocument(species));
        return species;
    }

    @Override
    public void saveAll(List<Species> species) {
         speciesRepo.saveAll(mapper.toDocuments(species));
    }

    @Override
    public boolean existsById(int id) {
        return speciesRepo.existsById(id);
    }



}
