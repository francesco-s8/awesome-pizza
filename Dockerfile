FROM maven:3.9.11-amazoncorretto-25-alpine AS builder
COPY . /app
WORKDIR /app
RUN --mount=type=cache,target=/root/.m2 mvn -f /app/pom.xml clean package
ENTRYPOINT ["java","-jar","/app/target/awesomw-pizza-1.0.0.jar","-XX:+UseG1GC"]
