package com.archetype.layer.client.pokemon;

import com.archetype.layer.client.pokemon.mapper.PokemonClientMapper;
import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.domain.dto.response.PokemonDetails;
import com.archetype.layer.domain.dto.response.PokemonOverview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service demonstrating how to use the Pokemon Feign client.
 * Shows integration between client layer and domain layer using proper mapping.
 * Follows ADR 0007 (Prefer OpenFeign) and ADR 0002 (Domain separation and mapping).
 * <p>
 * Located in the client.pokemon package to encapsulate all client implementation together.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PokemonClientService {

    private final PokemonClient pokemonClient;
    private final PokemonClientMapper clientMapper;

    /**
     * Create a Pokemon using the external service client.
     *
     * @param request Domain DTO for Pokemon creation
     * @return Domain DTO with Pokemon details
     */
    public PokemonDetails createPokemonViaClient(PokemonCreate request) {
        log.info("Creating Pokemon via client: nationalId={}, name={}",
                request.nationalId(), request.name());

        try {
            // Map domain DTO to client DTO
            var clientRequest = clientMapper.toClientCreate(request);

            // Call external service via Feign client
            ResponseEntity<com.archetype.layer.client.pokemon.dto.PokemonClientDetails> response =
                    pokemonClient.createPokemon(clientRequest);

            // Map client response back to domain DTO
            if (response.getBody() != null) {
                return clientMapper.toDomainDetails(response.getBody());
            } else {
                throw new RuntimeException("Empty response from Pokemon service");
            }

        } catch (Exception e) {
            log.error("Failed to create Pokemon via client", e);
            throw new RuntimeException("Pokemon creation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get Pokemon details using the external service client.
     *
     * @param id Pokemon ID
     * @return Domain DTO with Pokemon details
     */
    public PokemonDetails getPokemonViaClient(UUID id) {
        log.info("Getting Pokemon via client: id={}", id);

        try {
            ResponseEntity<com.archetype.layer.client.pokemon.dto.PokemonClientDetails> response =
                    pokemonClient.getPokemon(id);

            if (response.getBody() != null) {
                return clientMapper.toDomainDetails(response.getBody());
            } else {
                throw new RuntimeException("Pokemon not found: " + id);
            }

        } catch (Exception e) {
            log.error("Failed to get Pokemon via client: id={}", id, e);
            throw new RuntimeException("Pokemon retrieval failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get all Pokemon using the external service client.
     *
     * @return List of domain DTOs with Pokemon overviews
     */
    public List<PokemonOverview> getAllPokemonsViaClient() {
        log.info("Getting all Pokemon via client");

        try {
            ResponseEntity<List<com.archetype.layer.client.pokemon.dto.PokemonClientOverview>> response =
                    pokemonClient.getAllPokemons();

            if (response.getBody() != null) {
                return response.getBody().stream()
                               .map(clientMapper::toDomainOverview)
                               .toList();
            } else {
                return List.of();
            }

        } catch (Exception e) {
            log.error("Failed to get all Pokemon via client", e);
            throw new RuntimeException("Pokemon list retrieval failed: " + e.getMessage(), e);
        }
    }

    /**
     * Update Pokemon using the external service client.
     *
     * @param id      Pokemon ID
     * @param request Domain DTO for Pokemon update
     * @return Domain DTO with updated Pokemon details
     */
    public PokemonDetails updatePokemonViaClient(UUID id, PokemonCreate request) {
        log.info("Updating Pokemon via client: id={}, nationalId={}, name={}",
                id, request.nationalId(), request.name());

        try {
            var clientRequest = clientMapper.toClientCreate(request);

            ResponseEntity<com.archetype.layer.client.pokemon.dto.PokemonClientDetails> response =
                    pokemonClient.updatePokemon(id, clientRequest);

            if (response.getBody() != null) {
                return clientMapper.toDomainDetails(response.getBody());
            } else {
                throw new RuntimeException("Empty response from Pokemon service");
            }

        } catch (Exception e) {
            log.error("Failed to update Pokemon via client: id={}", id, e);
            throw new RuntimeException("Pokemon update failed: " + e.getMessage(), e);
        }
    }

    /**
     * Delete Pokemon using the external service client.
     *
     * @param id Pokemon ID
     */
    public void deletePokemonViaClient(UUID id) {
        log.info("Deleting Pokemon via client: id={}", id);

        try {
            pokemonClient.deletePokemon(id);
            log.info("Successfully deleted Pokemon: id={}", id);

        } catch (Exception e) {
            log.error("Failed to delete Pokemon via client: id={}", id, e);
            throw new RuntimeException("Pokemon deletion failed: " + e.getMessage(), e);
        }
    }
}
