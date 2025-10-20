package com.archetype.layer.controller;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.domain.dto.response.PokemonDetails;
import com.archetype.layer.domain.dto.response.SpeciesResponse;
import com.archetype.layer.service.PokemonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller layer using proper DTOs.
 * Following layered architecture - works with DTOs and domain models, not persistence documents.
 * Follows ADR 0015 (Prefer Spring annotations over ResponseEntity) for clean controller design.
 * Follows ADR 0016 (Exception handling strategy) for hybrid validation approach.
 */
@RestController
@RequestMapping("/api/pokemon")
@RequiredArgsConstructor
public class PokemonController implements PokemonControllerInfo {

    private final PokemonService pokemonService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PokemonDetails create(@RequestBody @Valid PokemonCreate pokemonCreate) {
        return pokemonService.createPokemon(pokemonCreate);
    }

    @GetMapping("/load")
    public List<SpeciesResponse> loadSpecies() {
        return pokemonService.loadSpecies();
    }

    @GetMapping("/species")
    public List<SpeciesResponse> getAllSpecies() {
        return pokemonService.listAllSpecies();
    }
}
