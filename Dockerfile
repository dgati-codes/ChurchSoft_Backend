# ==============================
# 1️⃣ BUILD STAGE
# ==============================
FROM eclipse-temurin:25-jdk-jammy AS builder

WORKDIR /app

# Copy Maven files for dependency caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Ensure mvnw is executable and download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests


# ==============================
# 2️⃣ RUNTIME STAGE
# ==============================
FROM eclipse-temurin:25-jre-jammy AS runtime

# Create a non-root user
RUN useradd -r -u 1001 springuser
USER springuser

WORKDIR /app

# Copy the jar
COPY --from=builder /app/target/*.jar app.jar

# Expose port for Render
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
