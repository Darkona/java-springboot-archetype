package com.archetype.layer.service;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.persistence.PokemonRepository;
import com.archetype.layer.persistence.document.PokemonDocument;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class PokemonService {

    private final PokemonRepository pokemonRepository;

    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public PokemonDocument createPokemon(PokemonCreate pokemonCreate) {
        PokemonDocument doc = new PokemonDocument();
        doc.setId(UUID.randomUUID());
        doc.setName(pokemonCreate.name());
        return pokemonRepository.save(doc);
    }

    public PokemonDocument getPokemon(UUID id) {
        return pokemonRepository.findById(id).orElseThrow();
    }

    public PokemonDocument updatePokemon(UUID id, PokemonCreate pokemonCreate) {
        PokemonDocument existing = pokemonRepository.findById(id).orElseThrow();
        existing.setName(pokemonCreate.name());
        return pokemonRepository.save(existing);
    }

    public void deletePokemon(UUID id) {
        pokemonRepository.deleteById(id);
    }

    public List<PokemonDocument> getAllPokemons() {
        return pokemonRepository.findAll();
    }
}

