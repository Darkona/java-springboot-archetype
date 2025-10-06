package com.archetype.mvc.exception;

import java.util.UUID;

/**
 * Domain exception thrown when attempting to create a Pokemon that already exists.
 * Results in HTTP 409 Conflict responses.
 * <p>
 * Follows ADR 0003 (Domain validation and robustness) and ADR 0016 (Exception handling strategy).
 */
public class PokemonAlreadyExistsException extends LayerDomainException {

    /**
     * Create exception for Pokemon that already exists by national ID.
     *
     * @param nationalId The Pokemon national ID that already exists
     */
    public PokemonAlreadyExistsException(int nationalId) {
        super("pokemon.already-exists.national-id", nationalId);
    }

    /**
     * Create exception for Pokemon that already exists by UUID.
     *
     * @param id The Pokemon UUID that already exists
     */
    public PokemonAlreadyExistsException(UUID id) {
        super("pokemon.already-exists", id);
    }

    /**
     * Create exception for Pokemon that already exists by name.
     *
     * @param name The Pokemon name that already exists
     */
    public PokemonAlreadyExistsException(String name) {
        super("pokemon.already-exists.name", name);
    }
}
