package com.archetype.layer.client.pokeapi;

import com.archetype.layer.client.pokeapi.mapper.PokeApiMapper;
import com.archetype.layer.domain.model.Pokemon;
import com.archetype.layer.service.PokemonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Service for populating the database with Pokemon data from PokeAPI.
 * Fetches the first 151 Pokemon from PokeAPI and saves them to the MongoDB database.
 * Follows ADR 0007 (Prefer OpenFeign) and ADR 0002 (Domain separation and mapping).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PokeApiDataService {

    private final PokeApiClient pokeApiClient;
    private final PokeApiMapper pokeApiMapper;
    private final PokemonService pokemonService;

    private final Executor executor = Executors.newFixedThreadPool(5); // Limit concurrent requests

    /**
     * Populate the database with the first 151 Pokemon from PokeAPI.
     * Uses batch processing and parallel requests with rate limiting.
     *
     * @return List of successfully saved Pokemon
     */
    public List<Pokemon> populateFirstGenerationPokemon() {
        log.info("Starting population of first generation Pokemon (1-151) from PokeAPI");

        List<Pokemon> savedPokemon = new ArrayList<>();
        List<CompletableFuture<Pokemon>> futures = new ArrayList<>();

        // Create async tasks for each Pokemon (1-151)
        for (int nationalId = 1; nationalId <= 151; nationalId++) {
            final int pokemonId = nationalId;
            CompletableFuture<Pokemon> future = CompletableFuture
                    .supplyAsync(() -> fetchAndSavePokemon(pokemonId), executor)
                    .exceptionally(throwable -> {
                        log.error("Failed to fetch Pokemon with ID {}: {}", pokemonId, throwable.getMessage());
                        return null;
                    });
            futures.add(future);

            // Small delay to avoid overwhelming PokeAPI
            try {
                Thread.sleep(100); // 100ms delay between requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Population interrupted");
                break;
            }
        }

        // Wait for all futures to complete and collect results
        for (CompletableFuture<Pokemon> future : futures) {
            try {
                Pokemon pokemon = future.get();
                if (pokemon != null) {
                    savedPokemon.add(pokemon);
                }
            } catch (Exception e) {
                log.error("Error waiting for Pokemon population: {}", e.getMessage());
            }
        }

        log.info("Successfully populated {} Pokemon from PokeAPI", savedPokemon.size());
        return savedPokemon;
    }

    /**
     * Fetch a single Pokemon from PokeAPI and save to database.
     *
     * @param nationalId Pokemon national ID (1-151)
     * @return Saved Pokemon or null if failed
     */
    public Pokemon fetchAndSavePokemon(int nationalId) {
        try {
            log.debug("Fetching Pokemon with national ID: {}", nationalId);

            // Check if Pokemon already exists
            if (pokemonService.existsByNationalId(nationalId)) {
                log.debug("Pokemon with national ID {} already exists, skipping", nationalId);
                return pokemonService.findByNationalId(nationalId);
            }

            // Fetch from PokeAPI
            var pokeApiPokemon = pokeApiClient.getPokemonById(nationalId);

            // Map to domain model
            Pokemon pokemon = pokeApiMapper.toDomainPokemon(pokeApiPokemon);

            // Save to database
            Pokemon saved = pokemonService.createPokemon(pokemon);

            log.debug("Successfully saved Pokemon: {} (ID: {})", saved.name(), saved.nationalId());
            return saved;

        } catch (Exception e) {
            log.error("Failed to fetch and save Pokemon with national ID {}: {}", nationalId, e.getMessage());
            return null;
        }
    }

    /**
     * Populate a specific range of Pokemon.
     * Useful for testing or partial population.
     *
     * @param startId Starting national ID (inclusive)
     * @param endId   Ending national ID (inclusive)
     * @return List of successfully saved Pokemon
     */
    public List<Pokemon> populatePokemonRange(int startId, int endId) {
        log.info("Populating Pokemon range: {} to {}", startId, endId);

        List<Pokemon> savedPokemon = new ArrayList<>();

        for (int nationalId = startId; nationalId <= endId; nationalId++) {
            Pokemon pokemon = fetchAndSavePokemon(nationalId);
            if (pokemon != null) {
                savedPokemon.add(pokemon);
            }

            // Small delay between requests
            try {
                Thread.sleep(200); // 200ms delay for smaller batches
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Population interrupted");
                break;
            }
        }

        log.info("Successfully populated {} Pokemon from range {}-{}", savedPokemon.size(), startId, endId);
        return savedPokemon;
    }

    /**
     * Get the count of Pokemon currently in the database.
     *
     * @return Number of Pokemon in database
     */
    public long getCurrentPokemonCount() {
        return pokemonService.countAll();
    }

    /**
     * Check if the database has been populated with first generation Pokemon.
     *
     * @return true if database has 150+ Pokemon (allowing for some failures)
     */
    public boolean isFirstGenerationPopulated() {
        return getCurrentPokemonCount() >= 150;
    }
}
