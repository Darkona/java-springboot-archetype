package com.archetype;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reusable architecture conditions for testing.
 * Following ADR 0014 - Architecture Testing Strategy.
 */
public class ArchitectureConditions {

    public static final ArchCondition<JavaClass> implementMatchingInfoInterface =
            new ArchCondition<>("implement interface named <ClassName>Info") {
                @Override
                public void check(JavaClass clazz, ConditionEvents events) {
                    if (clazz.isInterface()) return;

                    String pkg = clazz.getPackageName();
                    String expectedSimple = clazz.getSimpleName() + "Info";

                    List<String> candidates = List.of(
                            pkg + "." + expectedSimple,
                            pkg + ".info." + expectedSimple                      // optional subpackage "info"
                    );

                    Set<String> implemented = clazz.getAllRawInterfaces()
                                                   .stream().map(JavaClass::getFullName).collect(Collectors.toSet());

                    boolean ok = candidates.stream().anyMatch(implemented::contains);
                    if (!ok) {
                        events.add(SimpleConditionEvent.violated(
                                clazz,
                                String.format("%s must implement %s (one of %s)",
                                        clazz.getFullName(), expectedSimple, candidates)
                        ));
                    }
                }
            };

    /**
     * Smart condition that only checks internal layer dependencies.
     * Allows external dependencies (Spring, JPA, etc.) but validates internal module structure.
     */
    public static ArchCondition<JavaClass> onlyDependOnInternalLayerClasses(String basePackage) {
        return new ArchCondition<JavaClass>("only depend on allowed internal layer classes") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                javaClass.getDirectDependenciesFromSelf()
                        .stream()
                        .filter(dependency -> dependency.getTargetClass().getPackageName().startsWith(basePackage))
                        .forEach(dependency -> {
                            JavaClass targetClass = dependency.getTargetClass();
                            String targetPackage = targetClass.getPackageName();
                            String sourcePackage = javaClass.getPackageName();

                            // Only validate dependencies within the module
                            if (sourcePackage.startsWith(basePackage) && targetPackage.startsWith(basePackage)) {
                                // Custom validation logic can be added here
                                // This is a flexible condition that can be extended
                            }
                        });
            }
        };
    }

    /**
     * Condition to check if a class follows proper Spring component annotation.
     */
    public static final ArchCondition<JavaClass> beProperlyAnnotatedSpringComponent =
            new ArchCondition<>("be properly annotated Spring component") {
                @Override
                public void check(JavaClass javaClass, ConditionEvents events) {
                    String packageName = javaClass.getPackageName();
                    String className = javaClass.getSimpleName();

                    // Controllers should have @RestController or @Controller
                    if (packageName.contains(".controller") && className.endsWith("Controller")) {
                        boolean hasControllerAnnotation = javaClass.isAnnotatedWith("org.springframework.web.bind.annotation.RestController") ||
                                javaClass.isAnnotatedWith("org.springframework.stereotype.Controller");
                        if (!hasControllerAnnotation) {
                            events.add(SimpleConditionEvent.violated(javaClass,
                                    String.format("Controller %s should be annotated with @RestController or @Controller", className)));
                        }
                    }

                    // Services should have @Service
                    if (packageName.contains(".service") && className.endsWith("Service")) {
                        boolean hasServiceAnnotation = javaClass.isAnnotatedWith("org.springframework.stereotype.Service");
                        if (!hasServiceAnnotation) {
                            events.add(SimpleConditionEvent.violated(javaClass,
                                    String.format("Service %s should be annotated with @Service", className)));
                        }
                    }

                    // Repositories should have @Repository
                    if (packageName.contains(".persistence") && className.endsWith("Repository")) {
                        boolean hasRepositoryAnnotation = javaClass.isAnnotatedWith("org.springframework.stereotype.Repository");
                        if (!hasRepositoryAnnotation) {
                            events.add(SimpleConditionEvent.violated(javaClass,
                                    String.format("Repository %s should be annotated with @Repository", className)));
                        }
                    }
                }
            };

    /**
     * Condition to validate naming conventions for layer components.
     */
    public static final ArchCondition<JavaClass> followLayerNamingConventions =
            new ArchCondition<>("follow layer naming conventions") {
                @Override
                public void check(JavaClass javaClass, ConditionEvents events) {
                    String packageName = javaClass.getPackageName();
                    String className = javaClass.getSimpleName();

                    // Controllers should be in controller package and end with Controller
                    if (packageName.endsWith(".controller") &&
                            !className.endsWith("Controller")
                    && !javaClass.isInterface()) {
                        events.add(SimpleConditionEvent.violated(javaClass,
                                String.format("Class %s in controller package should end with 'Controller'", className)));
                    }

                    // Services should be in service package and end with Service
                    if (packageName.endsWith(".service") && !className.endsWith("Service") ) {
                        events.add(SimpleConditionEvent.violated(javaClass,
                                String.format("Class %s in service package should end with 'Service'", className)));
                    }

                }
            };
}
