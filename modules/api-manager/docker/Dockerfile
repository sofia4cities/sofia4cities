FROM openjdk:8-jdk-alpine

LABEL maintainer "plataformasofia2@indra.es"

VOLUME /tmp

ARG JAR_FILE

ADD *-exec.jar app.jar

EXPOSE 19090

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]