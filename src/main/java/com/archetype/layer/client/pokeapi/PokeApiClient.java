package com.archetype.layer.client.pokeapi;

import com.archetype.layer.client.pokeapi.dto.PokeApiAbility;
import com.archetype.layer.client.pokeapi.dto.PokeApiPokemon;
import io.github.darkona.logged.Logged;
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
     * @param id Pokemon national ID
     * @return Pokemon data from PokeAPI
     */
    @GetMapping("/api/v2/pokemon/{id}")
    @Logged
    PokeApiPokemon getPokemonById(@PathVariable("id") int id);

    @GetMapping("/api/v2/ability/{name}")
    PokeApiAbility getAbilityByName(@PathVariable String name);

}
