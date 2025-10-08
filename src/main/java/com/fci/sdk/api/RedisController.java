package com.fci.sdk.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Redis operations.
 * 
 * This controller provides endpoints for storing and retrieving data from Redis.
 * It implements the DatabaseOperations interface to ensure consistent API behavior
 * across different database implementations.
 * 
 * @author Gonzalo
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/dummy/redis")
@Slf4j
public class RedisController implements DatabaseOperations {
    /** Redis key used for storing string data. */
    private static final String REDIS_KEY = "dummy_strings_list";

    /** Redis template for performing Redis operations. */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public final ResponseEntity<List<String>> store(final List<String> data) {
        try {
            // Clear existing list
            redisTemplate.delete(REDIS_KEY);

            // Only push data if the list is not empty
            if (!data.isEmpty()) {
                redisTemplate.opsForList().rightPushAll(REDIS_KEY, data.toArray(new String[0]));
            }

            log.info("Successfully stored {} items in Redis list: {}", data.size(), REDIS_KEY);
            log.info("Stored data: " + data.size());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error storing data in Redis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(e.getMessage()));
        }
    }

    @Override
    public final ResponseEntity<List<String>> retrieve() {
        try {
            log.info("Retrieving data from Redis list with key: {}", REDIS_KEY);

            final List<String> result = redisTemplate.opsForList().range(REDIS_KEY, 0, -1);

            if (result == null || result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
            }

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Error retrieving data from Redis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(e.getMessage()));
        }
    }
} 
