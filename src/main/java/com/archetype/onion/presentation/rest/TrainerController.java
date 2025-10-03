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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for trainer operations in the onion architecture.
 * Presentation layer component that delegates to the application layer through ports.
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
    @Operation(summary = "Create a new trainer")
    public ResponseEntity<TrainerDTO> createTrainer(@RequestBody TrainerDTO trainerDTO) {
        try {
            Trainer trainer = mapper.toDomain(trainerDTO);
            Trainer created = trainerUseCase.createTrainer(trainer);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{trainerId}/pokemon")
    @Operation(summary = "Add a Pokemon to a trainer's collection")
    public ResponseEntity<TrainerDTO> addPokemon(
            @PathVariable String trainerId,
            @RequestBody PokemonOwnershipDTO ownershipDTO) {
        try {
            PokemonOwnership ownership = mapper.toDomain(ownershipDTO);
            Trainer updated = trainerUseCase.addPokemonToTrainer(trainerId, ownership);
            return ResponseEntity.ok(mapper.toDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @GetMapping("/{trainerId}")
    @Operation(summary = "Get a trainer by ID")
    public ResponseEntity<TrainerDTO> getTrainer(@PathVariable String trainerId) {
        return trainerUseCase.getTrainer(trainerId)
            .map(mapper::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "List all trainers")
    public ResponseEntity<List<TrainerDTO>> listTrainers() {
        List<Trainer> trainers = trainerUseCase.listTrainers();
        return ResponseEntity.ok(mapper.toDTOList(trainers));
    }
    
    @DeleteMapping("/{trainerId}")
    @Operation(summary = "Delete a trainer")
    public ResponseEntity<Void> deleteTrainer(@PathVariable String trainerId) {
        boolean deleted = trainerUseCase.deleteTrainer(trainerId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
