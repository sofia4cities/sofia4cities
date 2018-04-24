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

RUN mkdir -p /opt/nodeRed && \
	mkdir -p /tmp/logs/flowEngine && \
    mkdir -p /var/log/logs-s4c && \
	mkdir ./target	

ADD *-exec.jar app.jar
ADD Flow-Engine-Manager /opt/nodeRed/Flow-Engine-Manager

# create sofia user/group
RUN addgroup -S sofia -g 433 && adduser -u 431 -S -g sofia -h /usr/local -s /sbin/nologin sofia 

RUN chown -R sofia:sofia /usr/local && \
    chown -R sofia:sofia /var/log/logs-s4c && \
    chown -R sofia:sofia ./target && \
    chown -R sofia:sofia /opt/nodeRed && \
    chown sofia:sofia app.jar
    
USER sofia

EXPOSE 10000 8082 5050

COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]