# ==============================
# 1️⃣ BUILD STAGE
# ==============================
FROM eclipse-temurin:25-jdk-jammy AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (for caching dependencies)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make wrapper executable and pre-download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Now copy the source code
COPY src ./src

# Build the application JAR (skip tests for speed)
RUN ./mvnw clean package -DskipTests


# ==============================
# 2️⃣ RUNTIME STAGE
# ==============================
FROM eclipse-temurin:25-jre-jammy AS runtime

# Create a non-root user for security
RUN useradd -r -u 1001 springuser
USER springuser

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port for Render
EXPOSE 9009

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
