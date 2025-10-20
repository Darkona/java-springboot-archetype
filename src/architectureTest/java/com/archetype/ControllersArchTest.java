package com.archetype;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.conditions.ArchConditions;
import io.swagger.v3.oas.annotations.Hidden;
import org.junit.jupiter.api.DisplayName;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = {"..controller..", "..controllers.."})
public class ControllersArchTest {


    @ArchTest
    @DisplayName("\"Info\" controller interfaces should be annotated with springdoc @Tag")
    void controller_info_impl(JavaClasses classes) {

        classes().that()
                 .areInterfaces().and().haveNameMatching(".*ControllerInfo")
                 .should().beAnnotatedWith(io.swagger.v3.oas.annotations.tags.Tag.class)
                 .because("ADR 0009: Controller OpenAPI annotations through interface-based \"Info\" classes")
                .allowEmptyShould(true)
                 .check(classes);
    }

    @ArchTest
    @DisplayName("Classes annotated with @RestController shouldn't be annotated with OpenAPI's annotations")
    void controller_should_be_clean(JavaClasses classes) {

        var selector = noClasses().that()
                                  .resideInAnyPackage("..controller..", "..controllers..")
                                  .and().areNotInterfaces()
                                  .and().haveSimpleNameNotContaining("Test");

        selector.should().beAnnotatedWith(io.swagger.v3.oas.annotations.tags.Tag.class)
                .orShould().beAnnotatedWith(io.swagger.v3.oas.annotations.info.Info.class)
                .orShould().beAnnotatedWith(io.swagger.v3.oas.annotations.info.Contact.class)
                .orShould().beAnnotatedWith(io.swagger.v3.oas.annotations.info.License.class)
                .orShould().beAnnotatedWith(io.swagger.v3.oas.annotations.security.SecurityScheme.class)
                .because("ADR 009: Controller OpenAPI annotations through interface-based \"Info\" classes")
                .allowEmptyShould(true)
                .check(classes);

    }

    private static final DescribedPredicate<JavaMethod> IS_SPRING_ENDPOINT =
            new DescribedPredicate<>("Spring endpoint method") {
                @Override
                public boolean test(JavaMethod m) {
                    return m.isAnnotatedWith(RequestMapping.class)
                            || m.isAnnotatedWith(GetMapping.class)
                            || m.isAnnotatedWith(PostMapping.class)
                            || m.isAnnotatedWith(PutMapping.class)
                            || m.isAnnotatedWith(DeleteMapping.class)
                            || m.isAnnotatedWith(PatchMapping.class);
                }
            };

    @ArchTest
    @DisplayName("Endpoints in Rest Controllers shouldn't be annotated with OpenApi annotations")
    void controller_endpoints_clean(JavaClasses classes) {

        methods()
                .that(IS_SPRING_ENDPOINT)
                .and().areDeclaredInClassesThat().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                .should().notBeAnnotatedWith(io.swagger.v3.oas.annotations.Operation.class)
                .orShould(ArchConditions.beAnnotatedWith(Hidden.class))
                .allowEmptyShould(true)
                .check(classes);
    }

}
