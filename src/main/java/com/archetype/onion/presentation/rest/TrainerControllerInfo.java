package com.archetype.onion.presentation.rest;

import com.archetype.onion.presentation.dto.PokemonOwnershipDTO;
import com.archetype.onion.presentation.dto.TrainerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Tag(name = "Trainer", description = "Pokemon Trainer management API")
public interface TrainerControllerInfo {

    @Operation(summary = "Create a new trainer")
    TrainerDTO createTrainer(@RequestBody TrainerDTO trainerDTO);

    @Operation(summary = "Add a Pokemon to a trainer's collection")
    TrainerDTO addPokemon(
            @PathVariable String trainerId,
            @RequestBody PokemonOwnershipDTO ownershipDTO);

    @Operation(summary = "Get a trainer by ID")
    TrainerDTO getTrainer(@PathVariable String trainerId);

    @Operation(summary = "List all trainers")
    List<TrainerDTO> listTrainers();

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a trainer")
    void deleteTrainer(@PathVariable String trainerId);
}
