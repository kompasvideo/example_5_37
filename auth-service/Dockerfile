FROM amazoncorretto:17
COPY target/*.jar auth.jar
ENTRYPOINT ["java", "-jar", "/auth.jar"]