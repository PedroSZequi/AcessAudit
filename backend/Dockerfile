# syntax=docker/dockerfile:1.7
# =========================================================================
# Stage 1 — build do JAR com Maven
# =========================================================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Cache de dependências: copia só o pom primeiro
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Código-fonte
COPY src ./src

# Build (testes rodam no CI, não no Docker)
RUN ./mvnw -B -DskipTests package

# =========================================================================
# Stage 2 — runtime enxuto com JRE Alpine
# =========================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuário não-root
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/target/backend-*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

# Probe simples consumida pelo healthcheck do compose / load balancer
HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
