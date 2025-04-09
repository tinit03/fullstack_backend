# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn -f pom.xml package -DskipTests

# Runtime Stage
FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY src/main/resources/*.yml /app/config/
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
