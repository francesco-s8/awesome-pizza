FROM maven:3.10-amazoncorretto-25-alpine AS builder
WORKDIR /builder

COPY pom.xml ./
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn -T 2C clean package --errors
RUN java -Djarmode=tools -jar target/*.jar extract --layers --destination extracted

FROM eclipse-temurin:25.0.4.1_1-jre-noble

RUN addgroup --system app && adduser --system --ingroup app --home /home/app app
RUN mkdir -p /application/aot-cache && chown -R app:app /application

USER app

WORKDIR /application

COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
COPY --chmod=755 docker/docker-entrypoint.sh /usr/local/bin/docker-entrypoint

ENTRYPOINT ["/usr/local/bin/docker-entrypoint"]
