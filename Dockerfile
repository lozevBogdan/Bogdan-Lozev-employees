FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
EXPOSE 8080
COPY --from=build /app/target/bogdan-lozev-employees.jar /bogdan-lozev-employees.jar
ENTRYPOINT ["java", "-jar", "/bogdan-lozev-employees.jar"]
