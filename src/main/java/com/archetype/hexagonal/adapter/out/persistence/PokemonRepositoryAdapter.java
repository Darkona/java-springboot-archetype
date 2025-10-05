package com.archetype.hexagonal.adapter.out.persistence;

import com.archetype.hexagonal.application.port.out.PokemonRepositoryPort;
import com.archetype.hexagonal.domain.model.PokemonPet;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Persistence adapter implementing the out port for the petshop module.
 * Uses a JPA repository and maps between the domain model and the JPA entity.
 */
@Component
@Primary
public class PokemonRepositoryAdapter implements PokemonRepositoryPort {

    private final PokemonPetJpaRepository jpaRepository;

    public PokemonRepositoryAdapter(PokemonPetJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public PokemonPet save(PokemonPet pet) {
        PokemonPetEntity entity = Mapper.toEntity(pet);
        PokemonPetEntity saved = jpaRepository.save(entity);
        return Mapper.toDomain(saved);
    }

    @Override
    public Optional<PokemonPet> findById(UUID id) {
        return jpaRepository.findById(id).map(Mapper::toDomain);
    }

    @Override
    public List<PokemonPet> findAvailable() {
        return jpaRepository.findByAvailableTrue().stream().map(Mapper::toDomain).collect(Collectors.toList());
    }

    /**
     * Simple mapper between entity and domain to avoid introducing another MapStruct mapper file.
     * Keep mapping logic here to ensure single-step change and clarity in this example.
     */
    static class Mapper {

        static PokemonPet toDomain(PokemonPetEntity e) {
            if (e == null) return null;
            return PokemonPet.fromPersistence(
                    e.getId(),
                    e.getName(),
                    e.getTypes(),
                    e.isAvailable(),
                    e.getOwnerId(),
                    e.getCreatedAt()
            );
        }

        static PokemonPetEntity toEntity(PokemonPet domain) {
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
}

