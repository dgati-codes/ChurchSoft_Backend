FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

# Debug: Show project structure
RUN echo "=== Project Structure ===" && find . -name "*.java" | head -20

# Build
RUN mvn clean package -DskipTests

# Debug: Check JAR contents
RUN echo "=== JAR Contents ===" && \
    jar tf /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar | grep -i churchsoft || echo "No ChurchSoft classes found!"

# Debug: Check BOOT-INF contents
RUN echo "=== BOOT-INF Classes ===" && \
    jar tf /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar | grep BOOT-INF/classes | head -10

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]