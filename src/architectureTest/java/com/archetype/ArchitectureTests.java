package com.archetype;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import static com.archetype.ArchitectureConditions.implementMatchingInfoInterface;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;


@AnalyzeClasses(packages = {"com.archetype.."})
public class ArchitectureTests {


    @ArchTest
    @DisplayName("Avoid injection with @Autowired annotation, prefer constructor injection.")
    void noAutowired(JavaClasses classes) {

        fields()
                .should().notBeAnnotatedWith(Autowired.class)
                .because("ADR 0001: Prefer constructor injection using @RequiredArgsConstructor or explicit constructors")
                .check(classes);

    }

    @ArchTest
    @DisplayName("Controllers should implement an interface with the same name and the suffix \"Info\"")
    void controller_info(JavaClasses classes) {

        classes().that()
                 .resideInAPackage("com.archetype.layer.controller")
                 .should(implementMatchingInfoInterface)
                 .because("ADR 0009: Controller OpenAPI annotations through interface-based \"Info\" classes")
                 .check(classes);

    }

    @ArchTest
    @DisplayName("\"Info\" controller interfaces should be annotated with springdoc @Tag")
    void controller_info_impl(JavaClasses classes) {

        classes().that()
                 .areInterfaces().and().haveNameMatching(".*Info")
                 .should().beAnnotatedWith(Tag.class)
                 .because("ADR 0009: Controller OpenAPI annotations through interface-based \"Info\" classes")
                 .check(classes);

    }
}
