package com.archetype.layer.service;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.persistence.document.PokemonDocument;
import com.archetype.layer.persistence.PokemonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
public class PokemonServiceIT {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0"));

    @Autowired
    private PokemonRepository pokemonRepository;

    private PokemonService pokemonService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        pokemonRepository.deleteAll();
        pokemonService = new PokemonService(pokemonRepository);
    }

    @Test
    void createPokemonTest() {
        PokemonCreate request = new PokemonCreate(0, "Pikachu");

        PokemonDocument created = pokemonService.createPokemon(request);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Pikachu");
    }

    @Test
    void getPokemonTest() {
        PokemonCreate request = new PokemonCreate(0, "Charmander");

        PokemonDocument created = pokemonService.createPokemon(request);

        PokemonDocument retrieved = pokemonService.getPokemon(created.getId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(created.getId());
        assertThat(retrieved.getName()).isEqualTo("Charmander");
    }

    @Test
    void updatePokemonTest() {
        PokemonCreate request = new PokemonCreate(0, "Bulbasaur");

        PokemonDocument created = pokemonService.createPokemon(request);

        PokemonCreate updateRequest = new PokemonCreate(0, "Ivysaur");

        PokemonDocument updated = pokemonService.updatePokemon(created.getId(), updateRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName()).isEqualTo("Ivysaur");
    }

    @Test
    void deletePokemonTest() {
        PokemonCreate request = new PokemonCreate(0, "Squirtle");

        PokemonDocument created = pokemonService.createPokemon(request);

        pokemonService.deletePokemon(created.getId());

        assertThat(pokemonRepository.findById(created.getId())).isNotPresent();
    }

    @Test
    void getAllPokemonsTest() {
        PokemonCreate request1 = new PokemonCreate(0, "Eevee");
        PokemonCreate request2 = new PokemonCreate(0, "Jigglypuff");

        pokemonService.createPokemon(request1);
        pokemonService.createPokemon(request2);

        List<PokemonDocument> allPokemons = pokemonService.getAllPokemons();

        assertThat(allPokemons).hasSize(2);
    }
}

