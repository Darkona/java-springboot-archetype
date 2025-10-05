package com.archetype.layer;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Enhanced layered architecture tests following ADR 0014.
 * Focuses on smart internal dependency validation while allowing external dependencies.
 */
public class LayerArchitectureTests {

    static final String base = "com.archetype.layer";
    // Import once for all tests
    static final JavaClasses classes = new ClassFileImporter().importPackages(base);

    @Test
    @DisplayName("Persistence layer should not depend on upper layers within the module")
    void persistence_should_not_depend_on_upper_layers() {
        noClasses()
                .that().resideInAPackage(base + ".persistence..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base + ".service..",
                        base + ".controller.."
                )
                .check(classes);
    }

    @Test
    @DisplayName("Controllers should not depend on persistence layer within the module")
    void controllers_should_not_depend_on_persistence() {
        noClasses()
                .that().resideInAPackage(base + ".controller..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base + ".persistence.."
                )
                .check(classes);
    }

    @Test
    @DisplayName("Mappers should not depend on service layer within the module")
    void mappers_should_not_depend_on_services() {
        noClasses()
                .that().resideInAPackage(base + "..mapper")
                .should().dependOnClassesThat().resideInAnyPackage(base + ".service..")
                .allowEmptyShould(true).check(classes);
    }

    @Test
    @DisplayName("Services should not depend on controller layer within the module")
    void services_should_not_depend_on_controllers() {
        noClasses()
                .that().resideInAPackage(base + ".service..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base + ".controller.."
                )
                .check(classes);
    }

    @Test
    @DisplayName("Domain models should not depend on other layer packages within the module")
    void domain_models_should_not_depend_on_other_layer_packages() {
        noClasses()
                .that().resideInAnyPackage(base + ".domain.model..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base + ".controller..",
                        base + ".service..",
                        base + ".persistence..",
                        base + ".mapper..",
                        base + ".config.."
                )
                .check(classes);
    }


    @Test
    @DisplayName("Request DTOs should not be used by persistence layer")
    void request_dtos_should_not_be_used_by_persistence() {
        noClasses()
                .that().resideInAPackage(base + ".persistence..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base + ".domain.dto.request.."
                )
                .check(classes);
    }

    @Test
    @DisplayName("Response DTOs should not be used by persistence layer")
    void response_dtos_should_not_be_used_by_persistence() {
        noClasses()
                .that().resideInAPackage(base + ".persistence..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base + ".domain.dto.response.."
                )
                .check(classes);
    }

    @Test
    @DisplayName("Controllers should not directly use persistence documents")
    void controllers_should_not_use_persistence_documents() {
        noClasses()
                .that().resideInAPackage(base + ".controller..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base + ".persistence.document.."
                )
                .check(classes);
    }

}
