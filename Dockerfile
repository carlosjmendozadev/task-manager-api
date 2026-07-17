# Build stage: compile the Spring Boot application with Maven and Java 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace/app

# Copy Maven wrapper and project metadata first to leverage dependency caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

# Copy the application source and package it into a runnable JAR
COPY src ./src
RUN ./mvnw -q -DskipTests package

# Runtime stage: use a smaller Java 21 runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /workspace/app/target/api-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Default profile for container runs
ENV SPRING_PROFILES_ACTIVE=prod

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
