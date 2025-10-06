package com.archetype.hexagonal.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request DTO for creating Pokemon in the hexagonal architecture module.
 * 
 * Follows ADR 0017 (Java 21 language features) by using records for DTOs.
 * Includes Bean Validation for format/constraint validation.
 */
public record PokemonCreateRequest(
    @NotBlank(message = "Pokemon name is required")
    String name,
    
    @NotEmpty(message = "Pokemon must have at least one type")
    List<String> types
) {}
