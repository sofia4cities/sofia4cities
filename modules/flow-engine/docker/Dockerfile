# Step1 - nodejs binaries
FROM node:4.6.2-alpine AS nodebase

# Step2 - openjdk base image
FROM openjdk:8-jdk-alpine

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="flowengine"	

ENV JAVA_OPTS="" \
	SERVERNAME=localhost
	
VOLUME /tmp

COPY --from=nodebase /usr/local/bin/node /bin

RUN mkdir -p /opt/nodeRed &&\
	mkdir -p /tmp/logs/flowEngine

ADD *-exec.jar app.jar
ADD Flow-Engine-Manager /opt/nodeRed/Flow-Engine-Manager

EXPOSE 10000 8082

COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]

#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]