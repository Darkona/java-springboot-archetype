package com.archetype.hexagonal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.time.Duration;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Petshop module using Testcontainers for Postgres and RabbitMQ.
 * Lives in the integrationTest source set and is executed by the integrationTest Gradle task.
 */
@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"spring.flyway.enabled=false", "spring.sql.init.mode=never"}
)
@ActiveProfiles("petshop")
public class PetshopIntegrationTest {

    // Use recent Postgres image
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"))
                    .withDatabaseName("petshop")
                    .withUsername("petshop")
                    .withPassword("petshop");

    // Use a recent RabbitMQ management image (configurable)
    static final RabbitMQContainer RABBIT =
            new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.11-management"));

    static {
        POSTGRES.start();
        RABBIT.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        // Disable Flyway migrations during integration tests using Testcontainers-managed DB.
        // The project includes Flyway for other profiles; some Postgres versions in CI/local
        // can lead to "Unsupported Database" errors from Flyway, so we explicitly disable it here.
        registry.add("spring.flyway.enabled", () -> "false");

        registry.add("spring.rabbitmq.host", RABBIT::getHost);
        registry.add("spring.rabbitmq.port", () -> RABBIT.getMappedPort(5672));
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String queueName = "test.petshop.queue";

    @BeforeAll
    static void beforeAll() {
        // containers already started in static initializer
    }

    @AfterAll
    static void afterAll() {
        try {
            RABBIT.stop();
        } catch (Exception ignored) {}
        try {
            POSTGRES.stop();
        } catch (Exception ignored) {}
    }

    @Test
    void registerAndAdoptFlow_publishesEventsAndPersists() throws Exception {
        // Ensure exchange exists and declare a test queue bound to the exchange with wildcard routing key
        TopicExchange exchange = new TopicExchange("petshop.events");
        amqpAdmin.declareExchange(exchange);

        Queue q = new Queue(queueName, false, true, true);
        amqpAdmin.declareQueue(q);

        Binding binding = BindingBuilder.bind(q).to(exchange).with("petshop.pokemon.*");
        amqpAdmin.declareBinding(binding);

        // POST /api/hexagonal/petshop/pokemons -> register
        String base = "http://localhost:" + port + "/api/hexagonal/pokemon";
        Map<String, Object> createReq = Map.of(
                "name", "Testmon",
                "types", List.of("Normal")
        );

        var postResponse = restTemplate.postForEntity(base, createReq, Map.class);
//        assertThat(postResponse.getStatusCode().is2xxSuccessful()).isTrue();
        Map<String, Object> createdBody = postResponse.getBody();
        assertThat(createdBody).isNotNull();
        assertThat(createdBody.get("id")).isNotNull();

        UUID id = UUID.fromString(createdBody.get("id").toString());

        // Verify a message was published for registration
        org.springframework.amqp.core.Message msg = receiveMessage(queueName, 5000);
        assertThat(msg).as("Expect registration event message").isNotNull();

        // GET list and verify presence
        var listResponse = restTemplate.getForEntity(base, List.class);
        assertThat(listResponse.getStatusCode().is2xxSuccessful()).isTrue();
        List<?> list = listResponse.getBody();
        assertThat(list).isNotNull();
        List<String> ids = (List<String>) list.stream()
                .map(e -> ((Map<?, ?>) e).get("id"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
        assertThat(ids).contains(id.toString());

        // Adopt the pokemon
        String adoptUrl = base + "/" + id + "/adopt";
        Map<String, Object> adoptReq = Map.of("ownerId", "trainer-test");
        var adoptResp = restTemplate.postForEntity(adoptUrl, adoptReq, Map.class);
        assertThat(adoptResp.getStatusCode().is2xxSuccessful()).isTrue();
        Map<String, Object> adoptedBody = adoptResp.getBody();
        assertThat(adoptedBody).isNotNull();
        assertThat(adoptedBody.get("ownerId")).isEqualTo("trainer-test");
        assertThat(Boolean.FALSE.equals(adoptedBody.get("available")) || "false".equals(adoptedBody.get("available").toString())).isTrue();

        // Verify an adopted event was published
        org.springframework.amqp.core.Message adoptedMsg = receiveMessage(queueName, 5000);
        assertThat(adoptedMsg).as("Expect adopted event message").isNotNull();
    }

    private org.springframework.amqp.core.Message receiveMessage(String queue, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            org.springframework.amqp.core.Message m = rabbitTemplate.receive(queue);
            if (m != null) return m;
            Thread.sleep(200);
        }
        return null;
    }
}

