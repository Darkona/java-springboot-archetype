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
    public PokemonDetails createPokemon(@Valid @RequestBody PokemonCreate req) {
        // Pokemon created = pokemonService.createPokemon(req);
//        return pokemonDtoMapper.toDetails(created);
        return null;
    }

    @GetMapping("/{id}")
    public PokemonDetails getPokemon(@PathVariable UUID id) {
//        Pokemon pokemon = pokemonService.getPokemon(id);
//        return pokemonDtoMapper.toDetails(pokemon);
        return null;
    }

    @PutMapping("/{id}")
    public PokemonDetails updatePokemon(@PathVariable UUID id, @Valid @RequestBody PokemonCreate req) {
//        Pokemon updated = pokemonService.updatePokemon(id, req);
//        return pokemonDtoMapper.toDetails(updated);
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePokemon(@PathVariable UUID id) {
        // pokemonService.deletePokemon(id);
    }

    @GetMapping
    public List<PokemonOverview> getAllPokemons() {
//        List<Pokemon> pokemons = pokemonService.getAllPokemons();
//        return pokemons.stream()
//                       .map(pokemonDtoMapper::toOverview)
//                       .toList();
        return null;
    }

    /**
     * Populate the database with the first 151 Pokemon from PokeAPI.
     * This endpoint triggers the data population process.
     */
    @PostMapping("/populate/first-generation")
    public Map<String, Object> populateFirstGeneration() {
//        List<Pokemon> populated = pokeApiDataService.populateFirstGenerationPokemon();
//
//        return Map.of(
//                "message", "First generation Pokemon population completed",
//                "populated_count", populated.size(),
//                "total_count", pokeApiDataService.getCurrentPokemonCount()
//        );
        return null;
    }

    /**
     * Populate a specific range of Pokemon.
     * Useful for testing or partial population.
     */
    @PostMapping("/populate/range")
    public Map<String, Object> populateRange(
            @RequestParam int startId,
            @RequestParam int endId) {
//
//        if (startId < 1 || endId > 151 || startId > endId) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Invalid range. Must be between 1-151 and startId <= endId"
//            );
//        }
//
//        List<Pokemon> populated = pokeApiDataService.populatePokemonRange(startId, endId);
//
//        return Map.of(
//                "message", String.format("Pokemon population completed for range %d-%d", startId, endId),
//                "populated_count", populated.size(),
//                "total_count", pokeApiDataService.getCurrentPokemonCount()
//        );
        return null;
    }

    /**
     * Get the current population status.
     */
    @GetMapping("/populate/status")
    public Map<String, Object> getPopulationStatus() {
//        long currentCount = pokeApiDataService.getCurrentPokemonCount();
//        boolean isFirstGenComplete = pokeApiDataService.isFirstGenerationPopulated();
//
//        return Map.of(
//                "current_pokemon_count", currentCount,
//                "first_generation_complete", isFirstGenComplete,
//                "target_count", 151,
//                "completion_percentage", Math.round((currentCount / 151.0) * 100)
//        );
        return null;
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
