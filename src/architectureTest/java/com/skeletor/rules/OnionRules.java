package com.skeletor.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Relaxed ArchUnit rules for Onion Architecture.
 * Enforces only critical architectural boundaries without restrictive package dependency lists.
 */
public class OnionRules {
    
    private static final String ONION_BASE = "com.archetype.onion";
    
    /**
     * Enforce constructor injection pattern (ADR 0001).
     * No field injection with @Autowired should be used.
     */
    public static final ArchRule NO_FIELD_INJECTION =
        noFields().that()
            .areDeclaredInClassesThat().resideInAPackage(ONION_BASE + "..")
            .should().beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
            .because("Use constructor injection instead of field injection (ADR 0001)");
    
    /**
     * No cycles between packages.
     */
    public static final ArchRule NO_CYCLES_IN_ONION_PACKAGES =
        slices().matching(ONION_BASE + ".(*)..").should().beFreeOfCycles();
    
    /**
     * Validate all onion architecture rules.
     */
    public static void checkAll(JavaClasses classes) {
        NO_FIELD_INJECTION.check(classes);
        NO_CYCLES_IN_ONION_PACKAGES.check(classes);
    }
}
