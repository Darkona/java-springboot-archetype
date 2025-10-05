package com.archetype.layer.client.pokemon.mapper;

import com.archetype.layer.client.pokemon.dto.PokemonClientCreate;
import com.archetype.layer.client.pokemon.dto.PokemonClientDetails;
import com.archetype.layer.client.pokemon.dto.PokemonClientOverview;
import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.domain.dto.response.PokemonDetails;
import com.archetype.layer.domain.dto.response.PokemonOverview;
import com.archetype.layer.domain.model.Move;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for Pokemon client DTOs.
 * Maps between client DTOs and domain DTOs following ADR 0002 (Domain separation and mapping).
 */
@Mapper(componentModel = "spring")
public interface PokemonClientMapper {

    // ===== Request Mapping =====

    /**
     * Map domain create request to client create request.
     */
    PokemonClientCreate toClientCreate(PokemonCreate domainCreate);

    /**
     * Map client create request to domain create request.
     */
    PokemonCreate toDomainCreate(PokemonClientCreate clientCreate);

    // ===== Response Mapping =====

    /**
     * Map client details response to domain details response.
     * Note: Client uses simplified moves as strings, domain uses Move objects.
     */
    @Mapping(target = "moves", expression = "java(mapMovesToObjects(clientDetails.moves()))")
    @Mapping(target = "secret", ignore = true) // Client doesn't expose secret information
    PokemonDetails toDomainDetails(PokemonClientDetails clientDetails);

    /**
     * Map domain details response to client details response.
     * Note: Domain Move objects are simplified to strings for client.
     */
    @Mapping(target = "moves", expression = "java(mapMovesToStrings(domainDetails.moves()))")
    @Mapping(target = "id", ignore = true) // Client ID will be set separately
    PokemonClientDetails toClientDetails(PokemonDetails domainDetails);

    /**
     * Map client overview response to domain overview response.
     */
    PokemonOverview toDomainOverview(PokemonClientOverview clientOverview);

    /**
     * Map domain overview response to client overview response.
     */
    @Mapping(target = "id", ignore = true) // Client ID will be set separately
    PokemonClientOverview toClientOverview(PokemonOverview domainOverview);

    // ===== Helper Methods =====

    /**
     * Convert string moves from client to Move objects for domain.
     */
    default List<Move> mapMovesToObjects(List<String> moveNames) {
        if (moveNames == null) {
            return List.of();
        }
        return moveNames.stream()
                .map(name -> new Move(
                        name,
                        50,   // Default power
                        100,  // Default accuracy
                        20    // Default PP
                ))
                .toList();
    }

    /**
     * Convert Move objects from domain to strings for client.
     */
    default List<String> mapMovesToStrings(List<Move> moves) {
        if (moves == null) {
            return List.of();
        }
        return moves.stream()
                .map(Move::name)
                .toList();
    }
}
