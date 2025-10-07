package com.archetype.layer.controller;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.domain.dto.response.PokemonDetails;
import com.archetype.layer.domain.dto.response.PokemonOverview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

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
