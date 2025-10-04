# Onion Architecture Module - Pokemon Trainer

This document describes the Pokemon Trainer module implemented using Onion Architecture pattern.

## Overview

The Trainer module demonstrates a complete implementation of Onion Architecture with:
- **MongoDB** for persistence
- **Redis** for caching
- **REST API** for presentation
- **Testcontainers** for integration testing

## Architecture Layers

### 1. Domain (Core)
Pure business logic with no framework dependencies.

**Location**: `com.archetype.onion.domain.model`

- **Trainer**: Domain entity representing a Pokemon trainer
- **PokemonOwnership**: Value object for Pokemon ownership details
- Domain rules enforced:
  - Maximum 6 Pokemon per trainer
  - Unique nicknames per trainer
  - Name validation
  - Badges cannot be negative

### 2. Application Layer
Use cases and ports defining application boundaries.

**Locations**:
- `com.archetype.onion.application.ports.in`: Input ports (use cases)
- `com.archetype.onion.application.ports.out`: Output ports (repository contracts)
- `com.archetype.onion.application.services`: Application services

**Key Components**:
- **TrainerUseCase**: Defines business operations
- **TrainerService**: Implements use cases, coordinates domain logic
- Caching annotations applied here (@Cacheable, @CachePut, @CacheEvict)

### 3. Infrastructure Layer
Implements output ports with framework-specific code.

**Location**: `com.archetype.onion.infrastructure`

**Persistence** (`infrastructure.persistence`):
- **TrainerDocument**: MongoDB document entity
- **TrainerMongoRepository**: Spring Data MongoDB repository
- **TrainerRepositoryAdapter**: Implements repository port, maps between domain and documents

**Configuration** (`infrastructure.config`):
- **CacheConfig**: Redis cache configuration (5-minute TTL)
- **TrainerDemoDataLoader**: Loads sample data in `demo` profile

### 4. Presentation Layer
Exposes the application to external consumers.

**Location**: `com.archetype.onion.presentation`

**REST** (`presentation.rest`):
- **TrainerController**: REST endpoints

**DTOs** (`presentation.dto`):
- **TrainerDTO**: Request/response DTO
- **PokemonOwnershipDTO**: Ownership DTO

**Mappers** (`presentation.mapper`):
- **TrainerMapper**: MapStruct mapper for domain ↔ DTO conversion

## Caching Strategy

### Redis Configuration

- **Cache Name**: `trainers`
- **TTL**: 5 minutes
- **Serialization**: JSON (Jackson)
- **Strategy**: Cache-aside pattern

### Cached Operations

| Operation | Cache Behavior |
|-----------|---------------|
| `createTrainer` | @CachePut - adds to cache |
| `addPokemonToTrainer` | @CachePut - updates cache |
| `getTrainer` | @Cacheable - reads from cache |
| `deleteTrainer` | @CacheEvict - removes from cache |
| `listTrainers` | No caching (always fresh from DB) |

## Running Locally

### Prerequisites

1. Docker and Docker Compose installed
2. Java 21
3. Gradle

### Step 1: Start Infrastructure

Start MongoDB and Redis using the observability docker-compose:

```bash
cd observability
docker-compose up -d mongodb redis
```

This starts:
- **MongoDB**: localhost:27017 (credentials: archetype/archetype)
- **Redis**: localhost:6379 (password: archetype)

### Step 2: Configure Local Environment

Ensure `application-local.yaml` has MongoDB and Redis auto-configurations **enabled**:

```yaml
spring:
  autoconfigure:
    exclude:
      # Comment out these lines to enable MongoDB and Redis:
      # - org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
      # - org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
```

Add connection properties to `../local-config/.env`:

```properties
ENVIRONMENT=local

# MongoDB
SPRING_DATA_MONGODB_URI=mongodb://archetype:archetype@localhost:27017/archetype?authSource=admin

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=archetype
```

### Step 3: Run the Application

```bash
# With demo data
gradlew.bat bootRun --args="--spring.profiles.active=local,demo"

# Without demo data
gradlew.bat bootRun --args="--spring.profiles.active=local"
```

## REST API Endpoints

### Create Trainer
```http
POST /api/trainers
Content-Type: application/json

{
  "name": "Ash Ketchum",
  "badges": 0
}
```

### Add Pokemon to Trainer
```http
POST /api/trainers/{trainerId}/pokemon
Content-Type: application/json

{
  "pokemonId": "25",
  "nickname": "Pikachu"
}
```

### Get Trainer
```http
GET /api/trainers/{trainerId}
```

### List All Trainers
```http
GET /api/trainers
```

### Delete Trainer
```http
DELETE /api/trainers/{trainerId}
```

### Swagger UI

Access API documentation at: `http://localhost:8080/swagger-ui/index.html`

## Integration Testing

### Testcontainers Setup

Integration tests automatically start MongoDB and Redis containers:

```bash
gradlew.bat integrationTest
```

**Test Coverage**:
- Trainer creation
- Pokemon addition with domain rule enforcement
- Six Pokemon limit validation
- Duplicate nickname prevention
- Caching behavior verification

**Containers**:
- MongoDB 7
- Redis 7-alpine

**Dynamic Configuration**: Test properties auto-configure to use Testcontainers ports

## Demo Data

When running with `demo` profile, the following trainers are automatically created:

1. **Ash Ketchum** (8 badges)
   - Pikachu
   - Charizard

2. **Misty** (5 badges)
   - Staryu
   - Psyduck

## Monitoring Cache Performance

### Via Redis CLI

```bash
# Connect to Redis
docker exec -it archetype-redis redis-cli -a archetype

# Check cache keys
KEYS trainers::*

# Get cache entry
GET "trainers::trainer-id-here"

# Monitor cache hits/misses
MONITOR
```

### Via Spring Actuator

Access cache metrics at:
```
GET http://localhost:8080/actuator/metrics/cache.gets?tag=name:trainers
```

## Architecture Benefits

### Dependency Inversion
- Domain has no dependencies on infrastructure
- Application layer depends only on domain
- Infrastructure depends on application (implements ports)

### Testability
- Domain logic testable without frameworks
- Application services testable with mocked ports
- Integration tests with real infrastructure (Testcontainers)

### Flexibility
- Easy to swap persistence (MongoDB → PostgreSQL)
- Easy to swap cache (Redis → Hazelcast)
- Easy to add new presentation layers (GraphQL, gRPC)

## Troubleshooting

### MongoDB Connection Issues
```bash
# Check MongoDB is running
docker ps | grep mongo

# View MongoDB logs
docker logs archetype-mongodb

# Test connection
mongosh "mongodb://archetype:archetype@localhost:27017/archetype?authSource=admin"
```

### Redis Connection Issues
```bash
# Check Redis is running
docker ps | grep redis

# View Redis logs
docker logs archetype-redis

# Test connection
docker exec -it archetype-redis redis-cli -a archetype PING
```

### Cache Not Working
- Verify `@EnableCaching` in CacheConfig
- Check Redis connection in application logs
- Confirm cache annotations in TrainerService
- Monitor cache operations with Redis MONITOR command

## Next Steps

- Add pagination to `listTrainers` endpoint
- Implement trainer search by name
- Add Pokemon details enrichment via PokeAPI client
- Implement event sourcing for trainer history
- Add batch operations for Pokemon management

## References

- [Onion Architecture](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [Spring Cache Abstraction](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache)
- [Testcontainers](https://www.testcontainers.org/)
