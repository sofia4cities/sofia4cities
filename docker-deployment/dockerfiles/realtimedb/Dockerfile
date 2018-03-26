FROM mongo:3.4

# Metadata
LABEL module.maintainer="plataformasofia2@indra.es" \
	  module.name="realtimedb"	

# MongoDB environment
ENV MONGO_INITDB_DATABASE=sofia2_s4c

EXPOSE 27017

# Init data for testing purposes
COPY initdb.js /docker-entrypoint-initdb.d/initdb.js