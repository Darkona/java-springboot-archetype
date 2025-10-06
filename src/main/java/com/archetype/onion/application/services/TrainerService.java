package com.archetype.onion.application.services;

import com.archetype.onion.application.ports.in.TrainerUseCase;
import com.archetype.onion.application.ports.out.TrainerRepositoryPort;
import com.archetype.onion.domain.model.PokemonOwnership;
import com.archetype.onion.domain.model.Trainer;
import io.github.darkona.logged.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service implementing trainer use cases.
 * Coordinates domain logic and persistence through repository port.
 * Uses Redis caching for performance optimization.
 */
@Service
@RequiredArgsConstructor
@Logged
public class TrainerService implements TrainerUseCase {

    private final TrainerRepositoryPort repositoryPort;

    @Override
    @CachePut(value = "trainers", key = "#result.id")
    public Trainer createTrainer(Trainer trainer) {
        // Validate domain rules
        trainer.validate();

        // Generate ID and timestamps if not present
        if (trainer.getId() == null) {
            trainer.setId(UUID.randomUUID().toString());
        }

        Instant now = Instant.now();
        if (trainer.getCreatedAt() == null) {
            trainer.setCreatedAt(now);
        }
        trainer.setUpdatedAt(now);

        // Initialize badges to 0 if not set
        if (trainer.getBadges() == null) {
            trainer.setBadges(0);
        }

        return repositoryPort.save(trainer);
    }

    @Override
    @CachePut(value = "trainers", key = "#trainerId")
    public Trainer addPokemonToTrainer(String trainerId, PokemonOwnership ownership) {
        // Validate ownership
        ownership.validate();

        // Find trainer
        Trainer trainer = repositoryPort.findById(trainerId)
                                        .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainerId));

        // Set acquisition time if not present
        if (ownership.getAcquiredAt() == null) {
            ownership.setAcquiredAt(Instant.now());
        }

        // Add Pokemon (domain logic validates rules)
        trainer.addPokemon(ownership);

        // Save and return
        return repositoryPort.save(trainer);
    }

    @Override
    @Cacheable(value = "trainers", key = "#trainerId")
    public Optional<Trainer> getTrainer(String trainerId) {
        return repositoryPort.findById(trainerId);
    }

    @Override
    public List<Trainer> listTrainers() {
        return repositoryPort.findAll();
    }

    @Override
    @CacheEvict(value = "trainers", key = "#trainerId")
    public boolean deleteTrainer(String trainerId) {
        return repositoryPort.deleteById(trainerId);
    }
}
