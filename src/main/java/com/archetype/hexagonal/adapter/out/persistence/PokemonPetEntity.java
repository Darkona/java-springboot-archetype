package com.archetype.hexagonal.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity used by the petshop persistence adapter.
 * Keeps a minimal set of fields required by the domain model.
 */
@Entity
@Table(name = "petshop_pokemon")
public class PokemonPetEntity {

    @Id
    private UUID id;

    private String name;

    @ElementCollection
    @CollectionTable(name = "pet_types", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "type")
    private List<String> types;

    private boolean available;

    private String ownerId;

    private Instant createdAt;

    public PokemonPetEntity() {
    }

    public PokemonPetEntity(UUID id, String name, List<String> types, boolean available, String ownerId, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.types = types;
        this.available = available;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

