package com.fci.sdk.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for MongoDB operations.
 * 
 * This controller provides endpoints for storing and retrieving data from MongoDB.
 * It implements the DatabaseOperations interface to ensure consistent API behavior
 * across different database implementations.
 * 
 * The controller stores string data as MongoDB documents with additional metadata
 * including document ID and index for better data organization.
 */
@RestController
@RequestMapping("/api/dummy/mongodb")
@Slf4j
public class MongoDBController implements DatabaseOperations {

    /** MongoDB collection name for storing string data */
    private static final String COLLECTION_NAME = "dummy_strings_collection";

    /** MongoDB template for database operations */
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<List<String>> store(final List<String> data) {
        try {
            log.info("Storing {} items in MongoDB collection: {}", data.size(), COLLECTION_NAME);

            // Create documents from strings
            final List<org.bson.Document> documents = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                final org.bson.Document doc = new org.bson.Document();
                doc.put("_id", i + 1);
                doc.put("value", data.get(i));
                doc.put("index", i);
                documents.add(doc);
            }

            // Clear existing collection and insert new data
            mongoTemplate.getCollection(COLLECTION_NAME).drop();
            mongoTemplate.insert(documents, COLLECTION_NAME);

            log.info("Successfully stored {} items in MongoDB collection: {}", data.size(), COLLECTION_NAME);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error storing data in MongoDB: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<List<String>> retrieve() {
        try {
            log.info("Retrieving data from MongoDB collection: {}", COLLECTION_NAME);

            final List<org.bson.Document> documents = mongoTemplate.findAll(org.bson.Document.class, COLLECTION_NAME);

            final List<String> result = new ArrayList<>();
            for (final org.bson.Document doc : documents) {
                result.add(doc.getString("value"));
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error retrieving data from MongoDB: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(e.getMessage()));
        }
    }
} 
