# ==============================
# 1️⃣ BUILD STAGE
# ==============================
FROM openjdk:17-jdk-alpine AS builder

# Install dependencies for Maven + general utilities
RUN apk add --no-cache curl tar bash procps

# Define Maven version and download URL
ARG MAVEN_VERSION=3.9.6
ARG USER_HOME_DIR="/root"
ARG BASE_URL=https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries

# Install Maven manually
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
 && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
 && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
 && rm -f /tmp/apache-maven.tar.gz \
 && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# Set Maven environment
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# Work directory for the build
WORKDIR /app

# Copy only the pom.xml first (to leverage Docker cache for dependencies)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# ✅ Optional debug: confirm source files copied
RUN echo "==== Source files in container ====" && ls -R src/main/java | head -n 50

# Build the Spring Boot JAR (skip tests to speed up build)
RUN mvn clean package -DskipTests

# ==============================
# 2️⃣ RUNTIME STAGE
# ==============================
FROM openjdk:17-jdk-alpine

# Working directory in the runtime image
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar .

# Expose your app port
EXPOSE 9009

# Command to run the app
ENTRYPOINT ["java", "-jar", "ChurchSoft_Backend-0.0.1-SNAPSHOT.jar"]
