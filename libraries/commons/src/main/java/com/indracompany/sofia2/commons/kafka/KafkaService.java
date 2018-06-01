/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2.commons.kafka;

import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(prefix = "sofia2.iotbroker.plugable.gateway.kafka", name = "enable", havingValue = "true")
@Service
@Slf4j
public class KafkaService {

	@Value("${sofia2.iotbroker.plugable.gateway.kafka.host:localhost}")
	private String kafkaHost;

	@Value("${sofia2.iotbroker.plugable.gateway.kafka.port:9092}")
	private String kafkaPort;

	@Value("${sofia2.iotbroker.plugable.gateway.kafka.user:admin}")
	private String kafkaUser;

	@Value("${sofia2.iotbroker.plugable.gateway.kafka.password:admin-secret}")
	private String kafkaPassword;

	@Value("${sofia2.iotbroker.plugable.gateway.kafka.partitions:1}")
	int partitions;

	@Value("${sofia2.iotbroker.plugable.gateway.kafka.replication:1}")
	short replication;

	@Value("${sofia2.iotbroker.plugable.gateway.kafka.prefix:ontology_}")
	private String ontologyPrefix;

	@Value("${sofia2.iotbroker.plugable.gateway.kafka.group:ontologyGroup}")
	private String ontologyGroup;

	private AdminClient adminAcl;

	private void applySecurity(Properties config) {
		if (kafkaPort.contains("9092") == false) {
			config.put("security.protocol", "SASL_PLAINTEXT");
			config.put("sasl.mechanism", "PLAIN");

			config.put("sasl.jaas.config",
					"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + kafkaUser
							+ "\" password=\"" + kafkaPassword + "\";");
		}
	}

	@PostConstruct
	public void postKafka() {

		try {
			Properties config = new Properties();
			config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
			applySecurity(config);
			adminAcl = AdminClient.create(config);
		} catch (Exception e) {
		}

	}

	public String getTopicName(String ontology) {
		return ontologyPrefix + ontology;
	}

	public CreateTopicsResult createTopicWithPrefix(String name, int partitions, short replication) {
		NewTopic t = new NewTopic(getTopicName(name), partitions, replication);
		CreateTopicsResult result = adminAcl.createTopics(Arrays.asList(t));
		return result;
	}

	public CreateTopicsResult createTopicWithPrefix(String name) {
		NewTopic t = new NewTopic(getTopicName(name), partitions, replication);
		CreateTopicsResult result = adminAcl.createTopics(Arrays.asList(t));
		return result;
	}

	public boolean createTopicForOntology(String name) {
		NewTopic t = new NewTopic(getTopicName(name), partitions, replication);
		try {
			log.info("Creating topic '{}'", getTopicName(name));
			CreateTopicsResult result = adminAcl.createTopics(Arrays.asList(t));
			result.all().get();
			return true;
		} catch (Exception e) {
			log.info("Cannot ensure topic creation for  '{}'", getTopicName(name));
			return false;
		}
	}

	public DeleteTopicsResult deleteTopic(String name) {
		DeleteTopicsResult result = adminAcl.deleteTopics(Arrays.asList(name));
		return result;
	}

}
