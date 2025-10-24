# ==============================
# BUILD STAGE
# ==============================
FROM maven:3.9.9-eclipse-temurin-25 AS builder

WORKDIR /app

# Copy POM first for dependency caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# ==============================
# RUNTIME STAGE
# ==============================
FROM eclipse-temurin:25-jre-jammy

RUN groupadd -r springuser && useradd -r -g springuser -u 1001 springuser
USER springuser

WORKDIR /app

# Copy the built JAR
COPY --from=builder /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9009

ENTRYPOINT ["java", "-jar", "app.jar"]