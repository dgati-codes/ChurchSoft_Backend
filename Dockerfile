# ===============================
# Stage 1: Build the Spring Boot application
# ===============================
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the entire project structure
COPY . .

# Build the project (skip tests for faster build)
RUN mvn clean package -DskipTests

# ===============================
# Stage 2: Run the built JAR
# ===============================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]