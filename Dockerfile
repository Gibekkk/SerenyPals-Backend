FROM maven:3-openjdk-17-slim AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-alpine

WORKDIR /app

COPY .env .
COPY --from=builder /app/target/restfulapi*.jar ./restfulapi.jar

CMD [ "java", "-jar", "restfulapi.jar" ]