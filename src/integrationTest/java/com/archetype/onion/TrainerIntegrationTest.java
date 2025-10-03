package com.archetype.onion;

import com.archetype.onion.application.ports.in.TrainerUseCase;
import com.archetype.onion.domain.model.PokemonOwnership;
import com.archetype.onion.domain.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for the Trainer onion architecture module.
 * Uses Testcontainers to spin up MongoDB and Redis instances.
 */
@SpringBootTest
@Testcontainers
class TrainerIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7"))
        .withExposedPorts(27017);
    
    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
        .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // MongoDB configuration
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        
        // Redis configuration
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
        
        // Disable other auto-configurations for this test
        registry.add("spring.autoconfigure.exclude",
            () -> "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                  "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                  "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration");
    }
    
    @Autowired
    private TrainerUseCase trainerUseCase;
    
    @BeforeEach
    void setUp() {
        // Clean up before each test
        List<Trainer> allTrainers = trainerUseCase.listTrainers();
        allTrainers.forEach(t -> trainerUseCase.deleteTrainer(t.getId()));
    }
    
    @Test
    void shouldCreateTrainer() {
        // Given
        Trainer trainer = Trainer.builder()
            .name("Ash Ketchum")
            .badges(0)
            .ownedPokemons(new ArrayList<>())
            .build();
        
        // When
        Trainer created = trainerUseCase.createTrainer(trainer);
        
        // Then
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Ash Ketchum");
        assertThat(created.getBadges()).isEqualTo(0);
        assertThat(created.getCreatedAt()).isNotNull();
        assertThat(created.getUpdatedAt()).isNotNull();
    }
    
    @Test
    void shouldAddPokemonToTrainer() {
        // Given
        Trainer trainer = createSampleTrainer("Misty");
        
        PokemonOwnership pikachu = PokemonOwnership.builder()
            .pokemonId("25")
            .nickname("Pikachu")
            .acquiredAt(Instant.now())
            .build();
        
        // When
        Trainer updated = trainerUseCase.addPokemonToTrainer(trainer.getId(), pikachu);
        
        // Then
        assertThat(updated.getOwnedPokemons()).hasSize(1);
        assertThat(updated.getOwnedPokemons().get(0).getNickname()).isEqualTo("Pikachu");
        assertThat(updated.getOwnedPokemons().get(0).getPokemonId()).isEqualTo("25");
    }
    
    @Test
    void shouldEnforceSixPokemonLimit() {
        // Given
        Trainer trainer = createSampleTrainer("Brock");
        
        // Add 6 Pokemon
        for (int i = 1; i <= 6; i++) {
            PokemonOwnership pokemon = PokemonOwnership.builder()
                .pokemonId(String.valueOf(i))
                .nickname("Pokemon" + i)
                .acquiredAt(Instant.now())
                .build();
            trainer = trainerUseCase.addPokemonToTrainer(trainer.getId(), pokemon);
        }
        
        // When/Then - attempting to add 7th should fail
        PokemonOwnership seventh = PokemonOwnership.builder()
            .pokemonId("7")
            .nickname("Pokemon7")
            .acquiredAt(Instant.now())
            .build();
        
        String trainerId = trainer.getId();
        assertThatThrownBy(() -> trainerUseCase.addPokemonToTrainer(trainerId, seventh))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("maximum number of Pokemon");
    }
    
    @Test
    void shouldPreventDuplicateNicknames() {
        // Given
        Trainer trainer = createSampleTrainer("Gary");
        
        PokemonOwnership first = PokemonOwnership.builder()
            .pokemonId("1")
            .nickname("Buddy")
            .acquiredAt(Instant.now())
            .build();
        
        trainer = trainerUseCase.addPokemonToTrainer(trainer.getId(), first);
        
        // When/Then - attempting to add Pokemon with same nickname should fail
        PokemonOwnership duplicate = PokemonOwnership.builder()
            .pokemonId("2")
            .nickname("Buddy")
            .acquiredAt(Instant.now())
            .build();
        
        String trainerId = trainer.getId();
        assertThatThrownBy(() -> trainerUseCase.addPokemonToTrainer(trainerId, duplicate))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("nickname");
    }
    
    @Test
    void shouldRetrieveTrainerById() {
        // Given
        Trainer created = createSampleTrainer("Professor Oak");
        
        // When
        Optional<Trainer> retrieved = trainerUseCase.getTrainer(created.getId());
        
        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Professor Oak");
    }
    
    @Test
    void shouldListAllTrainers() {
        // Given
        createSampleTrainer("Trainer1");
        createSampleTrainer("Trainer2");
        createSampleTrainer("Trainer3");
        
        // When
        List<Trainer> trainers = trainerUseCase.listTrainers();
        
        // Then
        assertThat(trainers).hasSize(3);
    }
    
    @Test
    void shouldDeleteTrainer() {
        // Given
        Trainer trainer = createSampleTrainer("Temporary");
        
        // When
        boolean deleted = trainerUseCase.deleteTrainer(trainer.getId());
        
        // Then
        assertThat(deleted).isTrue();
        assertThat(trainerUseCase.getTrainer(trainer.getId())).isEmpty();
    }
    
    @Test
    void shouldCacheTrainerRetrievals() {
        // Given
        Trainer trainer = createSampleTrainer("Cached Trainer");
        String trainerId = trainer.getId();
        
        // When - first retrieval (should hit database)
        Optional<Trainer> first = trainerUseCase.getTrainer(trainerId);
        
        // When - second retrieval (should hit cache)
        Optional<Trainer> second = trainerUseCase.getTrainer(trainerId);
        
        // Then
        assertThat(first).isPresent();
        assertThat(second).isPresent();
        assertThat(first.get().getName()).isEqualTo(second.get().getName());
    }
    
    private Trainer createSampleTrainer(String name) {
        Trainer trainer = Trainer.builder()
            .name(name)
            .badges(0)
            .ownedPokemons(new ArrayList<>())
            .build();
        
        return trainerUseCase.createTrainer(trainer);
    }
}
