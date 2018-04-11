FROM docker.elastic.co/elasticsearch/elasticsearch:6.2.3

# Metadata
LABEL module.maintainer "plataformasofia2@indra.es" \
      module.name="elasticdb"

# Elastic Search custom configuration
COPY elasticsearch.yml /config

# Elastic Search SQL plugin
RUN ./bin/elasticsearch-plugin install https://github.com/NLPchina/elasticsearch-sql/releases/download/6.2.3.0/elasticsearch-sql-6.2.3.0.zip
    
EXPOSE 9200 9300 

# Elastic Search data directory
VOLUME /usr/share/elasticsearch/data