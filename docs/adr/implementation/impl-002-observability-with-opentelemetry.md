# ADR Impl-002 — Observability: prefer Spring OpenTelemetry starter with optional agent

Date: 2025-10-03

Status: Accepted

Decision
--------
Applications scaffolded from this archetype SHOULD use the Spring OpenTelemetry starter (for example,
`org.springframework.boot:spring-boot-starter-opentelemetry` or the Spring Cloud OpenTelemetry starter when available) to integrate tracing and metrics the "
Spring way". This approach provides centralized, idiomatic configuration via Spring's configuration model and integrates cleanly with Micrometer and Spring
Boot's observability features.

The OpenTelemetry Java agent may be used for quick, out-of-the-box instrumentation in local debugging or non-production environments, but its use in production
should be avoided unless there is a compelling reason. The agent requires separate JVM configuration, may conflict with Spring-specific instrumentation, and can
complicate deployment and configuration management.

Context
-------
Observability is essential. The Spring starter allows applications to configure tracing and metrics using familiar Spring configuration, bind properties, and
auto-configuration. It also supports integration with Micrometer and OpenTelemetry exporters without requiring JVM-level agent configuration.

Rationale
---------

- Starter-based instrumentation keeps configuration and lifecycle under Spring's control.
- It is easier to document, test, and override via Spring profiles and properties.
- The agent is useful for exploratory use but can introduce complexity and unexpected behavior in production.

Policy
------

- Prefer the Spring OpenTelemetry starter for application instrumentation.
- The OpenTelemetry agent is allowed for local or diagnostic use but should not be relied upon as the primary instrumentation method in production.
- If a project intends to use the agent in production, document the reasons and risks in an ADR and obtain approval.

Implementation notes
--------------------

- Add the appropriate Spring OpenTelemetry starter dependency to build files for generated projects when observability is requested.
- Configure Micrometer/OpenTelemetry exporters via application properties and Spring configuration classes.
- Document how to enable the agent for local runs (e.g., `-javaagent:/path/to/opentelemetry.jar`) in developer documentation, but avoid baking agent
  configuration into production deployment descriptors.

Consequences
------------
Positive:

- Consistent, Spring-friendly observability configuration across projects.
- Easier to integrate tracing with Spring Boot features and Micrometer.
  Negative:
- Some auto-instrumentation provided by the agent may not be available if the agent is not used.
