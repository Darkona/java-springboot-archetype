package com.archetype.layer.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating Pokemon.
 * <p>
 * Includes Bean Validation annotations for format/constraint validation.
 * Business logic validation is handled separately in the service layer.
 * <p>
 * Follows ADR 0016 (Exception handling strategy) for hybrid validation approach.
 */
public record PokemonCreate(

        @NotNull(message = "pokemon.national-id.required")
        @Min(value = 1, message = "pokemon.national-id.min")
        @Max(value = 1010, message = "pokemon.national-id.max")
        int nationalId,

        @NotBlank(message = "pokemon.name.required")
        @Size(max = 50, message = "pokemon.name.max-length")
        String name

) {
}
