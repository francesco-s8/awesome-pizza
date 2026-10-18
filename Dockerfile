FROM maven:3.10-amazoncorretto-25-alpine AS builder
COPY . /app
WORKDIR /app
RUN --mount=type=cache,target=/root/.m2 mvn -f /app/pom.xml -T 2C clean package --errors

FROM eclipse-temurin:25.0.4.1_1-jre-noble
WORKDIR /app
COPY --from=builder /app/target/awesome-pizza-1.0.0.jar /app/awesome-pizza.jar
ENTRYPOINT ["java","-jar","/app/awesome-pizza.jar","-Dspring.aot.enabled=true","-XX:+UseZGC","-XX:+UseCompactObjectHeaders","-XX:+UseStringDeduplication","-XX:+UseCompressedOops","-XX:+UseCompressedClassPointers"]
