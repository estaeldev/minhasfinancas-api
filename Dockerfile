FROM openjdk:17-alpine
EXPOSE 9000
WORKDIR /app
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} .
ENV DB_HOST=jdbc:postgresql://minhasfinancas_postgres:5432/minhasfinancas
ENV DB_USERNAME=root
ENV DB_PASSWORD=root
CMD ["java", "-jar", "/app/minhasfinancas1.0.0.jar"]
