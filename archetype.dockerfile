FROM eclipse-temurin:21-jre-alpine

RUN mkdir /app

RUN apk add --no-cache gcompat curl bash

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup
RUN chown -R appuser:appgroup /app

WORKDIR /app

COPY build/agents/aspectjweaver.jar aspectjweaver.jar
COPY build/agents/opentelemetry.jar opentelemetry.jar
COPY build/libs/archetype.jar archetype.jar

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-javaagent:/app/aspectjweaver.jar", "-javaagent:/app/opentelemetry.jar", "-jar", "/app/archetype.jar"]