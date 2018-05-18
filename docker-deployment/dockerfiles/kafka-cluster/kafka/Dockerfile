FROM confluentinc/cp-kafka:4.1.0

# Metadata
LABEL module.maintainer "plataformasofia2@indra.es" \
      module.name="kafkabroker"

# Copy kafka plugin and config
COPY kafka-server-start /usr/bin/kafka-server-start
COPY server.properties /etc/kafka/server.properties
COPY kafka_server.jaas /etc/kafka/kafka_server.jaas
COPY authentication-0.0.1-SNAPSHOT.jar /usr/share/java/kafka/authentication-0.0.1-SNAPSHOT.jar
      
EXPOSE 9093 9092   

ENTRYPOINT ["kafka-server-start", "/etc/kafka/server.properties"]
