package com.archetype.mvc.persistence.internal;

import com.archetype.mvc.model.Ability;
import com.archetype.mvc.model.Species;
import com.archetype.mvc.persistence.document.SpeciesDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Objects;

/**
 * Persistence mapper: maps between domain model and persistence document.
 * Following ADR 0002 - centralized mapper organization in mapper.persistence package.
 * This mapper works with domain models and persistence documents, NOT DTOs.
 */
@Mapper
public interface PokemonMvcPersistenceMapper {

    @Mapping(target = "abilities", source = "abilities", qualifiedByName = "idsToAbilities")
    @Mapping(target = "moves",     source = "doc.moves")
    Species toDomain(SpeciesDocument doc);

    @Mapping(target = "abilities", source = "abilities", qualifiedByName = "abilitiesToIds")
    @Mapping(target = "moves",     source = "species.moves")
    SpeciesDocument toDocument(Species species);

    // ---- helpers ----

    @Named("idsToAbilities")
    default List<Ability> idsToAbilities(List<Integer> ids) {
        if (ids == null) return List.of();
        return ids.stream()
                  .filter(Objects::nonNull)
                  .map(id -> new Ability(id, null, null, null)) // name/desc/hidden unknown at this layer
                  .toList();
    }

    @Named("abilitiesToIds")
    default List<Integer> abilitiesToIds(List<Ability> abilities) {
        if (abilities == null) return List.of();
        return abilities.stream()
                        .filter(Objects::nonNull)
                        .map(Ability::id)
                        .toList();
    }


    List<SpeciesDocument> toDocuments(List<Species> species);

    List<Species> toDomain(List<SpeciesDocument> speciesDocuments);
}
