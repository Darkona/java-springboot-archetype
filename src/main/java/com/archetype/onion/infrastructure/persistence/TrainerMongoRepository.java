package com.archetype.onion.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for trainer documents.
 */
@Repository
public interface TrainerMongoRepository extends MongoRepository<TrainerDocument, String> {
}
