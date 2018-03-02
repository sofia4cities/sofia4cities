FROM openjdk:8-jdk-alpine

LABEL maintainer "plataformasofia2@indra.es"

VOLUME /tmp

ADD *-exec.jar app.jar

EXPOSE 19090

ENV JAVA_OPTS=""

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]