FROM openjdk:17-alpine
EXPOSE 9000
WORKDIR /app
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} ./app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
