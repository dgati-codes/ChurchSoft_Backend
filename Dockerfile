# Stage 1: Build the Spring Boot application
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only the files needed for dependency resolution first
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Install dependencies separately (Docker layer caching)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the fat/executable JAR in one step
RUN mvn clean package -DskipTests

# Stage 2: Run the built JAR
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
