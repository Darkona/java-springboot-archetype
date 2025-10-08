package com.fci.sdk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fci.sdk.dto.FciPayload;
import com.fci.sdk.exception.FciSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Service for handling Failed Customer Interactions (FCI) events.
 * This service provides methods to initialize, send payload data, and finalize FCI events,
 * storing them in Redis for real-time processing and analysis.
 * <p>
 * Events are sent asynchronously by default! sendSync is provided as an
 * alternative
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FciService {

  public static final String FCI_KEY = "fci:events";
  /** Redis template for storing FCI events. */
    private final RedisTemplate<String, String> redisTemplate;
    /** Object mapper for JSON serialization of FCI events. */
    private final ObjectMapper objectMapper;

    /**
     * Sends intermediate payload data during a transaction.
     * This method can be called multiple times during a transaction to send additional data.
     *
     * @param payload The FCI payload containing the data to be sent
     */
    public final void sendSync(final FciPayload payload) {
      try {
        final String eventJson = createEventJson(payload);
        pushToRedis(eventJson);
        log.debug("FCI payload event sent for transaction: {}",
            payload.getMetadata().getTransactionId());
      } catch (JsonProcessingException e) {
        log.error("Error serializing FCI payload for transaction: {}",
            payload.getMetadata().getTransactionId(), e);
        throw new FciSendException(e);
      }
    }

    /**
     * Asynchronously sends an FCI event.
     * This method provides non-blocking event sending for better performance.
     *
     * @param payload The FCI payload to send
     * @return CompletableFuture that completes when the event is sent
     */
    public final CompletableFuture<Void> send(final FciPayload payload) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return createEventJson(payload);
            } catch (JsonProcessingException e) {
                throw new CompletionException(e);
            }
        }).thenAcceptAsync(this::pushToRedis).exceptionallyAsync(e -> {
            log.error("Error sending FCI async event for transaction: {}", payload.getMetadata().getTransactionId(), e);
            return null;
        });
    }

    /**
     * Creates JSON representation of the FCI event.
     *
     * @param payload The FCI payload to serialize
     * @return JSON string representation of the event
     * @throws JsonProcessingException if serialization fails
     */
    private String createEventJson(final FciPayload payload) throws JsonProcessingException {
        return objectMapper.writeValueAsString(payload);
    }

    /**
     * Pushes the FCI event JSON to Redis for processing.
     *
     * @param eventJson The JSON string to push to Redis
     */
    private void pushToRedis(final String eventJson) {
        redisTemplate.opsForList().rightPush(FCI_KEY, eventJson);
        log.debug("FCI event pushed to Redis with key: {}", FCI_KEY);
    }
} 
