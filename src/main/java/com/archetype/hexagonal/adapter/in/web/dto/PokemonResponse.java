package com.archetype.hexagonal.adapter.in.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PokemonResponse {
    private UUID id;
    private String name;
    private List<String> types;
    private boolean available;
    private String ownerId;
    private Instant createdAt;

    public PokemonResponse() {
    }

    public PokemonResponse(UUID id, String name, List<String> types, boolean available, String ownerId, Instant createdAt) {
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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

