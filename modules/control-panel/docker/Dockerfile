FROM maven:3.5.3-jdk-8-alpine

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="controlpanel"	

VOLUME /tmp

ADD *-exec.jar app.jar

EXPOSE 18080

ENV JAVA_OPTS="" \
    SERVER_NAME=localhost

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]