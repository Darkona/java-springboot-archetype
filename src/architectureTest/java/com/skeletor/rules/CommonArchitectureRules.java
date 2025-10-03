package com.archetype.rules;

import java.util.Arrays;

/**
 * Common helper constants and utilities for architecture tests.
 *
 * This centralizes allowed framework packages and shared project packages so tests
 * can remain conservative while still being easy to adjust.
 */
public final class CommonArchitectureRules {

    private CommonArchitectureRules() { /* utility class */ }

    // Common framework packages that are allowed by domain/adapters/etc.
    public static final String[] FRAMEWORK_PACKAGES = new String[] {
            "java..",
            "javax..",
            "jakarta..",
            "org.springframework..",
            "org.slf4j..",
            "lombok..",
            "com.fasterxml.jackson..",
            "io.swagger.v3.oas.."
    };

    // Project-wide shared packages that are allowed to be accessed across modules.
    public static final String[] SHARED_PACKAGES = new String[] {
            "com.archetype.common.."
    };

    /**
     * Returns a combined array of allowed packages (framework + shared + extras).
     * Tests can call this to build their allowed package lists.
     *
     * @param extras optional extra package prefixes to include (e.g. "com.archetype..")
     * @return combined allowed package prefixes
     */
    public static String[] allowedPackages(String... extras) {
        int baseLen = FRAMEWORK_PACKAGES.length + SHARED_PACKAGES.length + (extras == null ? 0 : extras.length);
        String[] result = new String[baseLen];
        int pos = 0;
        System.arraycopy(FRAMEWORK_PACKAGES, 0, result, pos, FRAMEWORK_PACKAGES.length);
        pos += FRAMEWORK_PACKAGES.length;
        System.arraycopy(SHARED_PACKAGES, 0, result, pos, SHARED_PACKAGES.length);
        pos += SHARED_PACKAGES.length;
        if (extras != null && extras.length > 0) {
            System.arraycopy(extras, 0, result, pos, extras.length);
        }
        return result;
    }

    /**
     * Convenience helper that returns the framework + shared packages as a String array.
     */
    public static String[] allowedFrameworkAndShared() {
        return allowedPackages();
    }

    @Override
    public String toString() {
        return "CommonArchitectureRules{" +
                "framework=" + Arrays.toString(FRAMEWORK_PACKAGES) +
                ", shared=" + Arrays.toString(SHARED_PACKAGES) +
                '}';
    }
}

