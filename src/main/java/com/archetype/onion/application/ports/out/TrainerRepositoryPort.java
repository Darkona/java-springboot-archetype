package com.archetype.onion.application.ports.out;

import com.archetype.onion.domain.model.Trainer;

import java.util.List;
import java.util.Optional;

/**
 * Output port for trainer persistence operations.
 * This interface defines the contract for the repository adapter.
 */
public interface TrainerRepositoryPort {
    
    /**
     * Save a trainer.
     *
     * @param trainer the trainer to save
     * @return the saved trainer
     */
    Trainer save(Trainer trainer);
    
    /**
     * Find a trainer by ID.
     *
     * @param trainerId the trainer's ID
     * @return the trainer if found
     */
    Optional<Trainer> findById(String trainerId);
    
    /**
     * Find all trainers.
     *
     * @return list of all trainers
     */
    List<Trainer> findAll();
    
    /**
     * Delete a trainer by ID.
     *
     * @param trainerId the trainer's ID
     * @return true if deleted, false if not found
     */
    boolean deleteById(String trainerId);
    
    /**
     * Check if a trainer exists by ID.
     *
     * @param trainerId the trainer's ID
     * @return true if exists, false otherwise
     */
    boolean existsById(String trainerId);
}
