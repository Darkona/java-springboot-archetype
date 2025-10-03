package com.skeletor;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

/**
 * Relaxed global architecture tests.
 * Only enforces critical patterns without restrictive dependency rules.
 */
public class ArchitectureTests {

    private static final JavaClasses ALL_CLASSES = new ClassFileImporter().importPackages("com.archetype");

    /**
     * ADR 0001: Enforce constructor injection over field injection.
     * No fields should be annotated with @Autowired.
     */
    @Test
    void no_field_injection_with_autowired() {
        ArchRule rule = fields()
                .should().notBeAnnotatedWith(Autowired.class)
                .because("ADR 0001: Prefer constructor injection using @RequiredArgsConstructor or explicit constructors");
        rule.check(ALL_CLASSES);
    }
}
