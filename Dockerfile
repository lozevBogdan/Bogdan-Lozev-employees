FROM openjdk:17
EXPOSE 8080
ADD target/Bogdan-Lozev-employees.jar Bogdan-Lozev-employees.jar
ENTRYPOINT ["java", "-jar", "/Bogdan-Lozev-employees.jar"]