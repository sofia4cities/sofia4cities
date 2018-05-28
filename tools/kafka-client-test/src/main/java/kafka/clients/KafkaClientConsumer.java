package kafka.clients;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaClientConsumer {

	public static void main(String[] args) {

		Boolean hasEnded;

		Properties config = new Properties();
		config.put("client.id", "localhost");
		config.put("group.id", "test-consumer-group2");
		config.put("bootstrap.servers", "localhost:9093");
		config.put("security.protocol", "SASL_PLAINTEXT");
		config.put("sasl.mechanism", "PLAIN");
		config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config.put("sasl.jaas.config",
				"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"2936c949-c514-43d5-ba86-568bb6791fb5\" password=\"56686a5a0d7e497d9cafbbbd4b2563ee\";");

		KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(config);
		List<String> topics = new ArrayList<>();
		topics.add("ontology_product");
		kafkaConsumer.subscribe(topics);
		hasEnded = false;
		Long ini = System.currentTimeMillis();
		int readRecords = 0;
		while (!hasEnded) {
			ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.MAX_VALUE);
			System.out.println(records); // application-specific processing
			consumerLogic(records);
			readRecords += records.count();
			hasEnded = checkEnd(ini, readRecords);
			kafkaConsumer.commitSync(); // commits the offsets modified by
										// "pool"
		}

		kafkaConsumer.close();
	}

	private static Boolean checkEnd(Long initialMillis, int readRecords) {
		return ((System.currentTimeMillis() - initialMillis) > 20000) || readRecords > 10;
	}

	private static void consumerLogic(ConsumerRecords<String, String> records) {

		Iterator<ConsumerRecord<String, String>> it = records.iterator();

		while (it.hasNext()) {
			ConsumerRecord<String, String> record = it.next();
			System.out.println(record.topic());
			System.out.println(record.partition());
			System.out.println(record.key());
			System.out.println(record.offset());
			System.out.println(record.value());
			System.out.println("++++++++++++++++++**");
		}
	}
}
