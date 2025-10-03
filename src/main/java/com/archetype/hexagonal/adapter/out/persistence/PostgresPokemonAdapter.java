package com.archetype.hexagonal.adapter.out.persistence;

import com.archetype.hexagonal.application.port.out.PokemonRepositoryPort;
import com.archetype.hexagonal.domain.model.PokemonPet;
import com.archetype.hexagonal.adapter.out.mapper.PokemonPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Postgres adapter implementing the outbound port using Spring Data JPA.
 */
@Repository
public class PostgresPokemonAdapter implements PokemonRepositoryPort {

    private final PokemonPetJpaRepository jpaRepository;

    public PostgresPokemonAdapter(PokemonPetJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PokemonPet save(PokemonPet pet) {
        PokemonPetEntity entity = PokemonPersistenceMapper.toEntity(pet);
        PokemonPetEntity saved = jpaRepository.save(entity);
        return PokemonPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<PokemonPet> findById(UUID id) {
        return jpaRepository.findById(id).map(PokemonPersistenceMapper::toDomain);
    }

    @Override
    public List<PokemonPet> findAvailable() {
        return jpaRepository.findByAvailableTrue()
                .stream()
                .map(PokemonPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}

