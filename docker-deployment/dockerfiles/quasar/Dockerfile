FROM frolvlad/alpine-oraclejdk8:slim

# Metadata
LABEL module.maintainer "plataformasofia2@indra.es" \
      module.name="quasar"

# Create quasar directory
RUN mkdir -p /usr/local/onesait/quasar

# Create the tomcat user and group
RUN addgroup -S quasar -g 433 && adduser -u 431 -S -g quasar -h /usr/local/onesait -s /sbin/nologin quasar

# Copy quasar plugin and config
COPY quasar-web-assembly-14.2.6.jar /usr/local/onesait/quasar
COPY config.json /usr/local/onesait/quasar
COPY docker-entrypoint.sh /usr/local/bin/

RUN chown -R quasar:quasar /usr/local/onesait/quasar && \
    chmod -R 777 /usr/local/onesait/quasar && \
    chmod +x /usr/local/bin/docker-entrypoint.sh
    
WORKDIR /usr/local/onesait/quasar

ENV JAVA_OPTS="" \
	REALTIMEDB=realtimedb
	
EXPOSE 10800

USER quasar
ENTRYPOINT ["docker-entrypoint.sh"]