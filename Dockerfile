
# Stage 1: Build the application
FROM openjdk:17-jdk-alpine AS builder

# Install required packages
RUN apk add --no-cache curl tar bash procps

# Maven version and user home directory
ARG MAVEN_VERSION=3.6.3
ARG USER_HOME_DIR="/root"
ARG BASE_URL=https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries

# Install Maven
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
 && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
 && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
 && rm -f /tmp/apache-maven.tar.gz \
 && cp -s /usr/share/maven/bin/mvn /usr/bin/mvn

# Set environment variables for Maven
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# Set work directory
WORKDIR /app

# Copy pom.xml and download dependencies (to cache this step)
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean install

# Stage 2: Create the final image
FROM openjdk:17-jdk-alpine

# Set work directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=builder /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar .

# Expose the application port
EXPOSE 9009

# Command to run the application
CMD ["java", "-jar", "ChurchSoft_Backend-0.0.1-SNAPSHOT.jar"]
