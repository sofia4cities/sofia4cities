FROM frolvlad/alpine-oraclejdk8:slim

# Metadata
LABEL module.maintainer "plataformasofia2@indra.es" \
      module.name="quasar"

# Create quasar directory
RUN mkdir -p /usr/local/Sofia2/quasar

# Copy quasar plugin and config
COPY quasar-web-assembly-14.2.6.jar /usr/local/Sofia2/quasar
COPY config.json /usr/local/Sofia2/quasar

WORKDIR /usr/local/Sofia2/quasar

ENV JAVA_OPTS="" \
	REALTIMEDB=realtimedb
	
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]