# Build dentro do container: não exige Java nem Maven na máquina. Os testes unitários rodam aqui.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q package

FROM eclipse-temurin:17-jre
COPY --from=build /app/target/oficinaflow-1.0.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
