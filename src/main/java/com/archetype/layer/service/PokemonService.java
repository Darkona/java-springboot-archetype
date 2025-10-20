package com.archetype.layer.service;

import com.archetype.layer.client.pokeapi.PokeApiAdapter;
import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.domain.dto.response.PokemonDetails;
import com.archetype.layer.domain.dto.response.SpeciesResponse;
import com.archetype.layer.domain.model.Pokemon;
import com.archetype.layer.domain.model.Species;
import com.archetype.layer.exception.PokemonAlreadyExistsException;
import com.archetype.layer.exception.PokemonNotFoundException;
import com.archetype.layer.exception.PokemonServiceException;
import com.archetype.layer.mapper.dto.PokemonDtoMapper;
import com.archetype.layer.mapper.dto.SpeciesDtoMapper;
import com.archetype.layer.persistence.PokemonDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service layer working with domain models.
 * Following layered architecture - uses domain models and maps to/from persistence via mapper.
 * <p>
 * Follows ADR 0003 (Domain validation and robustness) and ADR 0016 (Exception handling strategy)
 * for proper exception handling with domain-specific exceptions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PokemonService {

    private final PokemonDataRepository repository;
    private final SpeciesDtoMapper speciesMapper;
    private final PokeApiAdapter pokeApiAdapter;
    private final PokemonDtoMapper dtoMapper;

        public PokemonDetails createPokemon(PokemonCreate pokemonCreate) {
        log.debug("Creating Pokemon with national ID: {}, name: {}", pokemonCreate.nationalId(), pokemonCreate.name());

        // Check if Pokemon already exists by national ID
        if (repository.existsByNationalId(pokemonCreate.nationalId())) {
            throw new PokemonAlreadyExistsException(pokemonCreate.nationalId());
        }

        try {

            Species species = repository.getSpeciesById(pokemonCreate.nationalId());
            Pokemon pokemon = new Pokemon(species, pokemonCreate.name(), 1);
            return dtoMapper.toDto(repository.save(pokemon));

        } catch (Exception ex) {
            if (ex instanceof PokemonAlreadyExistsException) {
                throw ex; // Re-throw domain exceptions
            }
            throw new PokemonServiceException("create", pokemonCreate.nationalId(), ex);
        }
    }

    public PokemonDetails getPokemon(UUID id) {
        log.debug("Retrieving Pokemon with ID: {}", id);
        return dtoMapper.toDto(repository.getPokemonById(id));
    }

    public Pokemon updatePokemon(UUID id, PokemonCreate pokemonCreate) {
        log.debug("Updating Pokemon with ID: {}, new name: {}", id, pokemonCreate.name());

        Pokemon existing = repository.getPokemonById(id);

        try {
            existing.setName(pokemonCreate.name());
            // Add other update logic as needed
            return repository.save(existing);

        } catch (Exception ex) {
            if (ex instanceof PokemonNotFoundException) {
                throw ex;
            }
            throw new PokemonServiceException("update", id, ex);
        }
    }

    public void deletePokemon(UUID id) {
        log.debug("Deleting Pokemon with ID: {}", id);

        // Check if Pokemon exists before deletion
        if (!repository.pokemonExistsById(id)) {
            throw new PokemonNotFoundException(id);
        }

        try {
            repository.deletePokemon(id);
            log.info("Successfully deleted Pokemon with ID: {}", id);
        } catch (Exception ex) {
            throw new PokemonServiceException("delete", id, ex);
        }
    }

    public List<Pokemon> getAllPokemons() {
        return repository.getAllPokemon();
    }

    /**
     * Find Pokemon by national ID.
     */
    public List<Pokemon> findByNationalId(int nationalId) {
        log.debug("Finding Pokemon by national ID: {}", nationalId);

        return repository.getAllPokemonOfNationalId(nationalId);
    }

    public List<SpeciesResponse> loadSpecies() {
        List<Species> species = pokeApiAdapter.getFirstGenerationSpecies();
        repository.saveAll(species);
        return speciesMapper.toDto(species);
    }

    public List<SpeciesResponse> listAllSpecies() {
        return speciesMapper.toDto(repository.getAllSpecies());
    }


}
