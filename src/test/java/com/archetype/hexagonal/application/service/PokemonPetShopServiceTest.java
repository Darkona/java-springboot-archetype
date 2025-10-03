package com.archetype.hexagonal.application.service;

import com.archetype.hexagonal.application.port.out.EventPublisherPort;
import com.archetype.hexagonal.application.port.out.PokemonRepositoryPort;
import com.archetype.hexagonal.domain.model.PokemonPet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PokemonPetShopService using Mockito to mock outbound ports.
 */
class PokemonPetShopServiceTest {

    private PokemonRepositoryPort repository;
    private EventPublisherPort publisher;
    private PokemonPetShopService service;

    @BeforeEach
    void setUp() {
        repository = mock(PokemonRepositoryPort.class);
        publisher = mock(EventPublisherPort.class);
        service = new PokemonPetShopService(repository, publisher);
    }

    @Test
    void register_shouldSaveAndPublishEvent() {
        // Arrange
        String name = "Pikachu";
        List<String> types = List.of("Electric");

        // When repository.save is called, return the same pet instance passed in.
        when(repository.save(any(PokemonPet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PokemonPet created = service.register(name, types);

        // Assert
        assertNotNull(created);
        assertEquals(name, created.getName());
        assertTrue(created.isAvailable());
        assertEquals(types, created.getTypes());

        // Verify repository.save called once with the created domain object
        ArgumentCaptor<PokemonPet> captor = ArgumentCaptor.forClass(PokemonPet.class);
        verify(repository, times(1)).save(captor.capture());
        PokemonPet savedArg = captor.getValue();
        assertEquals(created.getId(), savedArg.getId());

        // Verify event published
        verify(publisher, times(1)).publishPokemonRegistered(any(PokemonPet.class));
    }

    @Test
    void adopt_shouldChangeAvailabilityAndPublishEvent() {
        // Arrange
        PokemonPet original = PokemonPet.register("Bulbasaur", List.of("Grass", "Poison"));
        UUID id = original.getId();
        when(repository.findById(id)).thenReturn(Optional.of(original));

        // When saving adopted pet, return the adopted version
        when(repository.save(any(PokemonPet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String ownerId = "trainer-ash";

        // Act
        PokemonPet adopted = service.adopt(id, ownerId);

        // Assert
        assertNotNull(adopted);
        assertEquals(ownerId, adopted.getOwnerId());
        assertFalse(adopted.isAvailable());
        assertEquals(original.getId(), adopted.getId());

        // Verify interactions
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(PokemonPet.class));
        verify(publisher, times(1)).publishPokemonAdopted(any(PokemonPet.class));
    }

    @Test
    void listAvailable_shouldReturnRepositoryResults() {
        // Arrange
        PokemonPet p1 = PokemonPet.register("Pidgey", List.of("Flying"));
        PokemonPet p2 = PokemonPet.register("Rattata", List.of("Normal"));
        when(repository.findAvailable()).thenReturn(List.of(p1, p2));

        // Act
        List<PokemonPet> available = service.listAvailable();

        // Assert
        assertNotNull(available);
        assertEquals(2, available.size());
        assertTrue(available.contains(p1));
        assertTrue(available.contains(p2));

        verify(repository, times(1)).findAvailable();
    }

    @Test
    void adopt_nonExisting_shouldThrow() {
        // Arrange
        UUID missing = UUID.randomUUID();
        when(repository.findById(missing)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(java.util.NoSuchElementException.class, () -> service.adopt(missing, "owner"));
        verify(repository, times(1)).findById(missing);
        verify(repository, never()).save(any());
        verify(publisher, never()).publishPokemonAdopted(any());
    }
}

