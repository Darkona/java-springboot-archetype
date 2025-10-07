package com.archetype.layer.controller;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.domain.dto.response.PokemonDetails;
import com.archetype.layer.domain.dto.response.PokemonOverview;
import com.archetype.layer.domain.model.Species;
import com.archetype.layer.service.PokemonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        return pokemonService.createPokemom(pokemonCreate);
    }

    @GetMapping("/load")
    public List<Species> loadSpecies() {
        return pokemonService.loadSpecies();
    }

    @GetMapping("/species")
    public List<Species> getAllSpecies() {
        return pokemonService.listAllSpecies();
    }
}
