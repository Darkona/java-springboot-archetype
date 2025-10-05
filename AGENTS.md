# AGENTS — How an AI agent should use this repository as an archetype

Purpose
-------
This repository is a starter / archetype intended to guide automated agents (or humans) when creating new projects. It contains multiple example architectures (layered, hexagonal, onion, MVC) and example implementations used for illustration only. Agents should use these examples to decide structure, conventions and patterns — not copy production implementations verbatim.

High-level intent for an agent
------------------------------
1. Inspect the entire repository and all ADRs to understand recommended architecture, conventions, and non-functional requirements.
2. Ask the user a small set of clarifying questions (see "Questions the agent must ask" below).
3. Pick one of the example module architectures (layer, hexagonal, onion, mvc) or choose `modulith`.
   - If you choose a single non-modulith architecture (layer, hexagonal, onion, mvc), scaffold the project using that architecture's example module and its ADRs.
   - If you choose `modulith`, ask the user which per-module architecture to apply (choose one of: layer, hexagonal, onion, mvc). All modules in the scaffold MUST follow the same chosen architecture. When scaffolding a modulith, the agent must use only the corresponding examples and ADRs from this repository for that chosen architecture and ignore other example modules and their ADRs.
   Create a new project scaffold that follows the chosen architecture's package layout and ADRs.
4. Use the repository examples (code, ADRs, ArchUnit rules, build files) as guidance only — do not copy implementation-level logic (domain logic, seeded data, example algorithms) into the new project.
5. Produce and persist a short ADR in the new project that documents the chosen architecture and any deviations the agent made.

Essential rules for the agent
-----------------------------
- Always obey the ADR documents present in `docs/adr/`. They codify the project's conventions (package layout, mapping rules, client patterns, logging, controller design, etc).
- Do NOT copy detailed implementation from example modules. Example code is for reference only (structure, naming, tests, DI patterns, mapper usage).
- Keep boundaries clear:
  - Domain packages must be framework- and persistence-free.
  - Controller/adapters use DTOs and mappers at the adapter boundary.
  - Clients live under a `clients.<service>` package and have separate client DTOs + mappers.
- If scaffolding a modulith, ensure every module uses the same architecture chosen by the user; the agent MUST use only the examples and ADRs for that selected architecture and ignore other architecture examples and their ADRs.
- Follow the `clients` pattern: for each external service, create `com.<group>.clients.<service>` with `client` interfaces, `dto` types, `mapper` adapters and optional `config`.
- Run and respect the ArchUnit/architecture tests (`src/architectureTest`) as part of validation.

Questions the agent must ask the user before scaffolding
-------------------------------------------------------
- What base group / root package should the new project use? (e.g. com.company.project)
- Which package layout do you want to use for the whole project? Choose one: `layer`, `hexagonal`, `onion`, `mvc`, `modulith`.
- What should the module / artifact name be? (e.g. orders-service)
- Which persistence technology should be included by default? (e.g. Mongo, JPA/Hibernate, none)
- Do you want pre-configured HTTP clients using OpenFeign for external services? If yes, list the services to scaffold (name + base URL property).
- Which build tool and Java version should be used? (Defaults: Gradle, Java 21)
- Any additional non-functional requirements (security, telemetry, tracing)?

Recommended agent workflow
--------------------------
1. Repo scan
   - Read ADRs in `docs/adr/` (especially those covering domain separation, package structure, HTTP clients, and controller design).
   - Inspect `build.gradle` and the `gradle/` helper scripts for test/source-set conventions (architectureTest, integrationTest, spockTest).
   - Review `src/` example modules: `layer`, `hexagonal`, `onion`, `mvc`.
   - Review `src/architectureTest` to learn enforced rules.

2. Ask the user the questions listed above and collect answers.

3. Choose the module template
   - Map user's choice to the corresponding example module in this repo.
   - Copy only the package/layout, test-style, configuration examples — do not copy domain logic or seeded database scripts.

4. Create new project scaffold
   - Generate project metadata (groupId, artifactId, version).
   - Create the top-level packages and placeholder classes (controllers, services, domain, persistence, dto, mapper, clients, config).
   - Add and configure dependencies analogous to this archetype:
     - MapStruct (mappers)
     - ArchUnit and architectureTest source set (optional to include as part of CI)
     - Spring Boot starters (web, security, data-* according to chosen persistence)
     - OpenFeign (per ADR 0007) if HTTP clients are required
     - Bean Validation for DTO validation (per ADR 0016)
   - Add an ADR documenting the chosen architecture (use an unused ADR number as a starting template and adapt it to the new project's package names).
   - Implement exception handling infrastructure following ADR 0016 (domain exceptions, global handler, i18n support).

5. Scaffold adapters for external services (optional)
   - For each external service the user requested, create `clients.<service>` with:
     - Feign client interface(s) annotated with `@FeignClient`
     - Client DTOs in `dto` package (separate from domain/persistence DTOs)
     - Client mappers in `mapper` package (MapStruct)
     - Optional client configuration class (timeouts/interceptors/error decoders)
   - Add properties for client base URLs (e.g., `clients.<service>.url`).

6. Add mapping and adapter examples
   - Add MapStruct mapper interfaces for domain ↔ DTO and client DTO ↔ persistence/domain.
   - Provide example service that demonstrates adapter usage (call client → map → persist / map → return DTO).
   - Implement domain-specific exceptions with error codes following ADR 0016.
   - Add global exception handler with RFC 9457 compliance and internationalization.

7. Add tests
   - Copy architecture test patterns (ArchUnit) to enforce package rules and update package names accordingly.
   - Provide basic unit test examples and an integration test profile (if desired).

8. Validation
   - Run `gradlew build` and `gradlew architectureTest` to ensure the scaffold compiles and passes architecture rules.
   - If tests fail, report clear diagnostics and suggest minimal fixes.

How to handle examples vs production code
-----------------------------------------
- This archetype contains small, illustrative examples (domain code, seeded DB, toy logic). The agent MUST treat those as examples only:
  - Use them to learn naming, package layout, DI patterns and test structure.
  - Do not copy business rules, sample data or algorithm implementations into the new project.
- Where the archetype demonstrates a pattern (e.g., "clients.<service>" layout, "mapper" usage, "pubsub" conventions), mirror the pattern — but implement project-specific DTOs and mappers.

Files and sections an agent should pay attention to
--------------------------------------------------
- docs/adr/ — architecture & conventions (currently 16 ADRs covering injection, domain separation, validation, testing, logging, package structure, HTTP clients, build standards, API documentation, observability, AOP, version management, test naming, architecture testing, controller design, and exception handling)
- build.gradle and gradle/*.gradle — dependency and source-set patterns
- src/architectureTest — ArchUnit examples and how architectureTest source-set is configured
- src/main/java/com/archetype/* — example modules (layer, onion, hexagonal, mvc)
- src/main/java/com/archetype/clients — example Feign clients, config, mappers
- src/integrationTest — examples of integration tests and testcontainers setup

Useful CLI commands (run from the project root)
-----------------------------------------------
- Run architecture tests:
  - Windows: cmd /c gradlew.bat architectureTest
  - Unix: ./gradlew architectureTest
- Full build:
  - Windows: cmd /c gradlew.bat build
  - Unix: ./gradlew build
- Run the app locally:
  - Windows: cmd /c gradlew.bat bootRun --args="--spring.profiles.active=local"
  - Unix: ./gradlew bootRun --args="--spring.profiles.active=local"

Local Development Setup
----------------------
Before running the application locally for the first time, you need to set up the local configuration:

1. Run the setup script to create the local-config directory:
   - Windows: setup-local-config.bat
   - Unix: ./setup-local-config.sh (if available)

   This creates a `local-config` directory outside the project with a `.env` file that contains:
   ```
   ENVIRONMENT=local
   ```

2. The `.env` file is automatically imported by `application.yaml` and provides local environment variables.

3. You can add additional local environment variables to this file as needed (database URLs, API keys, etc.)

### Running with Databases and Dependencies

By default, the local profile (`application-local.yaml`) has database and messaging auto-configurations disabled for simplified development. To enable full functionality:

1. **Start the required infrastructure** using the observability docker-compose:
   ```bash
   cd observability
   docker-compose up -d postgres mongodb rabbitmq
   ```

   This starts:
   - **PostgreSQL** on port 5432 (credentials: archetype/archetype)
   - **MongoDB** on port 27017 (credentials: archetype/archetype)
   - **RabbitMQ** on ports 5672 (AMQP) and 15672 (Management UI, credentials: archetype/archetype)

2. **Re-enable auto-configurations** in `src/main/resources/application-local.yaml` by commenting out the exclusions:
   ```yaml
   spring:
     autoconfigure:
       exclude:
         # - org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
         # - org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
         # - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
         # - org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
         # - org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
   ```

3. **Add database connection properties** to `../local-config/.env`:
   ```properties
   ENVIRONMENT=local
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/archetype
   SPRING_DATASOURCE_USERNAME=archetype
   SPRING_DATASOURCE_PASSWORD=archetype
   SPRING_DATA_MONGODB_URI=mongodb://archetype:archetype@localhost:27017/archetype?authSource=admin
   SPRING_RABBITMQ_HOST=localhost
   SPRING_RABBITMQ_PORT=5672
   SPRING_RABBITMQ_USERNAME=archetype
   SPRING_RABBITMQ_PASSWORD=archetype
   ```

4. Run the application with the local profile:
   ```bash
   gradlew.bat bootRun --args="--spring.profiles.active=local"
   ```

**Note**: The observability docker-compose also includes Redis, the full observability stack (Grafana, Loki, Tempo, Prometheus, Alloy), and more. You can start all services with `docker-compose up -d` or selectively start only what you need.

Optional Features
----------------
Some features are disabled by default for local development to simplify setup. See `docs/optional-configurations.md` for details on enabling:

- **OpenTelemetry Agent**: Automatic instrumentation for distributed tracing and observability
  - By default, the OpenTelemetry Java agent is NOT loaded during `bootRun`
  - To enable it, see the instructions in `docs/optional-configurations.md`
  - Required if you need distributed tracing or integration with observability backends (Jaeger, Tempo, etc.)
  - The observability stack (Grafana, Loki, Tempo, Prometheus) can be started with:
    ```
    cd observability
    docker-compose up -d
    ```

Generated Project Structure Examples
-----------------------------------
Here are examples of what the agent should generate for each architecture:

### Layered Architecture Project Structure
```
com.yourcompany.yourproject/
├── YourProjectApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── YourEntityController.java
│   └── YourEntityControllerInfo.java
├── service/
│   └── YourEntityService.java
├── domain/
│   ├── model/
│   │   └── YourEntity.java
│   └── dto/
│       ├── request/
│       │   └── YourEntityCreate.java
│       └── response/
│           └── YourEntityDetails.java
├── persistence/
│   ├── YourEntityRepository.java
│   └── document/
│       └── YourEntityDocument.java
├── mapper/
│   ├── dto/
│   │   └── YourEntityDtoMapper.java
│   └── persistence/
│       └── YourEntityPersistenceMapper.java
└── clients/
    └── externalservice/
        ├── ExternalServiceClient.java
        ├── dto/
        │   └── ExternalServiceDto.java
        └── mapper/
            └── ExternalServiceMapper.java
```

### Hexagonal Architecture Project Structure
```
com.yourcompany.yourproject/
├── YourProjectApplication.java
├── domain/
│   └── model/
│       └── YourEntity.java
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   ├── CreateYourEntity.java
│   │   │   └── ListYourEntities.java
│   │   └── out/
│   │       └── YourEntityRepositoryPort.java
│   └── service/
│       └── YourEntityService.java
└── adapter/
    ├── in/
    │   ├── web/
    │   │   ├── YourEntityController.java
    │   │   └── dto/
    │   │       ├── YourEntityCreateRequest.java
    │   │       └── YourEntityResponse.java
    │   └── messaging/
    │       └── YourEntityEventListener.java
    └── out/
        ├── persistence/
        │   ├── YourEntityEntity.java
        │   ├── YourEntityJpaRepository.java
        │   └── YourEntityRepositoryAdapter.java
        └── messaging/
            └── YourEntityEventPublisher.java
```

Validation Commands for Generated Projects
-----------------------------------------
After scaffolding, agents should run these commands to validate the generated project:

```bash
# Verify project builds successfully
./gradlew build

# Run architecture tests to ensure compliance
./gradlew architectureTest

# Verify application starts
./gradlew bootRun --args="--spring.profiles.active=local" &
sleep 10
curl -f http://localhost:8080/actuator/health
pkill -f "bootRun"

# Check code quality
./gradlew spotlessCheck
```

Common Troubleshooting Scenarios
-------------------------------

**Issue: Architecture tests fail after scaffolding**
- Check package naming matches chosen architecture
- Ensure domain models don't import Spring/persistence annotations
- Verify mappers are in correct packages

**Issue: Application fails to start**
- Check main class package matches root package
- Verify required dependencies are included
- Ensure configuration classes are properly annotated

**Issue: Build fails with compilation errors**
- Check MapStruct processors are configured
- Verify Lombok is properly set up
- Ensure all required annotations are present

**Issue: Tests fail to run**
- Check test source sets are properly configured
- Verify testcontainers dependencies for integration tests
- Ensure proper test profiles are used

Checklist template the agent should use (update per run)
-------------------------------------------------------
- [ ] Read ADRs and repo layout
- [ ] Ask user required questions (groupId, package style, artifactId, persistence, clients)
- [ ] Generate project scaffold (packages + placeholder types)
- [ ] Add dependencies and build config
- [ ] Add client packages (if requested)
- [ ] Add mappers and example adapters
- [ ] Add ADR for chosen architecture
- [ ] Run validation commands (build, architectureTest, health check)
- [ ] Verify code quality (spotlessCheck)
- [ ] Generate setup instructions for the user
- [ ] Deliver scaffold and a short report with next recommended steps

Advanced Scaffolding Techniques
------------------------------

### For Modulith Projects
When scaffolding a modulith, create separate packages for each business module:

```
com.yourcompany.yourproject/
├── YourProjectApplication.java
├── orders/              # Business module 1
│   ├── domain/
│   ├── application/
│   └── adapter/
├── inventory/           # Business module 2
│   ├── domain/
│   ├── application/
│   └── adapter/
├── shipping/            # Business module 3
│   ├── domain/
│   ├── application/
│   └── adapter/
└── shared/              # Shared components
    ├── config/
    └── common/
```

### Client Integration Patterns
For each external service, always create this structure:

```
com.yourcompany.yourproject.clients.servicename/
├── ServiceNameClient.java          # @FeignClient interface
├── ServiceNameConfiguration.java   # Client config (timeouts, etc.)
├── dto/
│   ├── ServiceNameRequest.java     # Request DTOs
│   └── ServiceNameResponse.java    # Response DTOs
└── mapper/
    └── ServiceNameMapper.java      # MapStruct mapper
```

### Exception Handling Patterns
Following ADR 0016, always implement:

```
com.yourcompany.yourproject/
├── domain/
│   └── exception/
│       ├── DomainException.java          # Base exception class
│       ├── EntityNotFoundException.java  # 404 errors
│       ├── EntityAlreadyExistsException.java  # 409 conflicts
│       ├── EntityValidationException.java     # 422 business validation
│       └── EntityServiceException.java        # 500 service errors
├── config/
│   ├── GlobalExceptionHandler.java       # RFC 9457 error responses
│   └── InternationalizationConfig.java   # i18n configuration
└── resources/
    └── messages.properties               # Error message templates
```

Error Handling and Fallback Strategies
-------------------------------------

**If user provides invalid architecture choice:**
Respond: "Invalid architecture choice. Please select one of: layer, hexagonal, onion, mvc, or modulith."

**If user provides invalid package name:**
Respond: "Invalid package name. Please use Java package naming conventions (e.g., com.company.project)."

**If scaffolding fails:**
1. Check target directory permissions
2. Verify all required templates are available
3. Ensure no naming conflicts with existing files
4. Fall back to basic scaffold without advanced features

**If validation fails:**
1. Report specific failing tests/commands
2. Provide suggested fixes
3. Offer to regenerate with simpler configuration

Post-Scaffolding User Instructions Template
------------------------------------------
After successful scaffolding, provide these instructions:

```
🎉 Project scaffolded successfully!

📁 Project Details:
- Root package: {package_name}
- Architecture: {architecture_type}
- Persistence: {persistence_type}
- External clients: {client_list}

🚀 Next Steps:
1. Navigate to your project directory: cd {project_directory}
2. Set up local development: ./setup-local-config.sh
3. Build the project: ./gradlew build
4. Run the application: ./gradlew bootRun --args="--spring.profiles.active=local"

🧪 Testing:
- Unit tests: ./gradlew test
- Integration tests: ./gradlew integrationTest
- Architecture tests: ./gradlew architectureTest

📚 Documentation:
- View your ADRs: docs/adr/ (create new ones using unused numbers as needed)
- API docs: http://localhost:8080/swagger-ui.html (when running)
- Actuator endpoints: http://localhost:8080/actuator

⚙️ Configuration:
- Local environment: ../local-config/.env
- Application config: src/main/resources/application-local.yaml

📝 TODO for your project:
- Replace example domain models with your business entities
- Implement your specific business logic
- Add authentication/authorization rules
- Configure external service URLs in .env file
- Set up CI/CD pipeline
- Create new ADRs as needed using the next unused number
```

Architecture Selection Decision Tree
-----------------------------------
To help agents make informed recommendations, use this decision tree:

**Choose LAYERED when:**
- Simple CRUD operations dominate
- Rapid prototyping is needed
- Team is new to advanced architectural patterns
- Business logic is straightforward

**Choose HEXAGONAL when:**
- Complex business logic with many external dependencies
- High testability requirements
- Need to isolate domain from infrastructure concerns
- Multiple adapters (web, CLI, messaging) required

**Choose ONION when:**
- Domain-driven design approach
- Rich domain models with complex business rules
- Enterprise applications with evolving requirements
- Strong separation of concerns is critical

**Choose MVC when:**
- Server-side rendered web applications
- Traditional web development patterns preferred
- Simple presentation logic
- Thymeleaf or similar templating engines used

**Choose MODULITH when:**
- Large application that could be multiple services
- Want to maintain deployment simplicity
- Need clear module boundaries within a monolith
- Planning future microservices extraction

Example questions the agent should present to the user (verbatim)
-----------------------------------------------------------------
1. "What base package/groupId should the new project use (e.g. com.acme.orders)?"
2. "Which package architecture should I use for the whole project: layer, hexagonal, onion, mvc, or modulith?"
3. "What is the artifact/module name for the new project?"
4. "Which persistence technology do you want (Mongo, JPA/Hibernate, none)?"
5. "List any external HTTP services you want a Feign client scaffolded for (name + property key for URL)."

Template Responses for Common Scenarios
--------------------------------------
**For simple business applications:**
"Based on your requirements for a straightforward CRUD application, I recommend the **layered architecture**. This provides clear separation between presentation, business, and data layers while remaining simple to understand and maintain."

**For complex domain logic:**
"Given the complexity of your business rules and the need for high testability, I suggest the **hexagonal architecture**. This will allow you to isolate your core business logic from external dependencies and make testing much easier."

**For enterprise applications:**
"For enterprise-grade applications with rich domain models, the **onion architecture** is ideal. It enforces dependency inversion and keeps your domain logic completely isolated from infrastructure concerns."

**For web applications with server-side rendering:**
"Since you're building a web application with server-side rendering, the **MVC architecture** is the most appropriate choice. It provides the traditional web development patterns you'll need."

Reporting expectations
----------------------
At the end of the scaffolding run the agent must produce:
- A summary of files created (paths).
- The ADR it created/updated describing the chosen architecture.
- Build & tests outcome and any failing tests or items requiring manual attention.
- Any assumptions it made (e.g., default package root, default persistence choice).

Notes for maintainers
---------------------
- Keep ADRs up to date. Agents rely on them as the single source of truth for conventions.
- If you change architecture tests or package rules, update `src/architectureTest` and `docs/adr` together to keep them consistent.
- If you want agents to scaffold different defaults (e.g., different Java version), update this file to reflect the new defaults.

License / Attribution
---------------------
This file is guidance for automated agents and humans using this repository as an archetype. The repository contains illustrative code; reuse should follow your project's licensing and contribution policies.
