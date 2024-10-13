# Build stage
FROM maven:3.8.6-openjdk-21 AS build
WORKDIR /app

# Copy the pom.xml and source code into the container
COPY pom.xml .
COPY src ./src

# Package the application using Maven (this creates the JAR file in the target directory)
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:21-jdk-slim
EXPOSE 8080

# Copy the JAR file from the build stage
COPY --from=build /app/target/bogdan-lozev-employees.jar /bogdan-lozev-employees.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/bogdan-lozev-employees.jar"]
