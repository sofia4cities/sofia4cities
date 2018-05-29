FROM confluentinc/cp-kafka:4.1.0

# Metadata
LABEL module.maintainer "plataformasofia2@indra.es" \
      module.name="kafkabroker"

# Default hostname
ENV SERVER_NAME=localhost

# Copy kafka plugin and config
COPY kafka-server-start /usr/bin/kafka-server-start
# COPY jolokia-jvm-1.5.0-agent.jar /usr/bin/jolokia-jvm-1.5.0-agent.jar
COPY server.properties /etc/kafka/server.properties
COPY kafka_server.jaas /etc/kafka/kafka_server.jaas
COPY sofia2-kafka-login*.jar /usr/share/java/kafka/sofia2-kafka-login.jar
      
EXPOSE 9093 9092 9999 7778 

ENTRYPOINT ["kafka-server-start", "/etc/kafka/server.properties"]