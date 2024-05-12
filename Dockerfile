FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install

FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=build /app/target/whateat-1.0.0.jar ./whateat.jar
EXPOSE 8080
CMD ["java", "-jar", "whateat.jar"]
