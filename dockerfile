# Stage 1: Build jar inside Docker
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2: Run jar
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/target/banking-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]