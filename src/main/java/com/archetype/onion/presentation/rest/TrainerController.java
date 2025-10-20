package com.archetype.onion.presentation.rest;

import com.archetype.onion.application.ports.in.TrainerUseCase;
import com.archetype.onion.domain.model.PokemonOwnership;
import com.archetype.onion.domain.model.Trainer;
import com.archetype.onion.presentation.dto.PokemonOwnershipDTO;
import com.archetype.onion.presentation.dto.TrainerDTO;
import com.archetype.onion.presentation.mapper.TrainerMapper;
import io.github.darkona.logged.Logged;
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
public class TrainerController implements TrainerControllerInfo {


    private final TrainerUseCase trainerUseCase;
    private final TrainerMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public TrainerDTO createTrainer(@RequestBody TrainerDTO trainerDTO) {
        try {
            Trainer trainer = mapper.toDomain(trainerDTO);
            Trainer created = trainerUseCase.createTrainer(trainer);
            return mapper.toDTO(created);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid trainer data");
        }
    }

    @Override
    @PostMapping("/{trainerId}/pokemon")
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

    @Override
    @GetMapping("/{trainerId}")
    public TrainerDTO getTrainer(@PathVariable String trainerId) {
        return trainerUseCase.getTrainer(trainerId)
                             .map(mapper::toDTO)
                             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found: " + trainerId));
    }

    @Override
    @GetMapping
    public List<TrainerDTO> listTrainers() {
        List<Trainer> trainers = trainerUseCase.listTrainers();
        return mapper.toDTOList(trainers);
    }

    @Override
    @DeleteMapping("/{trainerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainer(@PathVariable String trainerId) {
        boolean deleted = trainerUseCase.deleteTrainer(trainerId);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found: " + trainerId);
        }
    }
}
