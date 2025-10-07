package com.archetype.layer.service;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.domain.model.Pokemon;
import com.archetype.layer.domain.model.Species;
import com.archetype.layer.exception.PokemonAlreadyExistsException;
import com.archetype.layer.persistence.PokemonDataRepository;
import com.archetype.layer.persistence.document.PokemonDocument;
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
    private final PokeApiDataService pokeApiDataService;


        public Pokemon createPokemon(PokemonCreate pokemonCreate) {
        log.debug("Creating Pokemon with national ID: {}, name: {}", pokemonCreate.nationalId(), pokemonCreate.name());

        // Check if Pokemon already exists by national ID
        if (repository.existsByNationalId(pokemonCreate.nationalId())) {
            throw new PokemonAlreadyExistsException(pokemonCreate.nationalId());
        }

        try {
            // Create domain Pokemon from request DTO
            // In a real implementation, you'd lookup the Species from a species repository
            Species species = repository.getSpeciesById(pokemonCreate.nationalId());
            Pokemon pokemon = new Pokemon(species, pokemonCreate.name(), 1);

            // Map to document and save
            PokemonDocument document = repository.save(pokemon);
            document.setId(UUID.randomUUID());
            PokemonDocument saved = pokemonRepository.save(document);

            // Map back to domain model and return
            Pokemon created = persistenceMapper.toDomain(saved);
            log.info("Successfully created Pokemon: id={}, nationalId={}, name={}",
                    created.getName(), pokemonCreate.nationalId(), pokemonCreate.name());
            return created;

        } catch (Exception ex) {
            if (ex instanceof PokemonAlreadyExistsException) {
                throw ex; // Re-throw domain exceptions
            }
            throw new PokemonServiceException("create", pokemonCreate.nationalId(), ex);
        }
    }

    public Pokemon getPokemon(int id) {
//        log.debug("Retrieving Pokemon with ID: {}", id);
//
//        PokemonDocument document = repository.findById(id)
//                                                    .orElseThrow(() -> new PokemonNotFoundException(id));
//
//        return persistenceMapper.toDomain(document);
        return null;
    }
//
//    public Pokemon updatePokemon(UUID id, PokemonCreate pokemonCreate) {
//        log.debug("Updating Pokemon with ID: {}, new name: {}", id, pokemonCreate.name());
//
//        PokemonDocument existing = pokemonRepository.findById(id)
//                                                    .orElseThrow(() -> new PokemonNotFoundException(id));
//
//        // Check if national ID is being changed and would conflict
//        if (existing.getNationalId() != pokemonCreate.nationalId() &&
//                pokemonRepository.existsByNationalId(pokemonCreate.nationalId())) {
//            throw new PokemonAlreadyExistsException(pokemonCreate.nationalId());
//        }
//
//        try {
//            // Update the document with new data
//            existing.setName(pokemonCreate.name());
//            existing.setNationalId(pokemonCreate.nationalId());
//            // Add other update logic as needed
//
//            PokemonDocument updated = pokemonRepository.save(existing);
//            Pokemon result = persistenceMapper.toDomain(updated);
//
//            log.info("Successfully updated Pokemon: id={}, nationalId={}, name={}",
//                    id, pokemonCreate.nationalId(), pokemonCreate.name());
//            return result;
//
//        } catch (Exception ex) {
//            if (ex instanceof PokemonAlreadyExistsException) {
//                throw ex; // Re-throw domain exceptions
//            }
//            throw new PokemonServiceException("update", id, ex);
//        }
//    }
//
//    public void deletePokemon(UUID id) {
//        log.debug("Deleting Pokemon with ID: {}", id);
//
//        // Check if Pokemon exists before deletion
//        if (!pokemonRepository.existsById(id)) {
//            throw new PokemonNotFoundException(id);
//        }
//
//        try {
//            pokemonRepository.deleteById(id);
//            log.info("Successfully deleted Pokemon with ID: {}", id);
//        } catch (Exception ex) {
//            throw new PokemonServiceException("delete", id, ex);
//        }
//    }
//
//    public List<Pokemon> getAllPokemons() {
//        List<PokemonDocument> documents = pokemonRepository.findAll();
//        return documents.stream()
//                        .map(persistenceMapper::toDomain)
//                        .toList();
//    }

//    /**
//     * Create Pokemon from domain model (used by PokeAPI population service).
//     */
//    public Pokemon createPokemon(Pokemon pokemon) {
//        log.debug("Creating Pokemon from domain model: nationalId={}, name={}",
//                pokemon.getSpecies().nationalId(), pokemon.getName());
//
//        try {
//            PokemonDocument document = persistenceMapper.toDocument(pokemon);
//            document.setId(UUID.randomUUID());
//            PokemonDocument saved = pokemonRepository.save(document);
//
//            Pokemon result = persistenceMapper.toDomain(saved);
//            log.debug("Successfully created Pokemon from domain model: id={}", result.getName());
//            return result;
//
//        } catch (Exception ex) {
//            throw new PokemonServiceException("create from domain", pokemon.getSpecies().nationalId(), ex);
//        }
//    }
//
//    /**
//     * Check if Pokemon exists by national ID.
//     */
//    public boolean existsByNationalId(int nationalId) {
//        return pokemonRepository.existsByNationalId(nationalId);
//    }
//
//    /**
//     * Find Pokemon by national ID.
//     */
//    public Pokemon findByNationalId(int nationalId) {
//        log.debug("Finding Pokemon by national ID: {}", nationalId);
//
//        PokemonDocument document = pokemonRepository.findByNationalId(nationalId)
//                                                    .orElseThrow(() -> new PokemonNotFoundException(nationalId));
//
//        return persistenceMapper.toDomain(document);
//    }
//
//    /**
//     * Get total count of all Pokemon in database.
//     */
//    public long countAll() {
//        return pokemonRepository.count();
//    }


    public List<Species> loadSpecies() {
        List<Species> species = pokeApiDataService.getFirstGenerationSpecies();
        repository.saveAll(species);
        return species;
    }

    public List<Species> listAllSpecies() {
        return repository.getAllSpecies();
    }
}
