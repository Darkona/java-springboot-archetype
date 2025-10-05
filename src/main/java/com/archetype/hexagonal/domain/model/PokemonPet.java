package com.archetype.hexagonal.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model for Petshop example.
 */
public class PokemonPet {

    private final UUID id;
    private final String name;
    private final List<String> types;
    private final boolean available;
    private final String ownerId;
    private final Instant createdAt;

    private PokemonPet(UUID id, String name, List<String> types, boolean available, String ownerId, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.types = types;
        this.available = available;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    public static PokemonPet register(String name, List<String> types) {
        return new PokemonPet(UUID.randomUUID(), name, types, true, null, Instant.now());
    }

    /**
     * Factory used by persistence mappers to recreate a domain instance from stored state.
     * Keeps the main constructor private while allowing controlled recreation.
     */
    public static PokemonPet fromPersistence(UUID id, String name, List<String> types, boolean available, String ownerId, Instant createdAt) {
        return new PokemonPet(id, name, types, available, ownerId, createdAt);
    }

    public PokemonPet adopt(String ownerId) {
        if (!available) {
            throw new IllegalStateException("Pokemon is not available for adoption");
        }
        return new PokemonPet(this.id, this.name, this.types, false, ownerId, this.createdAt);
    }

    public PokemonPet returned() {
        return new PokemonPet(this.id, this.name, this.types, true, null, this.createdAt);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTypes() {
        return types;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PokemonPet that = (PokemonPet) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

