# ===============================
# Stage 1: Build the Spring Boot application
# ===============================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy POM and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Debug: Check what's in the JAR
RUN jar tf /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar | grep ChurchSoftBackendApplication

# ===============================
# Stage 2: Run the built JAR
# ===============================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the specific JAR file
COPY --from=build /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]