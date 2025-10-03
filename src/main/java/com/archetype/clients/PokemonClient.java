package com.archetype.clients;

import com.archetype.clients.pokemon.dto.PokemonClientCreate;
import com.archetype.clients.pokemon.dto.PokemonClientDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * Example Feign client for the Pokemon service.
 * This client uses client-side DTOs (separate from domain/persistence types) as guidance for adapters.
 *
 * Configure the base URL via `clients.pokemon.url` (defaults to http://localhost:8080).
 */
@FeignClient(name = "pokemon-client", url = "${clients.pokemon.url:http://localhost:8080}", configuration = FeignConfiguration.class)
public interface PokemonClient {

    @PostMapping("/api/pokemon")
    PokemonClientDetails createPokemon(@RequestBody PokemonClientCreate req);

    @GetMapping("/api/pokemon/{id}")
    PokemonClientDetails getPokemon(@PathVariable UUID id);

    @PutMapping("/api/pokemon/{id}")
    PokemonClientDetails updatePokemon(@PathVariable UUID id, @RequestBody PokemonClientCreate req);

    @DeleteMapping("/api/pokemon/{id}")
    void deletePokemon(@PathVariable UUID id);

    @GetMapping("/api/pokemon")
    List<PokemonClientDetails> getAllPokemons();
}

