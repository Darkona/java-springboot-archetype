package com.archetype.layer.controller;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.domain.dto.response.PokemonDetails;
import com.archetype.layer.domain.dto.response.PokemonOverview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * API documentation interface for PokemonController.
 * Contains OpenAPI / springdoc annotations so the implementation stays free of documentation noise.
 * 
 * Updated to follow ADR 0015 (Prefer Spring annotations over ResponseEntity) and 
 * ADR 0016 (Exception handling strategy).
 */
@Tag(name = "Pokemon API", description = "Operations to manage Pokemon")
public interface PokemonControllerInfo {

    @Operation(summary = "Create a new Pokemon", description = "Creates a new Pokemon and returns the persisted document")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad Request - Validation failed")
    @ApiResponse(responseCode = "409", description = "Conflict - Pokemon already exists")
    PokemonDetails createPokemon(@Parameter(description = "Pokemon create payload") @Valid PokemonCreate req);

    @Operation(summary = "Get a Pokemon", description = "Retrieve a Pokemon by id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found - Pokemon does not exist")
    PokemonDetails getPokemon(
            @Parameter(description = "The Pokemon id") UUID id
    );

    @Operation(summary = "Update a Pokemon", description = "Update an existing Pokemon by id")
    @ApiResponse(responseCode = "200", description = "Updated")
    @ApiResponse(responseCode = "400", description = "Bad Request - Validation failed")
    @ApiResponse(responseCode = "404", description = "Not Found - Pokemon does not exist")
    @ApiResponse(responseCode = "409", description = "Conflict - Pokemon already exists")
    PokemonDetails updatePokemon(
            @Parameter(description = "The Pokemon id") UUID id,
            @Parameter(description = "Pokemon update payload") @Valid PokemonCreate req
    );

    @Operation(summary = "Delete a Pokemon", description = "Delete a Pokemon by id")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "404", description = "Not Found - Pokemon does not exist")
    void deletePokemon(
            @Parameter(description = "The Pokemon id") UUID id
    );

    @Operation(summary = "List Pokemons", description = "Get all Pokemons")
    @ApiResponse(responseCode = "200", description = "OK")
    List<PokemonOverview> getAllPokemons();
}
