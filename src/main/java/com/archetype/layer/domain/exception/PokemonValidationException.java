package com.archetype.layer.domain.exception;

/**
 * Domain exception thrown when Pokemon business logic validation fails.
 * Results in HTTP 422 Unprocessable Entity responses.
 * <p>
 * This exception is for business rule violations, not format/constraint validation
 * (which is handled by @Valid annotations).
 * <p>
 * Follows ADR 0003 (Domain validation and robustness) and ADR 0016 (Exception handling strategy).
 */
public class PokemonValidationException extends LayerDomainException {

    /**
     * Create exception for invalid Pokemon level.
     *
     * @param level    The invalid level value
     * @param minLevel The minimum allowed level
     * @param maxLevel The maximum allowed level
     */
    public PokemonValidationException(int level, int minLevel, int maxLevel) {
        super("pokemon.validation.level.range", level, minLevel, maxLevel);
    }


    /**
     * Private constructor for generic validation failures.
     */
    private PokemonValidationException(String errorCode, Object... messageArgs) {
        super(errorCode, messageArgs);
    }

    /**
     * Create exception for generic Pokemon validation failure.
     *
     * @param field  The field that failed validation
     * @param value  The invalid value
     * @param reason The reason for failure
     */
    public static PokemonValidationException of(String field, Object value, String reason) {
        return new PokemonValidationException("pokemon.validation.field", field, value, reason);
    }
}
