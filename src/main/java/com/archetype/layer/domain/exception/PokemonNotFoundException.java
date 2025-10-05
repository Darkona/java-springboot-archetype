package com.archetype.layer.domain.exception;

import java.util.UUID;

/**
 * Domain exception thrown when a Pokemon cannot be found.
 * Results in HTTP 404 Not Found responses.
 * 
 * Follows ADR 0003 (Domain validation and robustness) and ADR 0016 (Exception handling strategy).
 */
public class PokemonNotFoundException extends LayerDomainException {
    
    /**
     * Create exception for Pokemon not found by UUID.
     * 
     * @param id The Pokemon UUID that was not found
     */
    public PokemonNotFoundException(UUID id) {
        super("pokemon.not-found", id);
    }
    
    /**
     * Create exception for Pokemon not found by national ID.
     * 
     * @param nationalId The Pokemon national ID that was not found
     */
    public PokemonNotFoundException(int nationalId) {
        super("pokemon.not-found.national-id", nationalId);
    }
    
    /**
     * Create exception for Pokemon not found with custom identifier.
     * 
     * @param identifier The identifier that was not found
     */
    public PokemonNotFoundException(String identifier) {
        super("pokemon.not-found.identifier", identifier);
    }
}
