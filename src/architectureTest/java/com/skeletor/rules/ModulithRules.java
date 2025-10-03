package com.archetype.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Modulith-related rules.
 *
 * Conservative checks:
 * - Ensure slices (com.archetype.<module>..) are free of cycles.
 * - Ensure other modules do not access a module's internal packages (com.archetype.<module>.internal..).
 *
 * The second check discovers first-level module names under com.skeletor and asserts that classes
 * outside that module do not access its internal package. This is implemented defensively: if a module
 * has no classes in an internal package, the specific check is skipped for that module.
 */
public class ModulithRules {

    private static final String BASE_ROOT = "com.skeletor";

    @Test
    void slices_should_be_free_of_cycles() {
        JavaClasses classes = new ClassFileImporter().importPackages(BASE_ROOT);
        ArchRule rule = slices().matching(BASE_ROOT + ".(*)..").should().beFreeOfCycles();
        rule.check(classes);
    }

    @Test
    void modules_should_not_expose_internal_packages_to_other_modules() {
        JavaClasses classes = new ClassFileImporter().importPackages(BASE_ROOT);

        // discover module names: the immediate token after 'com.skeletor'
        Set<String> modules = classes.stream()
                .map(JavaClass::getPackageName)
                .filter(pkg -> pkg.startsWith(BASE_ROOT + "."))
                .map(pkg -> {
                    String remainder = pkg.substring((BASE_ROOT + ".").length());
                    int dot = remainder.indexOf('.');
                    if (dot == -1) {
                        return remainder;
                    }
                    return remainder.substring(0, dot);
                })
                .collect(Collectors.toSet());

        for (String module : modules) {
            String internalPackage = BASE_ROOT + "." + module + ".internal..";
            String moduleRootPackage = BASE_ROOT + "." + module + "..";

            // Skip the check if no classes exist in the internal package for this module.
            boolean hasInternal = classes.stream().anyMatch(c -> c.getPackageName().startsWith(BASE_ROOT + "." + module + ".internal"));
            if (!hasInternal) {
                continue;
            }

            // No classes outside the moduleRootPackage should access classes in the internal package.
            ArchRule rule = noClasses()
                    .that().resideOutsideOfPackage(moduleRootPackage)
                    .should().accessClassesThat().resideInAPackage(internalPackage);

            rule.check(classes);
        }
    }
}

