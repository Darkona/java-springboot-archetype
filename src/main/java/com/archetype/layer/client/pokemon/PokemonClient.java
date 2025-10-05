package com.archetype.layer.client.pokemon;

import com.archetype.layer.client.pokemon.dto.PokemonClientCreate;
import com.archetype.layer.client.pokemon.dto.PokemonClientDetails;
import com.archetype.layer.client.pokemon.dto.PokemonClientOverview;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * OpenFeign client for Pokemon external service.
 * Demonstrates how to create a client for the same endpoints as our controller.
 * 
 * Uses client-specific DTOs separate from domain DTOs to maintain clear adapter boundaries
 * as per ADR 0007 (Prefer OpenFeign) and ADR 0002 (Domain separation and mapping).
 */
@FeignClient(
    name = "pokemon-service",
    url = "${clients.pokemon.url:http://localhost:8080}",
    configuration = PokemonClientConfiguration.class
)
public interface PokemonClient {

    /**
     * Create a new Pokemon via external service.
     */
    @PostMapping("/api/pokemon")
    ResponseEntity<PokemonClientDetails> createPokemon(@RequestBody PokemonClientCreate request);

    /**
     * Get Pokemon details by ID.
     */
    @GetMapping("/api/pokemon/{id}")
    ResponseEntity<PokemonClientDetails> getPokemon(@PathVariable("id") UUID id);

    /**
     * Update an existing Pokemon.
     */
    @PutMapping("/api/pokemon/{id}")
    ResponseEntity<PokemonClientDetails> updatePokemon(
            @PathVariable("id") UUID id,
            @RequestBody PokemonClientCreate request
    );

    /**
     * Delete a Pokemon by ID.
     */
    @DeleteMapping("/api/pokemon/{id}")
    ResponseEntity<Void> deletePokemon(@PathVariable("id") UUID id);

    /**
     * Get all Pokemon overview list.
     */
    @GetMapping("/api/pokemon")
    ResponseEntity<List<PokemonClientOverview>> getAllPokemons();
}
