FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar another-adventure.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/another-adventure.jar"]
