package com.archetype.layer;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.archetype.ArchitectureConditions.beProperlyAnnotatedSpringComponent;
import static com.archetype.ArchitectureConditions.followLayerNamingConventions;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Component role and annotation validation tests for layered architecture.
 * Following ADR 0014 - Architecture Testing Strategy.
 */
public class LayerComponentTests {

    static final String base = "com.archetype.layer";
    static final JavaClasses classes = new ClassFileImporter().importPackages(base);

    @Test
    @DisplayName("All mappers should be in centralized mapper package")
    void all_mappers_should_be_in_centralized_package() {
        classes()
                .that().haveSimpleNameEndingWith("Mapper")
                .and().resideInAPackage(base + "..")
                .should().resideInAPackage(base + ".mapper..")
                .because("ADR 0002: All mappers should be centralized under mapper package")
                .check(classes);
    }

    @Test
    @DisplayName("DTO mappers should be in mapper.dto package")
    void dto_mappers_should_be_in_correct_package() {
        classes()
                .that().haveSimpleNameEndingWith("Mapper")
                .and().resideInAnyPackage(base + ".mapper.dto..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        base + ".domain.model..",
                        base + ".domain.dto..",
                        "java..",
                        "org.mapstruct..",
                        "org.springframework.."
                )
                .because("ADR 0002: DTO mappers only work with domain models and DTOs")
                .check(classes);
    }

    @Test
    @DisplayName("Persistence mappers should be in mapper.persistence package")
    void persistence_mappers_should_be_in_correct_package() {
        classes()
                .that().haveSimpleNameEndingWith("Mapper")
                .and().resideInAnyPackage(base + ".mapper.persistence..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        base + ".domain.model..",
                        base + ".persistence.document..",
                        "java..",
                        "org.mapstruct..",
                        "org.springframework.."
                )
                .because("ADR 0002: Persistence mappers only work with domain models and persistence documents")
                .check(classes);
    }

    @Test
    @DisplayName("Services should be in service package")
    void services_should_be_in_service_package() {
        classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().resideInAnyPackage(base + "..")
                .should().resideInAPackage(base + ".service..")
                .check(classes);
    }

    @Test
    @DisplayName("Repositories should be in persistence package")
    void repositories_should_be_in_persistence_package() {
        classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().resideInAnyPackage(base + "..")
                .should().resideInAPackage(base + ".persistence..")
                .check(classes);
    }

    @Test
    @DisplayName("Controllers should have REST or MVC annotations")
    void controllers_should_have_proper_annotations() {
        classes()
                .that().resideInAPackage(base + ".controller..")
                .and().haveSimpleNameEndingWith("Controller")
                .should().beAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Controller")
                .check(classes);
    }

    @Test
    @DisplayName("Services should have @Service annotation")
    void services_should_have_service_annotation() {
        classes()
                .that().resideInAPackage(base + ".service..")
                .and().haveSimpleNameEndingWith("Service")
                .should().beAnnotatedWith("org.springframework.stereotype.Service")
                .check(classes);
    }

    @Test
    @DisplayName("Repositories should have @Repository annotation")
    void repositories_should_have_repository_annotation() {
        classes()
                .that().resideInAPackage(base + ".persistence..")
                .and().haveSimpleNameEndingWith("Repository")
                .should().beAnnotatedWith("org.springframework.stereotype.Repository")
                .check(classes);
    }

    @Test
    @DisplayName("Domain models should not have Spring annotations")
    void domain_models_should_not_have_spring_annotations() {
        classes()
                .that().resideInAPackage(base + ".domain.model..")
                .should().notBeAnnotatedWith("org.springframework.stereotype.Component")
                .andShould().notBeAnnotatedWith("org.springframework.stereotype.Service")
                .andShould().notBeAnnotatedWith("org.springframework.stereotype.Repository")
                .andShould().notBeAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                .andShould().notBeAnnotatedWith("org.springframework.stereotype.Controller")
                .check(classes);
    }

    @Test
    @DisplayName("DTOs should not have Spring annotations")
    void dtos_should_not_have_spring_annotations() {
        classes()
                .that().resideInAPackage(base + ".domain.dto..")
                .and().areNotAnnotatedWith("org.mapstruct.Mapper")
                .and().haveSimpleNameNotEndingWith("Mapper")// Mappers are allowed
                .and().haveSimpleNameNotEndingWith("Impl")// Mappers are allowed
                .should().notBeAnnotatedWith("org.springframework.stereotype.Component")
                .andShould().notBeAnnotatedWith("org.springframework.stereotype.Service")
                .andShould().notBeAnnotatedWith("org.springframework.stereotype.Repository")
                .andShould().notBeAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                .andShould().notBeAnnotatedWith("org.springframework.stereotype.Controller")
                .check(classes);
    }

    @Test
    @DisplayName("Mappers should have appropriate annotations")
    void mappers_should_have_appropriate_annotations() {
        classes()
                .that().haveSimpleNameEndingWith("Mapper")
                .and().resideInAnyPackage(base + "..")
                .should().beAnnotatedWith("org.mapstruct.Mapper")
                .check(classes);
    }

}
