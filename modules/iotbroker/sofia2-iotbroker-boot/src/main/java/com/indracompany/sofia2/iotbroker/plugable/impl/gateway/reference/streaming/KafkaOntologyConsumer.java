/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
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
package com.indracompany.sofia2.iotbroker.plugable.impl.gateway.reference.streaming;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.ontologydata.OntologyDataService;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;

import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(prefix = "sofia2.iotbroker.plugable.gateway.kafka", name = "enable", havingValue = "true")
@Slf4j
@Component
public class KafkaOntologyConsumer {

	@Autowired
	OntologyService ontologyService;

	@Autowired
	OntologyRepository ontologyRepository;

	@Autowired
	OntologyDataService ontologyDataService;

	ObjectMapper mapper = new ObjectMapper();

	@Value("${sofia2.iotbroker.plugable.gateway.kafka.prefix:ontology_}")
	private String ontologyPrefix;

	@Value("${sofia2.iotbroker.plugable.gateway.kafka.router.topic:router}")
	private String topicRouter;

	@Autowired
	private KafkaTemplate<String, NotificationModel> kafkaTemplate;

	public static final int COUNT = 20;

	private CountDownLatch latch = new CountDownLatch(COUNT);

	public CountDownLatch getLatch() {
		return latch;
	}

	// TODO
	//@KafkaListener(topicPattern = "${sofia2.iotbroker.plugable.gateway.kafka.topic.pattern}", containerFactory = "fooKafkaListenerContainerFactory")
	public void listenToParition(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic) {
		log.info("Received Message: " + message + " from partition: " + partition + " received topic:" + receivedTopic);

		String ontologyId = receivedTopic.replace(ontologyPrefix, "");

		boolean executable = true;

		// get user from AVRO!!!
		/*
		 * String sessionUserId="user"; ontologyService.getOntologyById(ontology,
		 * sessionUserId);
		 */

		String user = "administrator";
		Ontology ontology = ontologyRepository.getOne(ontologyId);
		/*
		 * try { JsonNode actualObj = mapper.readTree(message);
		 * ontologyDataService.checkOntologySchemaCompliance(actualObj, ontology); }
		 * catch (IOException e) {
		 * log.error("Data not valid to process internally: "+e.getMessage(),e);
		 * executable=true; } catch (DataSchemaValidationException e) {
		 * log.error("Data not valid to process internally: "+e.getMessage(),e);
		 * executable=true; }
		 */

		if (executable) {
			OperationType operationType = OperationType.INSERT;
			OperationModel model = OperationModel
					.builder(ontologyId, OperationType.valueOf(operationType.name()), user,
							OperationModel.Source.IOTBROKER)
					.body(message).clientPlatformId("").cacheable(false).build();

			NotificationModel modelNotification = new NotificationModel();

			modelNotification.setOperationModel(model);

			sendMessage(modelNotification);
		}

	}

	@KafkaListener(topicPattern = "${sofia2.iotbroker.plugable.gateway.kafka.topic.pattern}", containerFactory = "kafkaListenerContainerFactoryBatch")
	public void listenToParitionBatch(List<String> data,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic,
			@Header(KafkaHeaders.OFFSET) List<Long> offsets,
			Acknowledgment ack) {

		String user = "administrator";

		for (int i = 0; i < data.size(); i++) {
			log.info("received message='{}' with partition-offset='{}'", data.get(i),
					partitions.get(i) + "-" + offsets.get(i));
			

			String message = data.get(i);
			
			String ontologyId = receivedTopic.replace(ontologyPrefix, "");
			
			OperationType operationType = OperationType.INSERT;
			OperationModel model = OperationModel
					.builder(ontologyId, OperationType.valueOf(operationType.name()), user,
							OperationModel.Source.IOTBROKER)
					.body(message).clientPlatformId("").cacheable(false).build();

			NotificationModel modelNotification = new NotificationModel();

			modelNotification.setOperationModel(model);

			sendMessage(modelNotification);
			ack.acknowledge();
			latch.countDown();
		}

		log.info("end of batch receive");

	}

	public void sendMessage(NotificationModel message) {
		ListenableFuture<SendResult<String, NotificationModel>> pp = kafkaTemplate.send(topicRouter, message);

	}

}
