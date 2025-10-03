package com.skeletor;

import com.skeletor.rules.OnionRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

/**
 * Relaxed architecture tests for the Onion module.
 * Validates only critical patterns without restrictive package dependency lists.
 */
class OnionArchitectureTests {
    
    private static final JavaClasses classes = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
        .importPackages("com.archetype.onion");
    
    @Test
    void no_field_injection() {
        OnionRules.NO_FIELD_INJECTION.check(classes);
    }
    
    @Test
    void no_cycles_in_onion_packages() {
        OnionRules.NO_CYCLES_IN_ONION_PACKAGES.check(classes);
    }
}
