# =========================
# Stage 1: Build the JAR
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Clean and package the application
RUN mvn clean package -DskipTests

# Verify the JAR exists (optional)
RUN ls -l target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar

# =========================
# Stage 2: Run the JAR
# =========================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
