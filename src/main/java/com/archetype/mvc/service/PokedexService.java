package com.archetype.mvc.service;

import com.archetype.mvc.model.SpeciesOverview;
import com.archetype.mvc.persistence.PokemonRepository;
import com.archetype.mvc.persistence.document.PokemonDocument;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Simple service that exposes species-level data for the Pokedex UI.
 * It copies the layered persistence schema and maps documents to a view model.
 */
@Service
public class PokedexService {

    private final PokemonRepository repository;

    public PokedexService(PokemonRepository repository) {
        this.repository = repository;
    }

    public List<SpeciesOverview> findAllSpecies() {
        return repository.findAll()
                .stream()
                .map(this::toOverview)
                .collect(Collectors.toList());
    }

    public Optional<SpeciesOverview> findSpeciesById(UUID id) {
        return repository.findById(id).map(this::toOverview);
    }

    private SpeciesOverview toOverview(PokemonDocument doc) {
        return new SpeciesOverview(doc.nationalId(), doc.name(), doc.types());
    }
}

