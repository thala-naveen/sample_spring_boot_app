# Sentinel AML Multi-Stage Dockerfile (Arm64 & Amd64 Compatible)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and maven wrapper
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Copy source code
COPY src ./src

# Package application into standalone executable JAR
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime Environment
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built JAR artifact from builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
