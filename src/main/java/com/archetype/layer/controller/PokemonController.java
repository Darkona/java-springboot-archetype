package com.archetype.layer.controller;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.persistence.document.PokemonDocument;
import com.archetype.layer.service.PokemonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pokemon")
@RequiredArgsConstructor
public class PokemonController implements PokemonControllerInfo {

    final PokemonService pokemonService;

    @PostMapping
    public ResponseEntity<PokemonDocument> createPokemon(@RequestBody PokemonCreate req) {
        PokemonDocument created = pokemonService.createPokemon(req);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PokemonDocument> getPokemon(@PathVariable UUID id) {
        return ResponseEntity.ok(pokemonService.getPokemon(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PokemonDocument> updatePokemon(@PathVariable UUID id, @RequestBody PokemonCreate req) {
        return ResponseEntity.ok(pokemonService.updatePokemon(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePokemon(@PathVariable UUID id) {
        pokemonService.deletePokemon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PokemonDocument>> getAllPokemons() {
        return ResponseEntity.ok(pokemonService.getAllPokemons());
    }
}

