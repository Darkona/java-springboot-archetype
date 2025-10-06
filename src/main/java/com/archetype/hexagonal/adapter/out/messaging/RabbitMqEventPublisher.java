package com.archetype.hexagonal.adapter.out.messaging;

import com.archetype.hexagonal.application.port.out.EventPublisherPort;
import com.archetype.hexagonal.domain.model.PokemonPet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple RabbitMQ publisher using RabbitTemplate.
 * Publishes small JSON payloads to the petshop.events exchange with routing keys:
 * - petshop.pokemon.registered
 * - petshop.pokemon.adopted
 * - petshop.pokemon.returned
 * <p>
 * This implementation is intentionally simple; messages are best-effort.
 */
@Component
public class RabbitMqEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    @Value("${petshop.events.exchange:petshop.events}")
    private String exchange;

    public RabbitMqEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishPokemonRegistered(PokemonPet pet) {
        publish("petshop.pokemon.registered", buildPayload(pet));
    }

    @Override
    public void publishPokemonAdopted(PokemonPet pet) {
        publish("petshop.pokemon.adopted", buildPayload(pet));
    }

    @Override
    public void publishPokemonReturned(PokemonPet pet) {
        publish("petshop.pokemon.returned", buildPayload(pet));
    }

    private Map<String, Object> buildPayload(PokemonPet pet) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", pet.getId().toString());
        map.put("name", pet.getName());
        map.put("ownerId", pet.getOwnerId());
        map.put("available", pet.isAvailable());
        map.put("createdAt", pet.getCreatedAt() != null ? pet.getCreatedAt().toString() : Instant.now().toString());
        return map;
    }

    private void publish(String routingKey, Map<String, Object> payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            log.info("Publishing event to exchange='{}' routingKey='{}' payload={}", exchange, routingKey, json);
            rabbitTemplate.convertAndSend(exchange, routingKey, json);
            log.info("Published event to exchange='{}' routingKey='{}'", exchange, routingKey);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event for routingKey {}: {}", routingKey, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to publish event to exchange {} with routingKey {}: {}", exchange, routingKey, e.getMessage(), e);
        }
    }
}

