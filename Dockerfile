# Build stage
FROM maven:3.9.9-openjdk-21 AS builder

WORKDIR /app

# Copy POM and download dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:21-jre-slim

WORKDIR /app
COPY --from=builder /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9009
ENTRYPOINT ["java", "-jar", "app.jar"]