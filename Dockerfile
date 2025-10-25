# ==============================
# 1️⃣ BUILD STAGE
# ==============================
FROM openjdk:17-jdk-alpine AS builder

RUN apk add --no-cache curl tar bash procps

# Install Maven
ARG MAVEN_VERSION=3.9.6
ARG BASE_URL=https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
 && curl -fsSL -o /tmp/maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
 && tar -xzf /tmp/maven.tar.gz -C /usr/share/maven --strip-components=1 \
 && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
WORKDIR /app

# Copy pom.xml and pre-download dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy all source files
COPY src ./src

# Debug: confirm files exist
RUN echo "==== Checking copied sources ====" && ls -R src/main/java | head -n 30

# Build Spring Boot JAR
RUN mvn clean package -DskipTests

# ==============================
# 2️⃣ RUNTIME STAGE
# ==============================
FROM openjdk:17-jdk-alpine
WORKDIR /app

COPY --from=builder /app/target/ChurchSoft_Backend-0.0.1-SNAPSHOT.jar .

EXPOSE 9009
ENTRYPOINT ["java", "-jar", "ChurchSoft_Backend-0.0.1-SNAPSHOT.jar"]
