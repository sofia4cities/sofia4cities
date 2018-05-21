package kafka.clients;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerExampe {

	public static void main(String[] args) {

		Properties config = new Properties();
		config.put(ProducerConfig.CLIENT_ID_CONFIG, "localhost");
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
		config.put("security.protocol", "SASL_PLAINTEXT");
		config.put("sasl.mechanism", "PLAIN");
		// config.put("sasl.jaas.config",
		// "org.apache.kafka.common.security.plain.PlainLoginModule required
		// username=\"admin\" password=\"admin-secret\";");

		config.put("sasl.jaas.config",
				"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"2936c949-c514-43d5-ba86-568bb6791fb5\" password=\"56686a5a0d7e497d9cafbbbd4b2563ee\";");

		// Use Kafka Avro Deserializer.

		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		KafkaProducer<String, String> producer = new KafkaProducer<>(config);

		for (int i = 0; i < 1000; i++) {
			String pepe = "{\"groupId\" : \"GROUP\",\"imageList\" : [],\"code\" : \"5d7c232c-cc57-4a05-bbe1-236f9d9a3891\",\"name\" : \"name1\",\"category\" : \"CATEGORY\", \"mainImage\" : \"IMAGE\"}";
			Future<RecordMetadata> metadata = producer
					.send(new ProducerRecord<String, String>("ontology_product", pepe));
			System.out.println(i);

		}
		producer.flush();
		producer.close();
	}
}
