package com.archetype.hexagonal.adapter.in.web;

import com.archetype.hexagonal.adapter.in.web.dto.PokemonCreateRequest;
import com.archetype.hexagonal.adapter.in.web.dto.PokemonResponse;
import com.archetype.hexagonal.application.port.in.AdoptPokemon;
import com.archetype.hexagonal.application.port.in.ListAvailablePokemons;
import com.archetype.hexagonal.application.port.in.RegisterPokemon;
import com.archetype.hexagonal.application.service.PokemonPetShopService;
import com.archetype.hexagonal.domain.model.PokemonPet;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller (adapter-in) for the Petshop hexagonal module.
 * Follows ADR 0015 (Prefer Spring annotations over ResponseEntity) for clean controller design.
 * Endpoints:
 * - GET  /api/hexagonal/pokemon        -> list available
 * - POST /api/hexagonal/pokemon        -> register new pokemon
 * - POST /api/hexagonal/pokemon/{id}/adopt -> adopt pokemon (body: { "ownerId": "..." })
 * - POST /api/hexagonal/pokemon/{id}/return -> return pokemon
 */
@RestController
@RequestMapping("/api/hexagonal/pokemon")
public class PokemonPetShopController {

    private final RegisterPokemon registerUseCase;
    private final ListAvailablePokemons listUseCase;
    private final AdoptPokemon adoptUseCase;
    private final PokemonPetShopService service;

    public PokemonPetShopController(RegisterPokemon registerUseCase,
                                    ListAvailablePokemons listUseCase,
                                    AdoptPokemon adoptUseCase,
                                    PokemonPetShopService service) {
        this.registerUseCase = registerUseCase;
        this.listUseCase = listUseCase;
        this.adoptUseCase = adoptUseCase;
        this.service = service;
    }

    @GetMapping
    public List<PokemonResponse> listAvailable() {
        return listUseCase.listAvailable().stream().map(this::toResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PokemonResponse register(@RequestBody PokemonCreateRequest request) {
        PokemonPet created = registerUseCase.register(request.name(), request.types());
        return toResponse(created);
    }

    @PostMapping("/{id}/adopt")
    public PokemonResponse adopt(@PathVariable("id") UUID id, @RequestBody AdoptRequest request) {
        PokemonPet adopted = adoptUseCase.adopt(id, request.ownerId());
        return toResponse(adopted);
    }

    @PostMapping("/{id}/return")
    public PokemonResponse returned(@PathVariable("id") UUID id) {
        PokemonPet returned = service.returned(id);
        return toResponse(returned);
    }

    private PokemonResponse toResponse(PokemonPet pet) {
        return new PokemonResponse(
                pet.getId(),
                pet.getName(),
                pet.getTypes(),
                pet.isAvailable(),
                pet.getOwnerId(),
                pet.getCreatedAt()
        );
    }

    /**
     * Request DTO for adopting Pokemon.
     * Follows ADR 0017 (Java 21 language features) by using records for DTOs.
     */
    public record AdoptRequest(String ownerId) {}
}
