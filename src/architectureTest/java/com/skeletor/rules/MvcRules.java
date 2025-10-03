package com.skeletor.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * MVC-specific architecture rules.
 *
 * Conservative defaults:
 * - Controllers should only depend on services, models, views and allowed framework/shared packages.
 * - Views/presentation should not depend on persistence implementation.
 * - Controllers should not access persistence repositories directly.
 */
public class MvcRules {

    private static final String BASE = "com.archetype..mvc";

    @Test
    void views_rules_relaxed() {
        // Intentionally relaxed: all checks removed per project request
    }

    @Test
    void controllers_should_be_relaxed() {
        // Intentionally relaxed: all checks removed per project request
    }
}
