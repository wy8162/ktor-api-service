FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11.0.16-jre-slim
EXPOSE 8080:8080
RUN mkdir /app
RUN mkdir /opt/opentelemetry
COPY /home/gradle/opentelemetry/* /opt/opentelemetry
COPY /home/gradle/src/main/resources/opentelemetry.properties /opt/opentelemetry
COPY /home/gradle/src/build/libs/app.jar /app/app.jar
CMD java -javaagent:/opt/opentelemetry/opentelemetry-javaagent.jar \
  -D-Dotel.javaagent.configuration-file=/opt/opentelemetry/opentelemetry.properties \
  -jar /app/app.jar