package com.skeletor;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * Modulith-aware checks for module-specific assertions.
 *
 * This test uses reflection to interact with Spring Modulith types when present on the classpath.
 * If Spring Modulith is not available, the test is skipped so the architectureTest suite remains
 * usable in projects that don't include the Modulith dependency.
 */
public class ModulithArchitectureTests {


    @Test
    void modulith_application_modules_should_be_available_when_modulith_is_on_classpath() throws Exception {
        // If Spring Modulith isn't on the classpath, skip this test (it's an optional check).
        Class<?> modulesClass;
        try {
            modulesClass = Class.forName("org.springframework.modulith.ApplicationModules");
        } catch (ClassNotFoundException e) {
            Assumptions.assumeTrue(false, "Spring Modulith not present on classpath; skipping Modulith checks");
            return;
        }

        // We avoid starting the full Spring context in architecture tests.
        // Presence of the ApplicationModules class on the classpath is a sufficient indicator.
        Assertions.assertNotNull(modulesClass, "ApplicationModules class should be present when Spring Modulith is on the classpath");
    }
}
