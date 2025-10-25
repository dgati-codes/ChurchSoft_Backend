# ==============================
# BUILD STAGE
# ==============================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies first (better caching)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build and create executable JAR
RUN mvn clean package -DskipTests

# Debug: Verify the JAR structure
RUN ls -la target/ && \
    echo "=== JAR contents ===" && \
    jar tf target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar | grep -E "(BOOT-INF|ChurchSoftBackendApplication)" | head -20

# ==============================
# RUNTIME STAGE
# ==============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install jar command for debugging (optional)
RUN apk add --no-cache openjdk17-jre-headless

# Copy the built JAR from builder stage
COPY --from=builder /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9009

ENTRYPOINT ["java", "-jar", "app.jar"]