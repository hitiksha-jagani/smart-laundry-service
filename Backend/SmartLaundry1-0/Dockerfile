# syntax=docker/dockerfile:1

FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app
COPY Backend/SmartLaundry1-0/ ./SmartLaundry1-0/
WORKDIR /app/SmartLaundry1-0/
RUN ./mvnw clean package -DskipTests

# ---------------------
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=builder /app/SmartLaundry1-0/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]





# Use a JDK image
# FROM eclipse-temurin:21-jdk

# # Set working directory
# WORKDIR /app

# # Copy and build
# COPY ../../target/ SmartLaundry1-0-0.0.1-SNAPSHOT.jar

# # Package the Spring Boot app
# RUN chmod +x mvnw
# RUN ./mvnw clean package -DskipTests


# # Run the jar
# CMD ["java", "-jar", "app.jar"]
