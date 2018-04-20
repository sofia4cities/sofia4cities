FROM openjdk:8-jdk-alpine

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="monitoringui"

VOLUME /tmp

ADD *-exec.jar app.jar

EXPOSE 19000

ENV JAVA_OPTS="" \
    SERVER_NAME=localhost
    
# Copy certs
COPY sofia4cities.crt /usr/local/
COPY server.key /usr/local/

# Import certificates
RUN keytool -noprompt -import -v -trustcacerts -alias rancherserver -file /usr/local/sofia4cities.crt \
	-keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -keypass changeit -storepass changeit
    
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]