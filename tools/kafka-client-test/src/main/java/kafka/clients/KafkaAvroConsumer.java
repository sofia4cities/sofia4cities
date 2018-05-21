package kafka.clients;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public class KafkaAvroConsumer {

	public static void main(String[] args) {

		Boolean hasEnded;

		Properties config = new Properties();
		config.put(ConsumerConfig.CLIENT_ID_CONFIG, "localhost");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-avroGroup");
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
		config.put("security.protocol", "SASL_PLAINTEXT");
		config.put("sasl.mechanism", "PLAIN");
		config.put("sasl.jaas.config",
				"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin-secret\";");

		// Use Kafka Avro Deserializer.
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
		// Use Specific Record or else you get Avro GenericRecord.
		config.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
		// Schema registry location.
		config.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

		KafkaConsumer<String, Object> kafkaConsumer = new KafkaConsumer<>(config);
		List<String> topics = new ArrayList<>();
		topics.add("testAvro");
		kafkaConsumer.subscribe(topics);
		hasEnded = false;
		Long ini = System.currentTimeMillis();
		int readRecords = 0;
		while (!hasEnded) {
			ConsumerRecords<String, Object> records = kafkaConsumer.poll(Long.MAX_VALUE);
			System.out.println(records);
			// application-specific processing
			kafkaConsumer.commitSync();
			consumerLogic(records);
			readRecords += records.count();
			hasEnded = checkEnd(ini, readRecords);
		}

		kafkaConsumer.close();
	}

	private static Boolean checkEnd(Long initialMillis, int readRecords) {
		return ((System.currentTimeMillis() - initialMillis) > 20000) || readRecords > 10;
	}

	private static void consumerLogic(ConsumerRecords<String, Object> records) {

		Iterator<ConsumerRecord<String, Object>> it = records.iterator();

		while (it.hasNext()) {
			ConsumerRecord<String, Object> record = it.next();
			System.out.println(record.value().getClass().getName());
			GenericRecord rec = (GenericRecord) record.value();
			rec.get("field1");
			System.out.println(record.topic());
			System.out.println(record.partition());
			System.out.println(record.key());
			System.out.println(record.offset());
			System.out.println(rec);
			System.out.println("++++++++++++++++++**");
		}
	}
}
