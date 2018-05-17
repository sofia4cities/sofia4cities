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
package com.indracompany.sofia2.streaming;

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

@ConditionalOnProperty(prefix = "sofia2.iotbroker.plugable.gateway.kafka", name = "enable", havingValue = "true")
@Service
public class KafkaService {
	
	@Value("${sofia2.iotbroker.plugable.gateway.kafka.host:localhost}")
	private String kafkaHost;
	
	@Value("${sofia2.iotbroker.plugable.gateway.kafka.port:9092}")
	private String kafkaPort;
	
	@Value("${sofia2.iotbroker.plugable.gateway.kafka.partitions:1}")
	int partitions;
	
	@Value("${sofia2.iotbroker.plugable.gateway.kafka.replication:1}")
	short replication;
	
	@Value("${sofia2.iotbroker.plugable.gateway.kafka.prefix:ontology_}")
	private String ontologyPrefix;
	
	@Value("${sofia2.iotbroker.plugable.gateway.kafka.group:ontologyGroup}")
	private String ontologyGroup;
	
	private AdminClient adminAcl;
	
	@PostConstruct
	public void postKafka() {
		
		try {
			Properties config = new Properties();
			config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost+":"+kafkaPort);
			adminAcl = AdminClient.create(config);
		} catch (Exception e) {}
		
	}
	
	public CreateTopicsResult createTopicWithPrefix(String name, int partitions, short replication ) {
		NewTopic t = new NewTopic(ontologyPrefix+name, partitions, replication);
		CreateTopicsResult result = adminAcl.createTopics(Arrays.asList(t));
		return result;
	}
	
	public DeleteTopicsResult deleteTopic(String name ) {
		DeleteTopicsResult result = adminAcl.deleteTopics(Arrays.asList(name));
		return result;
	}
	


}
