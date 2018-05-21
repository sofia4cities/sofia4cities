FROM mongo:latest

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="realtimedb"	

# MongoDB environment
ENV MONGO_INITDB_DATABASE=sofia2_s4c

RUN chmod -R 777 /data/db

EXPOSE 27017

USER mongodb

# Init data for testing purposes
COPY initdb.js /docker-entrypoint-initdb.d/initdb.js