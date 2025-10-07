package com.archetype.layer.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API documentation interface for PokemonController.
 * Contains OpenAPI / springdoc annotations so the implementation stays free of documentation noise.
 * <p>
 * Updated to follow ADR 0015 (Prefer Spring annotations over ResponseEntity) and
 * ADR 0016 (Exception handling strategy).
 */
@Tag(name = "Pokemon API", description = "Operations to manage Pokemon")
public interface PokemonControllerInfo {

}
