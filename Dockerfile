# ==============================
# BUILD STAGE
# ==============================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy ALL project files
COPY . .

# Debug: Show complete project structure
RUN echo "=== Complete project structure ===" && \
    find . -type f -name "*.java" | head -30 && \
    echo "=== Checking main class exists ===" && \
    find . -name "ChurchSoftBackendApplication.java" && \
    echo "=== Maven version ===" && \
    mvn --version

# Build with verbose output
RUN mvn clean compile -DskipTests

# Check if classes were compiled
RUN echo "=== Checking compiled classes ===" && \
    find target -name "*.class" | head -20

# Now build the package
RUN mvn package -DskipTests

# Comprehensive JAR analysis
RUN echo "=== JAR detailed analysis ===" && \
    echo "JAR file size:" && \
    ls -lh /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar && \
    echo "=== Full JAR contents (first 100 lines) ===" && \
    jar tf /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar | head -100 && \
    echo "=== Checking for BOOT-INF ===" && \
    jar tf /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar | grep BOOT-INF || echo "BOOT-INF not found!" && \
    echo "=== Checking for main class ===" && \
    jar tf /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar | grep ChurchSoftBackendApplication || echo "Main class not found!"

# ==============================
# RUNTIME STAGE
# ==============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

# Verify JAR in runtime stage
RUN jar tf app.jar | head -10

EXPOSE 9009

ENTRYPOINT ["java", "-jar", "app.jar"]