FROM openjdk:8-jdk-alpine

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="configinit"	

VOLUME /tmp

ADD *-exec.jar app.jar

# web projects folder & logs folder
RUN mkdir -p /usr/local/webprojects && \
	mkdir -p /var/log/logs-s4c && \
	mkdir ./target

# create sofia user/group
RUN addgroup -S sofia -g 433 && adduser -u 431 -S -g sofia -h /usr/local -s /sbin/nologin sofia 

RUN chown -R sofia:sofia /usr/local && \
    chown -R sofia:sofia /var/log/logs-s4c && \
    chown -R sofia:sofia ./target && \    
    chown sofia:sofia app.jar && \
    chmod -R 777 ./target
    
USER sofia

EXPOSE 21000

ENV JAVA_OPTS="" \
    SERVER_NAME=localhost

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]