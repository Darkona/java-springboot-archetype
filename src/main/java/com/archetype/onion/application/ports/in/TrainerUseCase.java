package com.archetype.onion.application.ports.in;

import com.archetype.onion.domain.model.PokemonOwnership;
import com.archetype.onion.domain.model.Trainer;

import java.util.List;
import java.util.Optional;

/**
 * Input port defining trainer-related use cases.
 * This interface represents the application's entry point for trainer operations.
 */
public interface TrainerUseCase {

    /**
     * Create a new trainer.
     *
     * @param trainer the trainer to create
     * @return the created trainer with generated ID
     */
    Trainer createTrainer(Trainer trainer);

    /**
     * Add a Pokemon to a trainer's collection.
     *
     * @param trainerId the trainer's ID
     * @param ownership the Pokemon ownership details
     * @return the updated trainer
     * @throws IllegalArgumentException if trainer not found
     * @throws IllegalStateException    if trainer already has maximum Pokemon or nickname conflict
     */
    Trainer addPokemonToTrainer(String trainerId, PokemonOwnership ownership);

    /**
     * Get a trainer by ID.
     *
     * @param trainerId the trainer's ID
     * @return the trainer if found
     */
    Optional<Trainer> getTrainer(String trainerId);

    /**
     * List all trainers.
     *
     * @return list of all trainers
     */
    List<Trainer> listTrainers();

    /**
     * Delete a trainer.
     *
     * @param trainerId the trainer's ID
     * @return true if deleted, false if not found
     */
    boolean deleteTrainer(String trainerId);
}
