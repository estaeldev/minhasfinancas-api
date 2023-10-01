FROM openjdk:17-alpine
EXPOSE 9000
WORKDIR /app
COPY target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
