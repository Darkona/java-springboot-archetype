package com.archetype.layer.client.pokeapi;

import com.archetype.layer.client.pokeapi.dto.PokeApiPokemon;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for the PokeAPI.
 * Provides access to Pokemon data from the external PokeAPI service.
 * Follows ADR 0007 (Prefer OpenFeign) for external service integration.
 */
@FeignClient(
        name = "pokeapi",
        url = "${clients.pokeapi.url}",
        configuration = PokeApiClientConfiguration.class
)
public interface PokeApiClient {

    /**
     * Fetch a Pokemon by its ID from PokeAPI.
     *
     * @param id Pokemon national ID (1-151 for first generation)
     * @return Pokemon data from PokeAPI
     */
    @GetMapping("/api/v2/pokemon/{id}")
    PokeApiPokemon getPokemonById(@PathVariable("id") int id);

    /**
     * Fetch a Pokemon by its name from PokeAPI.
     *
     * @param name Pokemon name (e.g., "bulbasaur")
     * @return Pokemon data from PokeAPI
     */
    @GetMapping("/api/v2/pokemon/{name}")
    PokeApiPokemon getPokemonByName(@PathVariable("name") String name);
}
