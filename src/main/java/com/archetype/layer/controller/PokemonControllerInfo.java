package com.archetype.layer.controller;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.persistence.document.PokemonDocument;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;

/**
 * API documentation interface for PokemonController.
 * Contains OpenAPI / springdoc annotations so the implementation stays free of documentation noise.
 */
@Tag(name = "Pokemon API", description = "Operations to manage Pokemon")
public interface PokemonControllerInfo {

    @Operation(summary = "Create a new Pokemon", description = "Creates a new Pokemon and returns the persisted document")
    @ApiResponse(responseCode = "201", description = "Created")
    ResponseEntity<PokemonDocument> createPokemon(@Parameter(description = "Pokemon create payload") PokemonCreate req);

    @Operation(summary = "Get a Pokemon", description = "Retrieve a Pokemon by id")
    @ApiResponse(responseCode = "200", description = "OK")
    ResponseEntity<PokemonDocument> getPokemon(
            @Parameter(description = "The Pokemon id") UUID id
    );

    @Operation(summary = "Update a Pokemon", description = "Update an existing Pokemon by id")
    @ApiResponse(responseCode = "200", description = "Updated")
    ResponseEntity<PokemonDocument> updatePokemon(
            @Parameter(description = "The Pokemon id") UUID id,
            @Parameter(description = "Pokemon update payload") PokemonCreate req
    );

    @Operation(summary = "Delete a Pokemon", description = "Delete a Pokemon by id")
    @ApiResponse(responseCode = "204", description = "No Content")
    ResponseEntity<Void> deletePokemon(
            @Parameter(description = "The Pokemon id") UUID id
    );

    @Operation(summary = "List Pokemons", description = "Get all Pokemons")
    @ApiResponse(responseCode = "200", description = "OK")
    ResponseEntity<List<PokemonDocument>> getAllPokemons();
}

