# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

# Build a regular JAR without Spring Boot repackaging
RUN mvn clean compile assembly:single -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT-jar-with-dependencies.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-cp", "app.jar", "com.churchsoft.ChurchSoftBackendApplication"]