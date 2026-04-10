# Stage 1: Build stage (Environment with Maven + JDK)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Copy pom.xml and download dependencies
# This layer is cached unless you change your dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Copy source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage (Lightweight Environment with JRE only)
FROM eclipse-temurin:21-jre-alpine
LABEL authors="Lalit"
WORKDIR /app

# 3. Copy the jar from the 'build' stage
# This ensures we don't carry the 600MB of Maven/Source code to production
COPY --from=build /app/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Standard port for API Gateways
EXPOSE 8080

# 4. Start the Gateway
ENTRYPOINT ["java", "-jar", "app.jar"]