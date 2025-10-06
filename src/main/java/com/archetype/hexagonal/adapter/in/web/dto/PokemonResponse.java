package com.archetype.hexagonal.adapter.in.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for Pokemon in the hexagonal architecture module.
 * 
 * Follows ADR 0017 (Java 21 language features) by using records for DTOs.
 * Represents a Pokemon with ownership and availability information.
 */
public record PokemonResponse(
    UUID id,
    String name,
    List<String> types,
    boolean available,
    String ownerId,
    Instant createdAt
) {}
