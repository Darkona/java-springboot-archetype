package com.archetype.onion.infrastructure.config;

import com.archetype.onion.application.ports.in.TrainerUseCase;
import com.archetype.onion.domain.model.PokemonOwnership;
import com.archetype.onion.domain.model.Trainer;
import io.github.darkona.logged.Logged;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;

/**
 * Demo data loader for trainers.
 * Loads sample trainers when the 'demo' profile is active.
 */
@Component
@Profile("demo")
@RequiredArgsConstructor
@Slf4j
@Logged
public class TrainerDemoDataLoader implements CommandLineRunner {

    private final TrainerUseCase trainerUseCase;

    @Override
    public void run(String... args) {
        log.info("Loading demo trainer data...");

        try {
            // Create Ash Ketchum
            Trainer ash = Trainer.builder()
                                 .name("Ash Ketchum")
                                 .badges(8)
                                 .ownedPokemons(new ArrayList<>())
                                 .build();

            ash = trainerUseCase.createTrainer(ash);
            log.info("Created trainer: {}", ash.getName());

            // Add Pikachu
            PokemonOwnership pikachu = PokemonOwnership.builder()
                                                       .pokemonId("25")
                                                       .nickname("Pikachu")
                                                       .acquiredAt(Instant.now().minusSeconds(86400 * 365))
                                                       .build();

            ash = trainerUseCase.addPokemonToTrainer(ash.getId(), pikachu);
            log.info("Added Pikachu to Ash's team");

            // Add Charizard
            PokemonOwnership charizard = PokemonOwnership.builder()
                                                         .pokemonId("6")
                                                         .nickname("Charizard")
                                                         .acquiredAt(Instant.now().minusSeconds(86400 * 300))
                                                         .build();

            trainerUseCase.addPokemonToTrainer(ash.getId(), charizard);
            log.info("Added Charizard to Ash's team");

            // Create Misty
            Trainer misty = Trainer.builder()
                                   .name("Misty")
                                   .badges(5)
                                   .ownedPokemons(new ArrayList<>())
                                   .build();

            misty = trainerUseCase.createTrainer(misty);
            log.info("Created trainer: {}", misty.getName());

            // Add Staryu
            PokemonOwnership staryu = PokemonOwnership.builder()
                                                      .pokemonId("120")
                                                      .nickname("Staryu")
                                                      .acquiredAt(Instant.now().minusSeconds(86400 * 400))
                                                      .build();

            trainerUseCase.addPokemonToTrainer(misty.getId(), staryu);
            log.info("Added Staryu to Misty's team");

            // Add Psyduck
            PokemonOwnership psyduck = PokemonOwnership.builder()
                                                       .pokemonId("54")
                                                       .nickname("Psyduck")
                                                       .acquiredAt(Instant.now().minusSeconds(86400 * 350))
                                                       .build();

            trainerUseCase.addPokemonToTrainer(misty.getId(), psyduck);
            log.info("Added Psyduck to Misty's team");

            log.info("Demo trainer data loaded successfully!");

        } catch (Exception e) {
            log.error("Error loading demo data", e);
        }
    }
}
