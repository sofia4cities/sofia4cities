FROM openjdk:8-jdk-alpine

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="iotbroker"	

VOLUME /tmp

ADD *-exec.jar app.jar

EXPOSE 8081 1883

ENV JAVA_OPTS="" \
    SERVER_NAME=localhost

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]