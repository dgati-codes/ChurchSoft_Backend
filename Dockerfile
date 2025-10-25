# ==============================
# BUILD STAGE
# ==============================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy project files
COPY . .

# Debug: Show project structure
RUN echo "=== Project Structure ===" && \
    find src/main/java -name "*.java" | grep ChurchSoftBackendApplication

# Clean and build
RUN mvn clean compile -DskipTests

# Verify compilation worked
RUN echo "=== Compiled Classes ===" && \
    find target/classes -name "ChurchSoftBackendApplication.class"

# Create a proper executable JAR without Spring Boot repackaging
RUN mvn package -DskipTests -Dspring-boot.repackage.skip=true

# Create a simple executable JAR with proper manifest
RUN jar tf target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar > /tmp/jar_contents.txt && \
    echo "=== JAR Contents ===" && \
    cat /tmp/jar_contents.txt | head -20

# ==============================
# RUNTIME STAGE
# ==============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9009

# Use the exact classpath format that works
ENTRYPOINT ["java", "-cp", "app.jar", "com.churchsoft.ChurchSoftBackendApplication"]