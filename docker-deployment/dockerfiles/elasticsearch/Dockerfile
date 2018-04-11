FROM docker.elastic.co/elasticsearch/elasticsearch:6.2.3

# Metadata
LABEL module.maintainer "plataformasofia2@indra.es" \
      module.name="elasticdb"

ENV bootstrap.memory_lock=true \
	ES_JAVA_OPTS="-Xms512m -Xmx512m"

# Elastic Search custom configuration
COPY --chown=elasticsearch:elasticsearch elasticsearch.yml /usr/share/elasticsearch/config/

# Elastic Search SQL plugin
RUN ./bin/elasticsearch-plugin install https://github.com/NLPchina/elasticsearch-sql/releases/download/6.2.3.0/elasticsearch-sql-6.2.3.0.zip
    
EXPOSE 9200 9300 

# Elastic Search data directory
VOLUME /usr/share/elasticsearch/data