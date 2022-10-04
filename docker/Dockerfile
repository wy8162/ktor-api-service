FROM openjdk:11.0.16-jre-slim
EXPOSE 8080:8080
RUN mkdir /app
RUN mkdir /opt/opentelemetry
COPY ./opentelemetry/* /opt/opentelemetry
COPY ./build/libs/app.jar /app/app.jar
CMD java -javaagent:/opt/opentelemetry/opentelemetry-javaagent.jar \
  -D-Dotel.javaagent.configuration-file=/opt/opentelemetry/opentelemetry.properties \
  -jar /app/app.jar