FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache fontconfig freetype ttf-dejavu && fc-cache -f

RUN addgroup -S ironforge && adduser -S ironforge -G ironforge

COPY --from=builder /build/target/*.jar app.jar
RUN chown ironforge:ironforge app.jar

USER ironforge

ENV JAVA_OPTS="-Djava.awt.headless=true -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]