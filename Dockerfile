FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy project files
COPY . .

# Build and run in one stage
RUN ./mvnw clean package -DskipTests

EXPOSE 9009

ENTRYPOINT ["java", "-jar", "target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar"]