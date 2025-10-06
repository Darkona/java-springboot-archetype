package com.archetype.hexagonal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

//@Repository
public interface PokemonPetJpaRepository extends JpaRepository<PokemonPetEntity, UUID> {
    List<PokemonPetEntity> findByAvailableTrue();
}

