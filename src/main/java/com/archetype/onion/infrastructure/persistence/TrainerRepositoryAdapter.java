package com.archetype.onion.infrastructure.persistence;

import com.archetype.onion.application.ports.out.TrainerRepositoryPort;
import com.archetype.onion.domain.model.PokemonOwnership;
import com.archetype.onion.domain.model.Trainer;
import io.github.darkona.logged.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing the trainer repository port using MongoDB.
 * Handles mapping between domain models and MongoDB documents.
 */
@Component
@RequiredArgsConstructor
@Logged
public class TrainerRepositoryAdapter implements TrainerRepositoryPort {
    
    private final TrainerMongoRepository mongoRepository;
    
    @Override
    public Trainer save(Trainer trainer) {
        TrainerDocument document = toDocument(trainer);
        TrainerDocument saved = mongoRepository.save(document);
        return toDomain(saved);
    }
    
    @Override
    public Optional<Trainer> findById(String trainerId) {
        return mongoRepository.findById(trainerId)
            .map(this::toDomain);
    }
    
    @Override
    public List<Trainer> findAll() {
        return mongoRepository.findAll().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean deleteById(String trainerId) {
        if (mongoRepository.existsById(trainerId)) {
            mongoRepository.deleteById(trainerId);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean existsById(String trainerId) {
        return mongoRepository.existsById(trainerId);
    }
    
    /**
     * Convert domain model to MongoDB document.
     */
    private TrainerDocument toDocument(Trainer trainer) {
        List<TrainerDocument.PokemonOwnershipDocument> ownershipDocs = trainer.getOwnedPokemons().stream()
            .map(o -> TrainerDocument.PokemonOwnershipDocument.builder()
                .pokemonId(o.getPokemonId())
                .nickname(o.getNickname())
                .acquiredAt(o.getAcquiredAt())
                .build())
            .collect(Collectors.toList());
        
        return TrainerDocument.builder()
            .id(trainer.getId())
            .name(trainer.getName())
            .badges(trainer.getBadges())
            .ownedPokemons(ownershipDocs)
            .createdAt(trainer.getCreatedAt())
            .updatedAt(trainer.getUpdatedAt())
            .build();
    }
    
    /**
     * Convert MongoDB document to domain model.
     */
    private Trainer toDomain(TrainerDocument document) {
        List<PokemonOwnership> ownerships = document.getOwnedPokemons().stream()
            .map(o -> PokemonOwnership.builder()
                .pokemonId(o.getPokemonId())
                .nickname(o.getNickname())
                .acquiredAt(o.getAcquiredAt())
                .build())
            .collect(Collectors.toList());
        
        return Trainer.builder()
            .id(document.getId())
            .name(document.getName())
            .badges(document.getBadges())
            .ownedPokemons(ownerships)
            .createdAt(document.getCreatedAt())
            .updatedAt(document.getUpdatedAt())
            .build();
    }
}
