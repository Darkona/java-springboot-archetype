package com.archetype;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class LayerArchitectureTests {

    static final String base = "com.archetype.layer";
    // Import once for all tests
    static final JavaClasses classes = new ClassFileImporter().importPackages(base);

    @Test
    @DisplayName("Ensure the correct dependencies between classes in specific packages")
    void ensure_layered_architecture() {
        noClasses()
                .that().resideInAPackage(base + ".persistence")
                .should().dependOnClassesThat().resideInAnyPackage(
                        base +".service", base + ".controller", base + ".config"
                )
                .check(classes);
    }

    @Test
    @DisplayName("Controllers should not depend on persistence")
    void controllers_should_not_depend_on_persistence() {
        noClasses()
                .that().resideInAPackage(base + ".controller")
                .should().dependOnClassesThat().resideInAPackage(base + ".persistence")
                .check(classes);
    }

    @Test
    @DisplayName("Persistence mappers should not depend on services")
    void mappers_should_not_depend_on_services() {
        noClasses()
                .that().resideInAPackage(base + ".persistence.mapper")
                .should().dependOnClassesThat().resideInAPackage(base + ".service")
                .check(classes);
    }

    @Test
    @DisplayName("Services should not depend on controllers")
    void services_should_not_depend_on_controllers() {
        noClasses()
                .that().resideInAPackage(base + ".service")
                .should().dependOnClassesThat().resideInAPackage(base + ".controller")
                .check(classes);
    }

    @Test
    @DisplayName("Domain model should not depend on other layer packages")
    void model_should_not_depend_on_other_packages() {
        noClasses()
                .that().resideInAnyPackage(base + ".domain..")
                .should().dependOnClassesThat().resideOutsideOfPackage(base + ".domain..")
                .orShould().dependOnClassesThat().resideInAPackage(base + ".domain..")
                .check(classes);
    }

}
