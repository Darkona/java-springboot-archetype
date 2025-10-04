package com.archetype;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchitectureRules {

    static final ArchCondition<JavaClass> implementMatchingInfoInterface =
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
}
