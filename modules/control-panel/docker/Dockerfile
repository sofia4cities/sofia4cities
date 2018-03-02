FROM openjdk:8-jdk-alpine

LABEL maintainer "plataformasofia2@indra.es"

VOLUME /tmp

ADD *-exec.jar app.jar

EXPOSE 18080

ENV JAVA_OPTS=""

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
#ENTRYPOINT ["java","$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]