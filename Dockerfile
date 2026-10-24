FROM maven:3.10-amazoncorretto-25-alpine AS builder
WORKDIR /builder

COPY pom.xml ./
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn -T 2C clean package --errors
RUN java -Djarmode=tools -jar target/*.jar extract --layers --destination extracted

FROM eclipse-temurin:25.0.4.1_1-jre-noble

WORKDIR /application

COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
COPY --from=builder /builder/target/*.jar application.jar

ENTRYPOINT ["java","-XX:+UseZGC","-XX:+UseCompactObjectHeaders","-XX:+UseStringDeduplication","-XX:+UseCompressedOops","-XX:+UseCompressedClassPointers","-jar","/application/application.jar"]
