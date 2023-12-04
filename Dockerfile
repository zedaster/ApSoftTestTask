# Run "mvn package" before creating this image
# Use "docker build -t ap-soft-test ." to build the image
# This image doesn't need any dependent containers

FROM eclipse-temurin:17-jre
EXPOSE 8080
RUN mkdir /app
COPY /target/*.jar /app/spring-boot-application.jar

ENTRYPOINT ["java", "-jar", "/app/spring-boot-application.jar"]