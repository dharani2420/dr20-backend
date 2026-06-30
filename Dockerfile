# Stage 1: Build JAR with Maven + Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run JAR (Render sets PORT; app uses server.port=${PORT:8081})
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/doctor-patient-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]
