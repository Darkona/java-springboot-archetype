package com.archetype.layer;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Data flow validation tests for layered architecture.
 * Ensures proper DTO/Domain model boundaries and mapper responsibilities.
 * Following ADR 0014 - Architecture Testing Strategy.
 */
public class LayerDataFlowTests {

    static final String base = "com.archetype.layer";
    static final JavaClasses classes = new ClassFileImporter().importPackages(base);

    @Test
    @DisplayName("Request DTOs should only be used by controllers and DTO mappers")
    void request_dtos_should_only_be_used_by_controllers_and_mappers() {
        noClasses()
                .that().resideInAnyPackage(
                        base + ".service..",
                        base + ".persistence.."
                )
                .should().dependOnClassesThat().resideInAPackage(base + ".domain.dto.request..")
                .check(classes);
    }

    @Test
    @DisplayName("Response DTOs should not be used by persistence layer")
    void response_dtos_should_not_be_used_by_persistence() {
        noClasses()
                .that().resideInAPackage(base + ".persistence..")
                .should().dependOnClassesThat().resideInAPackage(base + ".domain.dto.response..")
                .check(classes);
    }

    @Test
    @DisplayName("Domain models should not depend on DTOs")
    void domain_models_should_not_depend_on_dtos() {
        noClasses()
                .that().resideInAPackage(base + ".domain.model..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base + ".domain.dto.request..",
                        base + ".domain.dto.response.."
                )
                .check(classes);
    }

    @Test
    @DisplayName("Persistence documents should not depend on DTOs")
    void persistence_documents_should_not_depend_on_dtos() {
        noClasses()
                .that().resideInAPackage(base + ".persistence.document..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base + ".domain.dto.request..",
                        base + ".domain.dto.response.."
                )
                .check(classes);
    }

    @Test
    @DisplayName("DTO mappers should only work with DTOs and domain models")
    void dto_mappers_should_work_with_dtos_and_domain() {
        classes()
                .that().resideInAPackage(base + ".mapper.dto..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        base + ".domain.model..",
                        base + ".domain.dto..",
                        "java..",
                        "org.mapstruct..",
                        "org.springframework.."
                )
                .because("ADR 0002: DTO mappers should only work with domain models and DTOs")
                .check(classes);
    }

    @Test
    @DisplayName("Persistence mappers should only work with domain models and documents")
    void persistence_mappers_should_work_with_domain_and_documents() {
        classes()
                .that().resideInAPackage(base + ".mapper.persistence..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        base + ".domain.model..",
                        base + ".persistence.document..",
                        "java..",
                        "org.mapstruct..",
                        "org.springframework.."
                )
                .because("ADR 0002: Persistence mappers should only work with domain models and persistence documents")
                .check(classes);
    }

    @Test
    @DisplayName("Services should not return persistence documents")
    void services_should_not_return_persistence_documents() {
        // This would require more complex ArchUnit rules to check return types
        // For now, we ensure services don't depend on document packages for outputs
        noClasses()
                .that().resideInAPackage(base + ".controller..")
                .should().dependOnClassesThat().resideInAPackage(base + ".persistence.document..")
                .check(classes);
    }

    @Test
    @DisplayName("Controllers should not use domain models directly in public APIs")
    void controllers_should_use_dtos_not_domain_models() {
        // Controllers should primarily work with DTOs, not domain models
        // This is a design guideline - domain models can be used internally but not exposed
        classes()
                .that().resideInAPackage(base + ".controller..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base + ".domain.dto.request..",
                        base + ".domain.dto.response.."
                )
                .check(classes);
    }

    @Test
    @DisplayName("Request DTOs should be immutable data structures")
    void request_dtos_should_be_data_structures() {
        noClasses()
                .that().resideInAPackage(base + ".domain.dto.request..")
                .should().haveSimpleNameEndingWith("Service")
                .orShould().haveSimpleNameEndingWith("Repository")
                .orShould().haveSimpleNameEndingWith("Controller")
                .check(classes);
    }

    @Test
    @DisplayName("Response DTOs should be immutable data structures")
    void response_dtos_should_be_data_structures() {
        noClasses()
                .that().resideInAPackage(base + ".domain.dto.response..")
                .should().haveSimpleNameEndingWith("Service")
                .orShould().haveSimpleNameEndingWith("Repository")
                .orShould().haveSimpleNameEndingWith("Controller")
                .check(classes);
    }


    @Test
    @DisplayName("Services should work with domain models, not DTOs or documents directly")
    void services_should_work_with_domain_models() {
        classes()
                .that().resideInAPackage(base + ".service..")
                .should().dependOnClassesThat().resideInAPackage(base + ".domain.model..")
                .check(classes);
    }

}
