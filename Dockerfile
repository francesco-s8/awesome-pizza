FROM maven:3.9.16-amazoncorretto-25-alpine AS builder
COPY . /app
WORKDIR /app
RUN --mount=type=cache,target=/root/.m2 mvn -f /app/pom.xml clean package -DskipTests --errors

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/awesome-pizza-1.0.0.jar /app/awesome-pizza.jar
ENTRYPOINT ["java","-jar","/app/awesome-pizza.jar","-XX:+UseZGC","-XX:+UseCompactObjectHeaders","-XX:+UseStringDeduplication","-XX:+UseCompressedOops","-XX:+UseCompressedClassPointers"]
