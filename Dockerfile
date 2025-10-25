# ==============================
# BUILD STAGE
# ==============================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy ALL project files first
COPY . .

# Debug: List files to verify everything is copied
RUN echo "=== Project structure ===" && \
    ls -la && \
    echo "=== Source files ===" && \
    find src -name "*.java" | head -20

# Build the application
RUN mvn clean package -DskipTests

# Debug: Check the actual JAR contents (without grep failures)
RUN echo "=== Full JAR contents ===" && \
    jar tf /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar | head -50

# ==============================
# RUNTIME STAGE
# ==============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9009

ENTRYPOINT ["java", "-jar", "app.jar"]