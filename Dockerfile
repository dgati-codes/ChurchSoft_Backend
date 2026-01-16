
# Stage 1: Build the Spring Boot application
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy everything
COPY . .

# Create resources directory if it doesn't exist (for Render build)
RUN mkdir -p src/main/resources

# First compile the source code explicitly
RUN mvn compile -DskipTests

# Then build the package
RUN mvn package -DskipTests

# Verify the JAR contains our classes
RUN jar tf /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar | head -20

# Stage 2: Run the built JAR
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
