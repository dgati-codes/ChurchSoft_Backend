==============================
BUILD STAGE
==============================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
Copy pom.xml first for better Docker layer caching
COPY pom.xml .
Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B
Copy source code
COPY src ./src
Build the application (repacks into executable fat JAR)
RUN mvn clean package -DskipTests
==============================
RUNTIME STAGE
==============================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
Copy the built JAR from builder stage
COPY --from=builder /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar
Optional: Set JVM options for production (tune heap, etc.)
ENV JAVA_OPTS="-Xms256m -Xmx512m"
EXPOSE 9009
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]