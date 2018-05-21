FROM confluentinc/cp-zookeeper:4.1.0

# Metadata
LABEL module.maintainer "plataformasofia2@indra.es" \
      module.name="zookeeper"
      
# Copy zookeeper plugin and config
COPY zookeeper-server-start /usr/bin/zookeeper-server-start
COPY zookeeper.properties /etc/kafka/zookeeper.properties
COPY zookeeper_jaas.conf /etc/kafka/zookeeper_jaas.conf


EXPOSE 2181      

ENTRYPOINT ["zookeeper-server-start", "/etc/kafka/zookeeper.properties"]
