package com.archetype.mvc.service;

import com.archetype.mvc.model.Species;
import com.archetype.mvc.model.SpeciesOverview;
import com.archetype.mvc.persistence.PokemonMvcDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple service that exposes species-level data for the Pokedex UI.
 * It copies the layered persistence schema and maps documents to a view model.
 */
@Service
@RequiredArgsConstructor
public class PokedexService {

    private final PokemonMvcDataRepository repository;


    public List<SpeciesOverview> findAllSpecies() {
        return repository.getAllSpecies()
                         .stream()
                         .map(this::toOverview)
                         .collect(Collectors.toList());
    }

    public SpeciesOverview findSpeciesById(int id) {
        return toOverview(repository.getSpeciesById(id));
    }

    private SpeciesOverview toOverview(Species doc) {
        var firstType = doc.firstType().toString();
        var secondType = doc.secondType() != null ? doc.secondType().toString() : "none";
        var list = new ArrayList<String>();
        list.add(firstType);
        list.add(secondType);
        return new SpeciesOverview(doc.nationalId(), doc.name(), list);
    }
}

