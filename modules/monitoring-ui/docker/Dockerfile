FROM openjdk:8-jdk-alpine

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="monitoringui"

VOLUME /tmp

ADD *-exec.jar app.jar

# logs folder
RUN mkdir -p /var/log/logs-s4c && \
	mkdir ./target
	
# create sofia user/group
RUN addgroup -S sofia -g 433 && adduser -u 431 -S -g sofia -h /usr/local -s /sbin/nologin sofia 

RUN chown -R sofia:sofia /usr/local && \
    chown -R sofia:sofia /var/log/logs-s4c && \
    chown -R sofia:sofia ./ && \    
    chown sofia:sofia app.jar

# Copy certs
COPY sofia4cities.crt /usr/local/
COPY server.key /usr/local/

# Import certificates
RUN keytool -noprompt -import -v -trustcacerts -alias rancherserver -file /usr/local/sofia4cities.crt \
	-keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -keypass changeit -storepass changeit
	  
USER sofia

EXPOSE 18100

ENV JAVA_OPTS="" \
    SERVER_NAME=localhost
    
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]