# ===============================
# Stage 1: Build the Spring Boot application
# ===============================
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only pom.xml first to leverage Docker layer caching
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Now copy the source code
COPY src ./src

# Build the project (skip tests for faster build)
RUN mvn clean package -DskipTests

# ===============================
# Stage 2: Run the built JAR
# ===============================
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
