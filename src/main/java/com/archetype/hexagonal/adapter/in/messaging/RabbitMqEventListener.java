package com.archetype.hexagonal.adapter.in.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Simple test-only RabbitMQ listener used by the integration tests to verify
 * that events were published to the petshop.events exchange and received.
 *
 * Binds a temporary queue to the configured exchange and listens to the
 * petshop.pokemon.* routing keys used by the publisher.
 *
 * The queue and exchange names are configurable via properties:
 *  - petshop.events.exchange (default: petshop.events)
 *  - petshop.events.queue (default: petshop.events.test.queue)
 *
 * This listener is intentionally minimal and only logs received messages.
 */
@Component
public class RabbitMqEventListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqEventListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${petshop.events.queue:petshop.events.test.queue}", durable = "false", autoDelete = "true"),
            exchange = @Exchange(value = "${petshop.events.exchange:petshop.events}", type = "topic", durable = "true"),
            key = {"petshop.pokemon.registered", "petshop.pokemon.adopted", "petshop.pokemon.returned"}
    ))
    public void onEvent(String message) {
        log.info("RabbitMqEventListener received message: {}", message);
    }
}

