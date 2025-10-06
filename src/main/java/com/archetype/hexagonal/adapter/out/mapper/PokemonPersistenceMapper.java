package com.archetype.hexagonal.adapter.out.mapper;

import com.archetype.hexagonal.adapter.out.persistence.PokemonPetEntity;
import com.archetype.hexagonal.domain.model.PokemonPet;

import java.util.List;
import java.util.UUID;

/**
 * Simple manual mapping between JPA entity and domain model.
 * Types are stored as a collection in the entity, so mapping is direct.
 */
public final class PokemonPersistenceMapper {

    private PokemonPersistenceMapper() {
    }

    public static PokemonPet toDomain(PokemonPetEntity entity) {
        if (entity == null) return null;
        List<String> types = entity.getTypes();
        return PokemonPet.fromPersistence(
                entity.getId() != null ? entity.getId() : UUID.randomUUID(),
                entity.getName(),
                types,
                entity.isAvailable(),
                entity.getOwnerId(),
                entity.getCreatedAt()
        );
    }

    public static PokemonPetEntity toEntity(PokemonPet domain) {
        if (domain == null) return null;
        PokemonPetEntity e = new PokemonPetEntity();
        e.setId(domain.getId());
        e.setName(domain.getName());
        e.setTypes(domain.getTypes());
        e.setAvailable(domain.isAvailable());
        e.setOwnerId(domain.getOwnerId());
        e.setCreatedAt(domain.getCreatedAt());
        return e;
    }
}

