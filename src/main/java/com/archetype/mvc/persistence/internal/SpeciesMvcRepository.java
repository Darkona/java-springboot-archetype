package com.archetype.mvc.persistence.internal;

import com.archetype.mvc.model.Type;
import com.archetype.mvc.persistence.document.SpeciesDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpeciesMvcRepository extends MongoRepository<SpeciesDocument, Integer> {

    Optional<SpeciesDocument> findByName(String name);

    List<SpeciesDocument> findAllByFirstType(Type firstType);

    List<SpeciesDocument> findAllByFirstTypeAndSecondType(Type firstType, Type lastType);

    List<SpeciesDocument> findAllByFirstTypeOrSecondType(Type firstType, Type secondType);
}
