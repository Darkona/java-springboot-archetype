package com.archetype.mvc.persistence.internal;


import com.archetype.mvc.model.Species;
import com.archetype.mvc.persistence.PokemonMvcDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class PokemonMongoMvcMvcDataRepository implements PokemonMvcDataRepository {

    private final PokemonMvcRepository pokemonRepo;
    private final SpeciesMvcRepository speciesRepo;
    private final PokemonMvcPersistenceMapper mapper;

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
