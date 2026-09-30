# Build stage: compile the Spring Boot application with Maven and Java 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy Maven wrapper and project metadata first to leverage dependency caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

# Copy the application source and package it into a runnable JAR
COPY src ./src
RUN ./mvnw -q -DskipTests package

# Runtime stage: use a smaller Java 21 runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /build/target/*.jar app.jar

# Segurança
RUN addgroup -g 1000 app && adduser -D -u 1000 -G app app
USER app

# Expose the application port
EXPOSE 8080

# Monitoramento
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --spider http://localhost:8080/actuator/health || exit 1

# Default profile for container runs
ENV SPRING_PROFILES_ACTIVE=prod

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
