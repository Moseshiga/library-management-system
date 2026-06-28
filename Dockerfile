FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY library-management/pom.xml .
COPY library-management/.mvn .mvn
COPY library-management/mvnw .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY library-management/src ./src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]