package com.archetype.hexagonal.application.service;

import com.archetype.hexagonal.application.port.in.AdoptPokemon;
import com.archetype.hexagonal.application.port.in.ListAvailablePokemons;
import com.archetype.hexagonal.application.port.in.RegisterPokemon;
import com.archetype.hexagonal.application.port.out.EventPublisherPort;
import com.archetype.hexagonal.application.port.out.PokemonRepositoryPort;
import com.archetype.hexagonal.domain.model.PokemonPet;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Application service implementing inbound use-cases for the Petshop.
 * Respects hexagonal architecture: depends only on ports and domain.
 */
@Service
public class PokemonPetShopService implements RegisterPokemon, ListAvailablePokemons, AdoptPokemon {

    private final PokemonRepositoryPort repository;
    private final EventPublisherPort eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(PokemonPetShopService.class);

    public PokemonPetShopService(PokemonRepositoryPort repository, EventPublisherPort eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PokemonPet register(String name, List<String> types) {
        PokemonPet pet = PokemonPet.register(name, types);
        PokemonPet saved = repository.save(pet);
        log.info("Pokemon registered: id={}, name={}", saved.getId(), saved.getName());
        try {
            eventPublisher.publishPokemonRegistered(saved);
        } catch (Exception ignored) {
            // Keep service resilient to publisher failures; events are best-effort here.
        }
        return saved;
    }

    @Override
    public List<PokemonPet> listAvailable() {
        return repository.findAvailable();
    }

    @Override
    public PokemonPet adopt(UUID id, String ownerId) {
        PokemonPet pet = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Pokemon not found: " + id));
        PokemonPet adopted = pet.adopt(ownerId);
        PokemonPet saved = repository.save(adopted);
        log.info("Pokemon adopted: id={}, ownerId={}", saved.getId(), saved.getOwnerId());
        try {
            eventPublisher.publishPokemonAdopted(saved);
        } catch (Exception ignored) {
        }
        return saved;
    }

    public PokemonPet returned(UUID id) {
        PokemonPet pet = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Pokemon not found: " + id));
        PokemonPet returned = pet.returned();
        PokemonPet saved = repository.save(returned);
        log.info("Pokemon returned: id={}", saved.getId());
        try {
            eventPublisher.publishPokemonReturned(saved);
        } catch (Exception ignored) {
        }
        return saved;
    }
}

