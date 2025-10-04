# Optional Configurations

This document contains optional configurations that can be enabled if needed for specific use cases.

## OpenTelemetry Agent (Optional)

The OpenTelemetry Java agent provides automatic instrumentation for observability. It is **disabled by default** in local development but can be enabled when needed.

### When to Enable

Enable OpenTelemetry when you need:
- Distributed tracing across microservices
- Automatic instrumentation of HTTP clients, databases, and frameworks
- Integration with observability backends (Jaeger, Tempo, etc.)

### How to Enable

1. **Modify `build.gradle`**: Update the `bootRun` task to load the OpenTelemetry agent:

```groovy
// ===== Boot Run Configuration =====
bootRun {
    dependsOn(stageAgents)
    doFirst {
        def dir = agentsOut.get().asFile
        def aj = new File(dir, "aspectjweaver.jar")
        def ot = new File(dir, "opentelemetry.jar")

        if (!aj.exists() || !ot.exists()) {
            throw new GradleException("Can't find the agents for AspectJ/OpenTelemetry.")
        }
        jvmArgs += "-javaagent:${aj.absolutePath}"
        jvmArgs += "-javaagent:${ot.absolutePath}"
    }
}
```

2. **Configure application properties**: Remove or comment out the OpenTelemetry disabling configuration in `application-local.yaml`:

```yaml
# Remove or comment out these lines:
# otel:
#   sdk.disabled: true
#   javaagent.enabled: false
#   instrumentation.common.default-enabled: false
```

3. **Set up observability backend**: Ensure you have an OTLP-compatible backend running (e.g., via the observability docker-compose):

```bash
cd observability
docker-compose up -d
```

### Configuration Options

The OpenTelemetry agent can be configured via environment variables or system properties:

- `OTEL_SERVICE_NAME`: Service name for traces (default: application name)
- `OTEL_EXPORTER_OTLP_ENDPOINT`: OTLP endpoint URL (default: http://localhost:4318)
- `OTEL_TRACES_EXPORTER`: Trace exporter type (default: otlp)
- `OTEL_METRICS_EXPORTER`: Metrics exporter type (default: otlp)
- `OTEL_LOGS_EXPORTER`: Logs exporter type (default: otlp)

For production deployments, configure these in your application-{profile}.yaml or as environment variables.

### Disabling in Local Development

To disable OpenTelemetry in local development (current default):

1. Keep the `build.gradle` bootRun task without the OpenTelemetry agent line
2. Add these properties to `application-local.yaml`:

```yaml
otel:
  metrics.exporter: none
  logs.exporter: none
  traces.exporter: none
  sdk.disabled: true
  javaagent.enabled: false
  instrumentation.common.default-enabled: false
```

### References

- [OpenTelemetry Java Agent Documentation](https://opentelemetry.io/docs/languages/java/automatic/)
- [ADR 0010 - Observability with OpenTelemetry](adr/0010-observability-opentelemetry.md)

---

## Running with Databases (PostgreSQL, MongoDB, RabbitMQ)

By default, the local profile has database and messaging auto-configurations disabled to simplify initial setup. This allows you to run the application without external dependencies.

### When to Enable

Enable databases and messaging when you need:
- Full persistence functionality
- Integration testing with real databases
- Message queue functionality
- To test the complete application stack locally

### How to Enable

1. **Start the infrastructure services** using Docker Compose:
   ```bash
   cd observability
   docker-compose up -d postgres mongodb rabbitmq
   ```

   Available services:
   - **PostgreSQL**: Port 5432 (user: `archetype`, password: `archetype`, database: `archetype`)
   - **MongoDB**: Port 27017 (user: `archetype`, password: `archetype`, database: `archetype`)
   - **RabbitMQ**: Port 5672 for AMQP, Port 15672 for Management UI (user: `archetype`, password: `archetype`)
   - **Redis**: Port 6379 (password: `archetype`) - optional, start with `docker-compose up -d redis`

2. **Re-enable auto-configurations** in `src/main/resources/application-local.yaml`:
   
   Comment out the exclusions:
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

3. **Configure connection properties** in `../local-config/.env`:
   ```properties
   ENVIRONMENT=local
   
   # PostgreSQL
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/archetype
   SPRING_DATASOURCE_USERNAME=archetype
   SPRING_DATASOURCE_PASSWORD=archetype
   
   # MongoDB
   SPRING_DATA_MONGODB_URI=mongodb://archetype:archetype@localhost:27017/archetype?authSource=admin
   
   # RabbitMQ
   SPRING_RABBITMQ_HOST=localhost
   SPRING_RABBITMQ_PORT=5672
   SPRING_RABBITMQ_USERNAME=archetype
   SPRING_RABBITMQ_PASSWORD=archetype
   ```

4. **Run the application**:
   ```bash
   gradlew.bat bootRun --args="--spring.profiles.active=local"
   ```

### Stopping Services

To stop the services:
```bash
cd observability
docker-compose down
```

To stop and remove all data:
```bash
cd observability
docker-compose down -v
```

### Additional Services

The observability docker-compose includes other services you can optionally start:
- **Full Observability Stack**: `docker-compose up -d` (starts everything including Grafana, Loki, Tempo, Prometheus, Alloy)
- **Grafana Dashboard**: Access at http://localhost:3000 (user: `admin`, password: `admin`)
- **RabbitMQ Management**: Access at http://localhost:15672
- **Prometheus**: Access at http://localhost:9090

### Database Initialization

- **MongoDB**: Automatically initialized with the script at `observability/mongo-init.js`
- **PostgreSQL**: Uses Flyway migrations from `src/main/resources/db/migration/`
