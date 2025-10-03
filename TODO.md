
## TODO

* Add tests similar to SkeletonBaggageValidators for all headers that are entities
* Search for unused methods
* Ensure skeleton can be updated on a single folder setup
* Search for unnecessary indirection
* Check all method documentation to match parameters and return values
* Add a rule to PMD to block use of `trim().isEmpty()` and suggest `isBlank()` that is performance tested to be faster
* Ensure client_name is mapped to request clients correctly from spring.
* Add rule to PMD to block use of trim().isEmpty() and suggest isBlank() that is performance tested to be faster
* Matadata - Owner, domain, slack, etc.
* Redis Pub/Sub Integration with Lists & BLPOP Workers – A framework to handle event-driven workers with Redis queues.
* Observability Layer with W3C Baggage Support – Tracing and correlation IDs built into all service calls.
  * DOING: Gonzalo
* Semantic Error Code Response Model – Standard JSON response format mapping semantic error codes to HTTP status codes.
* SLI/SO Metrics Definition – Predefined metrics (latency, success rates) exposed via Actuator and Prometheus endpoints.
* Security Framework – Spring Security, JWT/OAuth2, and request filters for tracing and baggage propagation.
* Configuration & Secrets Management – Profiles for environments and externalized configuration (e.g., Spring Cloud Config, Vault).
* Error Handling & Exception Mappers – A global @ControllerAdvice for error formatting with detailed messages.
  * DOING: Gonzalo
* CI/CD Templates – GitHub Actions or GitLab CI pipelines with quality gates (SonarQube, Jacoco coverage).
* Feature Flags & Toggle System – A way to enable/disable features dynamically (e.g., LaunchDarkly or Togglz).
* Integrar Darkona Logged
* Add Mapstruct support with fixes for JUnit and Lombok
* Add style recommendations for whitespaces
* Test Flyway migrations with Testcontainers

## DONE

* Database & Cache Layer Setup – Flyway for DB migrations and Redis connection pooling.
* Failed Customer Interaction (FCI) – SDK to push FCI to a Redis list that
  can be used as a queue to later consume store and integrate FCIs with documentation.
* Test Fci default context interceptor and fci exceptions after setting context
* Add spring application name validation for consistent naming in SkeletonConfigPropertiesValidator with an external validation class
* Added Lombok configuration for unit test coverage reporting with Jacoco.
* Centralized Logging & Tracing – Integration with OpenTelemetry (OTEL) or 
  Zipkin/Jaeger for distributed tracing.
* Add coding style for IntelliJ and VSCode
* Spring MVC uses virtual threads for better scalability.
* Fixed gradle build triggering integration and unit tests on every build.
* Standardized API Documentation – OpenAPI 3/Swagger setup with examples and schema definitions.
* Spring Boot MVC Skeleton (Java 21) – Base project structure following clean architecture principles.
  - Implemented using Spring Initializr with dependencies for Web, Data JPA, Redis, MongoDB, PostgreSQL, Actuator, JUnit, Testcontainers, etc.
  - Configured Gradle build with necessary plugins and dependencies.
  - Added integration tests specific sourcesets to Gradle configuration.
* Testing Strategy from Day One – JUnit 5, Testcontainers for Redis, integration tests:
  - Implemented test containers
  - Implemented integrated tests using test containers for MongoDB, Redis, and PostgreSQL with data insertion and validation.
  - Implemented docker-compose with configuration matching testcontainers tests to allow for copy and paste implementations.
  - Documented and tested docker-compose setup for local development and testing.

* ADR folder structure with global and local - support for global and local ADR with template
  - Added ADR template format
  - Added recommended ADRs as examples to start developing with sensitive defaults

* AI enabled
  - Added ADR agent to answer specific questions going over all documentation automatically

* Code Quality & Static Analysis Tools – Google Java Code style enforcement and pmd.
  - Implemented Google Java Style configuration.
  - Implemented PMD with custom ruleset to balance strictness and practicality.

* Health Checks & Readiness Endpoints – Actuator endpoints integrated with Redis and worker health checks.
  - Implemented Spring Boot Actuator with Integration tests.

* Containerization & Deployment Setup – Dockerfile, docker-compose for Redis, and Kubernetes manifests.
  - Implemented docker-compose with Redis, MongoDB, PostgreSQL, and the Spring Boot application.

* Create example CRUD applications for PostgreSQL, MongoDB, Redis.
  - Implemented example CRUD applications for PostgreSQL, MongoDB, and Redis with RESTful APIs.
  - Added integration tests for each CRUD application to validate functionality.
  - Implemented OpenAPI documentation for each CRUD application with example requests and responses.
  - Implemented Swagger UI for interactive API documentation.
  - Implemented FCI example controller to demonstrate pushing FCIs to a Redis.

## OUT OF SCOPE
Performance & Load Testing Scripts – Gatling or JMeter templates for worker load testing.

Async Processing & Retry Mechanisms – Configurable retry policies for workers using Redis queues.