FROM openjdk:21
EXPOSE 8080
ADD target/bogdan-lozev-employees.jar bogdan-lozev-employees.jar
ENTRYPOINT ["java", "-jar", "/bogdan-lozev-employees.jar"]
