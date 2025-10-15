FROM gradle:7.2.0-jdk17 AS builder
WORKDIR /app

ENV GRADLE_OPTS="-Dorg.gradle.vfs.watch=false"

COPY build.gradle settings.gradle ./
COPY gradle gradle

RUN gradle dependencies --no-daemon || true

COPY . .

RUN gradle clean jar --no-daemon

FROM openjdk:17-jdk
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]