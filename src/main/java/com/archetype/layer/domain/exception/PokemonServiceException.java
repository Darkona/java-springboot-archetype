package com.archetype.layer.domain.exception;

/**
 * Domain exception thrown when Pokemon service operations fail due to internal issues.
 * Results in HTTP 500 Internal Server Error responses.
 * <p>
 * This exception is for service-layer failures that are not domain validation issues,
 * such as external service failures, data corruption, or unexpected system errors.
 * <p>
 * Follows ADR 0003 (Domain validation and robustness) and ADR 0016 (Exception handling strategy).
 */
public class PokemonServiceException extends LayerDomainException {

    /**
     * Create exception for Pokemon population service failure.
     *
     * @param reason The reason for the population failure
     */
    public PokemonServiceException(String reason) {
        super("pokemon.service.population-failed", reason);
    }

    /**
     * Create exception for Pokemon population service failure with underlying cause.
     *
     * @param reason The reason for the population failure
     * @param cause  The underlying cause
     */
    public PokemonServiceException(String reason, Throwable cause) {
        super("pokemon.service.population-failed", cause, reason);
    }

    /**
     * Create exception for Pokemon data consistency failure.
     *
     * @param operation The operation that failed
     * @param pokemonId The Pokemon ID involved in the failure
     */
    public PokemonServiceException(String operation, Object pokemonId) {
        super("pokemon.service.data-consistency", operation, pokemonId);
    }

    /**
     * Create exception for Pokemon data consistency failure with underlying cause.
     *
     * @param operation The operation that failed
     * @param pokemonId The Pokemon ID involved in the failure
     * @param cause     The underlying cause
     */
    public PokemonServiceException(String operation, Object pokemonId, Throwable cause) {
        super("pokemon.service.data-consistency", cause, operation, pokemonId);
    }

    /**
     * Private constructor for specific error codes.
     */
    private PokemonServiceException(String errorCode, Throwable cause, Object... messageArgs) {
        super(errorCode, cause, messageArgs);
    }

    /**
     * Create exception for external service integration failure.
     *
     * @param serviceName The name of the external service
     * @param operation   The operation that failed
     * @param cause       The underlying cause
     */
    public static PokemonServiceException externalServiceFailure(String serviceName, String operation, Throwable cause) {
        return new PokemonServiceException("pokemon.service.external-failure", cause, serviceName, operation);
    }
}
