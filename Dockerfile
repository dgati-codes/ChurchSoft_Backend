FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

# Build without Spring Boot plugin interference
RUN mvn clean package -DskipTests -Dspring-boot.repackage.skip=true

# Check the JAR structure
RUN jar tf /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Use explicit classpath execution
ENTRYPOINT ["java", "-cp", "app.jar", "org.springframework.boot.loader.JarLauncher"]