# ADR 0008 — Build tool, Java version and dependency policy

Date: 2025-10-03

Status: Accepted

Decision
--------
This project and any projects scaffolded from this archetype MUST use:

- Gradle 9.1.0 (wrapper) for builds.
- Java 21 as the language and runtime.

All application-level functionality MUST be implemented using Spring Boot starters or libraries from the Spring ecosystem where equivalents exist. Do not
introduce alternative libraries for functionality that Spring provides via a supported starter or first-party Spring project.

Whenever a required functionality CANNOT be implemented using SpringBoot starters or libraries, other libraries may be used, but an ADR explaining the rationale
for doing so must be added to the project and approved by architects.

This is a mandatory, project-level architectural decision and must be respected by automated agents and developers alike.

Context
-------
The repository serves as an archetype and reference for scaffolding new projects. To reduce incidental complexity and maximize operational consistency across
generated projects, build and dependency choices are standardized:

- Using a single Gradle version across all projects and CI agents avoids tooling inconsistencies and simplifies plugin and wrapper management.
- Standardizing on Java 21 ensures consistent language features, bytecode target, and runtime assumptions.
- Encouraging Spring Boot starters and Spring-first implementations reduces divergence in configuration, idioms, and supportability for monitoring, security,
  and observability.

Rationale
---------

- Consistent build tool and Java version reduce environment-related build failures and CI drift.
- Spring Boot starters provide production-ready defaults, autoconfiguration, and strong ecosystem integration (security, data, messaging, cloud).
- Centralized dependency policy reduces the risk of incompatible transitive dependencies, duplicated functionality, and long-term maintenance debt.
- Requiring ADRs for deviations enforces architectural review for non-standard dependencies.

Policy — mandatory rules
------------------------

1. Gradle
    - Every project generated from this archetype MUST use the Gradle wrapper set to version `9.1.0`.
    - Example: `gradle/wrapper/gradle-wrapper.properties` must contain `distributionUrl` referencing Gradle 9.1.0.
2. Java
    - The project language level and runtime target MUST be Java 21.
    - Build files/toolchains must be configured to compile and target Java 21.
3. Spring
    - Prefer and use Spring Boot starters and Spring first-party projects for required capabilities (web, data, security, cloud, observability, etc.).
    - Avoid introducing alternate libraries when Spring provides an appropriate, supported solution via a starter or Spring project.
4. Permitted non-Spring libraries (non-exhaustive)
    - The following third-party libraries are explicitly permitted without additional ADRs:
        - ArchUnit
        - PIT (pitest)
        - MapStruct
        - Lombok
        - Spock
        - Apache Camel
        - Apache PDFBox
        - Logged
        - Apache POI
        - JUnit (Jupiter)
        - Faker (data generation)
    - Any other non-Spring third-party dependency requires an ADR describing:
        - Technical justification
        - Security/maintenance considerations
        - Migration/rollback plan
        - Approval by architecture owners
5. Dependency justification and review
    - New non-permitted dependencies must be proposed by creating an ADR (or an extension to an ADR) and obtaining approval before merging into mainline
      branches.
6. Avoid duplicating functionality
    - Do not introduce a library that duplicates core Spring Boot functionality unless a valid, documented reason exists and is approved via ADR.

Consequences
------------
Positive:

- Uniform developer experience across projects and modules.
- Easier CI/CD and tooling maintenance due to consistent Gradle and Java versions.
- Reduced risk from incompatible or unnecessary third-party dependencies.

Negative:

- May exclude some libraries that would otherwise be convenient; teams must open ADRs to request exceptions.
- Tighter governance introduces a small process overhead for unusual dependency choices.

Implementation / enforcement
----------------------------

- Ensure the Gradle wrapper is checked into the repository with Gradle 9.1.0 (this archetype includes a wrapper configured to that version).
- Configure Gradle toolchain and compile options to target Java 21 in the archetype's `build.gradle` and template project scaffolds.
- Update CI templates to use the Gradle wrapper and JDK 21 images/runners.
- Add a checklist item to the project's PR template requiring maintainers to confirm new non-Spring dependencies are permitted or ADR-approved.
- Optionally add a Gradle or CI check that scans dependencies and fails when disallowed libraries are introduced (map allowed list vs introduced dependencies).
- This can be enforced via a Gradle task or CI script.

Examples
--------

- Allowed: `implementation 'org.springframework.boot:spring-boot-starter-web'`
- Allowed: `testImplementation 'com.tngtech.archunit:archunit:1.1.0'`
- Disallowed (without ADR): `implementation 'org.apache.httpcomponents:httpclient'` if Spring's REST client alternatives or Feign are available and appropriate;
- introduce only with an ADR.

Notes
-----

- This ADR is intentionally strict to preserve long-term maintainability of projects generated from this archetype. Agents that scaffold projects from this
  repository must follow these rules automatically (configure wrapper, toolchain, and dependency choices).
- If it becomes necessary to relax the permitted list, update this ADR accordingly and document the rationale.
