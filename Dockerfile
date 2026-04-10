# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Copy pom.xml and download dependencies (for faster builds)
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
LABEL authors="Lalit"
WORKDIR /app

# 3. Copy the jar from the build stage
# The name comes from your <artifactId> and <version> in pom.xml
COPY --from=build /app/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# API Gateways usually run on port 8080 or 9090.
# Adjust this to match your application.properties
EXPOSE 8080

# 4. Run the application with optimized memory settings for containers
ENTRYPOINT ["java", "-jar", "app.jar"]