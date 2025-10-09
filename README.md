# Java Spring Boot Modulith Archetype

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green) ![Gradle](https://img.shields.io/badge/Gradle-8.x-blue) ![License](https://img.shields.io/badge/License-MIT-yellow)

A comprehensive **Java 21 Spring Boot archetype** designed for **AI agents** and developers to generate new projects with multiple architectural patterns. This archetype provides a **modulith foundation** that can be used to create either single-service applications or complex modular monoliths.

## 🎯 What Is This Archetype?

This project serves as a **template and reference implementation** that demonstrates:

- **4 Different Architectural Patterns**: Layer, Hexagonal (Ports & Adapters), Onion, and MVC
- **Modulith Design**: Build modular monoliths where each module can follow different architectural principles
- **AI Agent Ready**: Specifically designed for automated project generation
- **Production Patterns**: Real-world conventions, testing strategies, and operational concerns
- **Comprehensive Tooling**: Architecture testing, observability, security, and CI/CD ready

## 🚀 Quick Start

### For AI Agents
See the detailed [`AGENTS.md`](AGENTS.md) file for comprehensive instructions on using this archetype programmatically.

### For Human Developers

1. **Clone the repository**
   ```bash
   git clone https://github.com/Darkona/java-springboot-archetype.git
   cd java-springboot-archetype
   ```

2. **Set up local development**
   ```bash
   # Windows
   setup-local-config.bat
   
   # Unix/Linux
   chmod +x setup-local-config.sh
   ./setup-local-config.sh
   ```

3. **Run the application**
   ```bash
   # Windows
   gradlew.bat bootRun --args="--spring.profiles.active=local"
   
   # Unix/Linux
   ./gradlew bootRun --args="--spring.profiles.active=local"
   ```

## 🏗️ Architecture Modules

This archetype contains **4 complete architectural implementations** in separate packages. Each demonstrates different architectural principles:

| Architecture | Package | Description | Best For |
|--------------|---------|-------------|----------|
| **Layered** | `com.archetype.layer` | Traditional N-tier architecture | Simple CRUD applications, rapid prototyping |
| **Hexagonal** | `com.archetype.hexagonal` | Ports & Adapters pattern | Complex business logic, testability focus |
| **Onion** | `com.archetype.onion` | Dependency inversion architecture | Domain-driven design, enterprise applications |
| **MVC** | `com.archetype.mvc` | Model-View-Controller pattern | Web applications with server-side rendering |

### Package Structure Examples

<details>
<summary><strong>📁 Layered Architecture</strong></summary>

```
com.archetype.layer/
├── controller/          # REST endpoints
├── service/            # Business logic
├── domain/             # Domain models and DTOs
│   ├── model/         # Domain entities
│   └── dto/           # Data transfer objects
├── persistence/        # Data access layer
│   └── document/      # Database entities
└── mapper/            # MapStruct mappers
    ├── dto/          # Domain ↔ DTO mapping
    └── persistence/   # Domain ↔ Entity mapping
```
</details>

<details>
<summary><strong>🔗 Hexagonal Architecture</strong></summary>

```
com.archetype.hexagonal/
├── domain/
│   └── model/         # Pure domain models
├── application/
│   ├── port/          # Interfaces (in/out ports)
│   │   ├── in/       # Driving adapters interfaces
│   │   └── out/      # Driven adapters interfaces
│   └── service/       # Application services
└── adapter/
    ├── in/           # Driving adapters
    │   ├── web/      # REST controllers
    │   └── messaging/ # Event listeners
    └── out/          # Driven adapters
        ├── persistence/ # Database adapters
        └── messaging/   # Event publishers
```
</details>

<details>
<summary><strong>🧅 Onion Architecture</strong></summary>

```
com.archetype.onion/
├── domain/
│   └── model/         # Core domain entities
├── application/
│   ├── ports/         # Application interfaces
│   │   ├── in/       # Use case interfaces
│   │   └── out/      # Repository interfaces
│   └── services/      # Application services
├── infrastructure/    # External concerns
│   ├── config/       # Configuration
│   └── persistence/  # Database implementations
└── presentation/      # Presentation layer
    ├── rest/         # REST controllers
    ├── dto/          # Presentation DTOs
    └── mapper/       # Presentation mappers
```
</details>

<details>
<summary><strong>📱 MVC Architecture</strong></summary>

```
com.archetype.mvc/
├── controller/        # MVC controllers
├── model/            # View models
├── service/          # Business services
└── persistence/      # Data access
    └── document/     # Database entities
```
</details>

## 🛠️ Technology Stack

### Core Framework
- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.x** - Application framework and auto-configuration
- **Spring Modulith** - Modular monolith support with module boundaries

### Data & Persistence
- **Spring Data JPA** - PostgreSQL integration with Hibernate
- **Spring Data MongoDB** - NoSQL document store support
- **Spring Data Redis** - Caching and session storage
- **Flyway** - Database migration management

### Web & API
- **Spring Web MVC** - REST API development
- **Spring Security** - Authentication and authorization
- **OpenAPI 3 (SpringDoc)** - API documentation and Swagger UI
- **OpenFeign** - Declarative HTTP clients

### Messaging & Events
- **Spring AMQP** - RabbitMQ integration for messaging
- **Spring Modulith Events** - Internal event handling

### Observability & Monitoring
- **OpenTelemetry** - Distributed tracing and metrics
- **Micrometer** - Application metrics
- **Prometheus** - Metrics collection
- **Grafana Stack** - Observability platform (Loki, Tempo)

### Development & Quality
- **MapStruct** - Type-safe mapping between layers
- **Lombok** - Reducing boilerplate code
- **ArchUnit** - Architecture testing
- **Spock Framework** - BDD testing with Groovy
- **Testcontainers** - Integration testing with real dependencies

### Build & Deployment
- **Gradle 8.x** - Build automation with version catalogs
- **Docker** - Containerization support
- **AspectJ** - Aspect-oriented programming for cross-cutting concerns

## 🧪 Testing Strategy

The archetype includes **4 types of testing** with dedicated source sets:

| Test Type | Source Set | Purpose | Tools |
|-----------|------------|---------|--------|
| **Unit Tests** | `src/test` | Fast, isolated component testing | JUnit 5, Mockito |
| **Integration Tests** | `src/integrationTest` | Component integration testing | Testcontainers, Spring Test |
| **Architecture Tests** | `src/architectureTest` | Enforce architectural rules | ArchUnit |
| **Spock Tests** | `src/spockTest` | BDD-style testing | Spock Framework (Groovy) |

### Running Tests

```bash
# All tests
./gradlew check

# Individual test types
./gradlew test                    # Unit tests
./gradlew integrationTest         # Integration tests
./gradlew architectureTest        # Architecture compliance
./gradlew spockTest              # BDD tests
```

## 📋 Development Workflow

### Local Development Setup

1. **Initialize local configuration**
   ```bash
   # Creates ../local-config/.env with local settings
   ./setup-local-config.sh  # or .bat on Windows
   ```

2. **Start infrastructure dependencies (optional)**
   ```bash
   cd observability
   docker-compose up -d postgres mongodb rabbitmq
   ```

3. **Configure application profiles**
   - `local` - Simplified development (default)
   - `container` - Full containerized environment
   - `petshop` - Example business domain profile

### Package Renaming

Use the provided scripts to rename packages for new projects:

```bash
# Rename all packages in the project
./rename-package.sh src com.archetype com.yourcompany.yourproject
# or on Windows: rename-package.bat src com.archetype com.yourcompany.yourproject
```

### Building and Running

```bash
# Build the application
./gradlew build

# Run locally with simplified configuration
./gradlew bootRun --args="--spring.profiles.active=local"

# Build Docker image
./gradlew bootBuildImage --imageName=your-app:latest
```

## 🤖 AI Agent Integration

This archetype is specifically designed for AI agents to generate new projects. Key features for automation:

### Decision Framework

AI agents should ask users to choose:

1. **Architecture Pattern**: `layer`, `hexagonal`, `onion`, `mvc`, or `modulith`
2. **Package Structure**: Base package name (e.g., `com.company.project`)
3. **Persistence**: `MongoDB`, `PostgreSQL`, `JPA`, or `none`
4. **External Services**: List of services needing OpenFeign clients
5. **Features**: Security, observability, messaging requirements

### Scaffolding Guidelines

- **Single Architecture**: Use one of the 4 example modules as template
- **Modulith**: Ask user which architecture to apply to all modules
- **Follow ADRs**: Respect all architectural decision records in `docs/adr/`
- **Preserve Patterns**: Copy structure and patterns, not business logic
- **Validate**: Run architecture tests to ensure compliance

See [`AGENTS.md`](AGENTS.md) for detailed implementation guidance.

## 📚 Architectural Decision Records (ADRs)

This directory contains all architectural decisions for the Java Spring Boot archetype, organized by theme with independent numbering per category.

### 📁 Foundation - Core Standards
| ADR | Title | Key Decisions |
|-----|-------|---------------|
| [foundation/found-001](docs/adr/foundation/found-001-build-and-dependency-standards.md) | Build and Dependency Standards | Gradle, Java 21, dependency policy |
| [foundation/found-002](docs/adr/foundation/found-002-java-21-language-features.md) | Java 21 language features and modern coding practices | Records, switch expressions, modern patterns |
| [foundation/found-003](docs/adr/foundation/found-003-constructor-injection.md) | Prefer constructor injection (Lombok @RequiredArgsConstructor) over field injection (@Autowired) | Constructor injection preferred; use Lombok where available |
| [foundation/found-004](docs/adr/foundation/found-004-gradle-version-catalog.md) | Use Gradle Version Catalog (TOML) for Centralized Dependency Management | Gradle version catalogs / centralized dependency management |

### 🏗️ Architecture - Domain Design & Cross-cutting Patterns
| ADR | Title | Key Decisions |
|-----|-------|---------------|
| [architecture/arch-001](docs/adr/architecture/arch-001-domain-separation-and-mapping.md) | Domain separation and mapping | Layer separation with MapStruct mapping |
| [architecture/arch-002](docs/adr/architecture/arch-002-domain-validation-strategy.md) | Domain Validation Strategy | Validation strategies and domain exceptions |
| [architecture/arch-003](docs/adr/architecture/arch-003-testing-strategy-and-tdd.md) | Domain testing standards: 100% unit coverage, mutation testing, and TDD | Testing & mutation testing requirements |
| [architecture/arch-004](docs/adr/architecture/arch-004-openfeign-http-clients.md) | Use Spring Cloud OpenFeign for synchronous HTTP clients | OpenFeign for external service integration |
| [architecture/arch-005](docs/adr/architecture/arch-005-spring-annotations-over-responseentity.md) | Prefer Spring annotations over ResponseEntity in controllers | Controller design: annotations preferred |
| [architecture/arch-006](docs/adr/architecture/arch-006-exception-handling-strategy.md) | Exception handling strategy with RFC 9457 compliance | RFC 9457 Problem Details, i18n, domain exceptions |

### ⚙️ Implementation - Tool Configurations & Conventions
| ADR | Title | Key Decisions |
|-----|-------|---------------|
| [implementation/impl-001](docs/adr/implementation/impl-001-logging-standards-and-obfuscation.md) | Use Logged for local development and enforce sensitive-data obfuscation | Local dev logging and redaction rules |
| [implementation/impl-002](docs/adr/implementation/impl-002-observability-with-opentelemetry.md) | Observability with OpenTelemetry | OpenTelemetry integration guidance |
| [implementation/impl-003](docs/adr/implementation/impl-003-openapi-documentation-standards.md) | OpenAPI documentation standards | API documentation and OpenAPI 3 standards |
| [implementation/impl-004](docs/adr/implementation/impl-004-architecture-testing-strategy.md) | Architecture testing strategy | ArchUnit testing strategy |
| [implementation/impl-005](docs/adr/implementation/impl-005-aspectj-weaving-configuration.md) | AspectJ weaving configuration | AOP weaving configuration |
| [implementation/impl-006](docs/adr/implementation/impl-006-test-naming-conventions.md) | Test naming conventions | Readable test naming guidance |

### 🎯 Patterns - Architecture-Specific Guidance
| ADR | Title | Key Decisions |
|-----|-------|---------------|
| [patterns/pattern-001](docs/adr/patterns/pattern-001-layered-architecture-package-structure.md) | Layered architecture package structure | Layered architecture organization |
| [patterns/pattern-002](docs/adr/patterns/pattern-002-hexagonal-architecture-package-structure.md) | Hexagonal architecture package structure | Ports & Adapters organization |
| [patterns/pattern-003](docs/adr/patterns/pattern-003-onion-architecture-package-structure.md) | Onion architecture package structure | Dependency inversion and layering |
| [patterns/pattern-004](docs/adr/patterns/pattern-004-classical-mvc-package-structure.md) | Classical MVC package structure | Server-side rendered app patterns |
| [patterns/pattern-005](docs/adr/patterns/pattern-005-cqrs-architecture-package-structure.md) | CQRS architecture package structure | CQRS guidance and structure |

See [docs/adr/README.md](docs/adr/README.md) for complete navigation guide.

## 🔧 Helper Scripts

| Script | Platform | Purpose |
|--------|----------|---------|
| `setup-local-config.sh/.bat` | Cross-platform | Initialize local development environment |
| `rename-package.sh/.bat` | Cross-platform | Rename packages throughout the project |
| `fix-encoding.bat` | Windows | Fix console encoding issues |

## 📊 Observability Stack

The archetype includes a complete observability setup in the `observability/` directory:

### Components
- **Grafana** - Dashboards and visualization
- **Prometheus** - Metrics collection
- **Loki** - Log aggregation
- **Tempo** - Distributed tracing
- **Alloy** - OpenTelemetry collector

### Quick Start
```bash
cd observability
docker-compose up -d

# Access services
open http://localhost:3000  # Grafana (admin/admin)
open http://localhost:9090  # Prometheus
```

## 🤝 Contributing

### For Archetype Maintainers

1. **Update ADRs** when changing architectural decisions
2. **Maintain Examples** - Keep all 4 architecture examples in sync
3. **Update Tests** - Ensure ArchUnit rules match conventions
4. **Version Catalog** - Keep dependencies current in `gradle/libs.versions.toml`

### For Project Usage

1. **Fork or Use as Template** - Don't contribute project-specific changes back
2. **Report Issues** - Architecture or setup problems only
3. **Suggest Improvements** - General archetype enhancements

## 📖 Additional Documentation

- [`AGENTS.md`](AGENTS.md) - Comprehensive AI agent integration guide
- [`CONTRIBUTING.md`](CONTRIBUTING.md) - Contribution guidelines
- [`docs/console-utf8.md`](docs/console-utf8.md) - Console encoding fixes
- [`docs/onion-module.md`](docs/onion-module.md) - Onion architecture deep dive
- [`docs/optional-configurations.md`](docs/optional-configurations.md) - Advanced setup options

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🎯 Project Goals

This archetype aims to:

1. **Accelerate Development** - Provide ready-to-use architectural patterns
2. **Ensure Consistency** - Enforce conventions through testing and documentation
3. **Enable AI Generation** - Support automated project scaffolding
4. **Demonstrate Best Practices** - Show real-world Spring Boot patterns
5. **Support Moduliths** - Enable modular monolith development

---

**Ready to start building?** Choose your architecture, run the setup scripts, and begin developing with proven patterns and comprehensive tooling.
