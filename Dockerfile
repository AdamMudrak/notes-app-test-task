# --- build stage ---
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Cache dependencies
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -B -DskipTests package

# --- run stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run as non-root
RUN useradd -r -u 1001 appuser
COPY --from=builder /build/target/*.jar app.jar
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
