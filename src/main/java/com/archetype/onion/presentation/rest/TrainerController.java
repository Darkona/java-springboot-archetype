package com.archetype.onion.presentation.rest;

import com.archetype.onion.application.ports.in.TrainerUseCase;
import com.archetype.onion.domain.model.PokemonOwnership;
import com.archetype.onion.domain.model.Trainer;
import com.archetype.onion.presentation.dto.PokemonOwnershipDTO;
import com.archetype.onion.presentation.dto.TrainerDTO;
import com.archetype.onion.presentation.mapper.TrainerMapper;
import io.github.darkona.logged.Logged;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for trainer operations in the onion architecture.
 * Presentation layer component that delegates to the application layer through ports.
 * Follows ADR 0015 (Prefer Spring annotations over ResponseEntity) for clean controller design.
 */
@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Logged
@Tag(name = "Trainer", description = "Pokemon Trainer management API")
public class TrainerController {

    private final TrainerUseCase trainerUseCase;
    private final TrainerMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new trainer")
    public TrainerDTO createTrainer(@RequestBody TrainerDTO trainerDTO) {
        try {
            Trainer trainer = mapper.toDomain(trainerDTO);
            Trainer created = trainerUseCase.createTrainer(trainer);
            return mapper.toDTO(created);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid trainer data");
        }
    }

    @PostMapping("/{trainerId}/pokemon")
    @Operation(summary = "Add a Pokemon to a trainer's collection")
    public TrainerDTO addPokemon(
            @PathVariable String trainerId,
            @RequestBody PokemonOwnershipDTO ownershipDTO) {
        try {
            PokemonOwnership ownership = mapper.toDomain(ownershipDTO);
            Trainer updated = trainerUseCase.addPokemonToTrainer(trainerId, ownership);
            return mapper.toDTO(updated);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found: " + trainerId);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pokemon already assigned to trainer");
        }
    }

    @GetMapping("/{trainerId}")
    @Operation(summary = "Get a trainer by ID")
    public TrainerDTO getTrainer(@PathVariable String trainerId) {
        return trainerUseCase.getTrainer(trainerId)
                             .map(mapper::toDTO)
                             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found: " + trainerId));
    }

    @GetMapping
    @Operation(summary = "List all trainers")
    public List<TrainerDTO> listTrainers() {
        List<Trainer> trainers = trainerUseCase.listTrainers();
        return mapper.toDTOList(trainers);
    }

    @DeleteMapping("/{trainerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a trainer")
    public void deleteTrainer(@PathVariable String trainerId) {
        boolean deleted = trainerUseCase.deleteTrainer(trainerId);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found: " + trainerId);
        }
    }
}
