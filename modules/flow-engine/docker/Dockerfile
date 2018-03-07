# Step1 - nodejs binaries
FROM node:4.6.2-alpine AS nodebase

# Step2 - openjdk base image
FROM openjdk:8-jdk-alpine

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="flowengine"	

VOLUME /tmp

COPY --from=nodebase /usr/local/bin/node /bin

RUN mkdir -p /opt/flowengine/ &&\
	mkdir -p /tmp/logs/flowEngine

ADD *-exec.jar app.jar
ADD Flow-Engine-Manager /opt/flowengine/Flow-Engine-Manager

EXPOSE 8082

ENV JAVA_OPTS=""

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]