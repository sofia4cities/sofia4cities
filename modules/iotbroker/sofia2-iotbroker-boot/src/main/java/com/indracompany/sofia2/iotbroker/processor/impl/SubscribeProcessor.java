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
package com.indracompany.sofia2.iotbroker.processor.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.SuscriptionNotificationsModel;
import com.indracompany.sofia2.config.repository.SuscriptionModelRepository;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.exception.OntologySchemaException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.common.util.SSAPUtils;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.model.SuscriptionModel;
import com.indracompany.sofia2.router.service.app.service.RouterSuscriptionService;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodySubscribeMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.enums.SSAPQueryType;

@Component
@EnableScheduling
public class SubscribeProcessor implements MessageTypeProcessor {

	@Autowired
	private RouterSuscriptionService routerService;
	@Autowired
	SecurityPluginManager securityPluginManager;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	SuscriptionModelRepository repository;

	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message) {

		@SuppressWarnings("unchecked")
		final SSAPMessage<SSAPBodySubscribeMessage> subscribeMessage = (SSAPMessage<SSAPBodySubscribeMessage>) message;
		SSAPMessage<SSAPBodyReturnMessage> response = new SSAPMessage<>();
		final String subsId = UUID.randomUUID().toString();
		response.setBody(new SSAPBodyReturnMessage());

		final Optional<IoTSession> session = securityPluginManager.getSession(subscribeMessage.getSessionKey());

		final SuscriptionModel model = new SuscriptionModel();
		model.setOntologyName(subscribeMessage.getBody().getOntology());
		model.setOperationType(SuscriptionModel.OperationType.SUSCRIBE);
		model.setQuery(subscribeMessage.getBody().getQuery());

		SuscriptionModel.QueryType qType = SuscriptionModel.QueryType.NATIVE;
		if (SSAPQueryType.SQL.equals(subscribeMessage.getBody().getQueryType())) {
			qType = SuscriptionModel.QueryType.SQLLIKE;
		}
		model.setQueryType(qType);
		model.setSessionKey(subscribeMessage.getSessionKey());

		model.setSuscriptionId(subsId);
		session.ifPresent(s -> model.setUser(s.getUserID()));

		OperationResultModel routerResponse = null;
		try {
			routerResponse = routerService.suscribe(model);
		} catch (final Exception e1) {
			// TODO: LOG
			response = SSAPUtils.generateErrorMessage(subscribeMessage, SSAPErrorCode.PROCESSOR, e1.getMessage());
			return response;
		}

		final String errorCode = routerResponse.getErrorCode();
		final String messageResponse = routerResponse.getMessage();
		final String operation = routerResponse.getOperation();
		final String result = routerResponse.getResult();
		System.out.println(errorCode + " " + messageResponse + " " + operation + " " + result);

		if (!StringUtils.isEmpty(routerResponse.getErrorCode())) {
			response = SSAPUtils.generateErrorMessage(subscribeMessage, SSAPErrorCode.PROCESSOR,
					routerResponse.getErrorCode());
			return response;

		}
		response.setDirection(SSAPMessageDirection.RESPONSE);
		response.setMessageType(SSAPMessageTypes.SUBSCRIBE);
		response.setSessionKey(subscribeMessage.getSessionKey());
		response.getBody().setOk(true);
		response.getBody().setError(routerResponse.getErrorCode());
		final String dataStr = "{\"subscriptionId\": \"" + subsId + "\",\"message\": \"" + routerResponse.getMessage()
				+ "\"}";
		JsonNode data;
		try {
			data = objectMapper.readTree(dataStr);
			response.getBody().setData(data);
		} catch (final IOException e) {
			// TODO: LOG
			response = SSAPUtils.generateErrorMessage(subscribeMessage, SSAPErrorCode.PROCESSOR, e.getMessage());
			return response;
		}

		return response;

		// return response;
	}

	@Override
	public List<SSAPMessageTypes> getMessageTypes() {
		return Collections.singletonList(SSAPMessageTypes.SUBSCRIBE);
	}

	@Override
	public boolean validateMessage(SSAPMessage<? extends SSAPBodyMessage> message)
			throws OntologySchemaException, BaseException, Exception {
		@SuppressWarnings("unchecked")
		final SSAPMessage<SSAPBodySubscribeMessage> subscribeMessage = (SSAPMessage<SSAPBodySubscribeMessage>) message;

		if (StringUtils.isEmpty(subscribeMessage.getBody().getQuery())) {
			throw new SSAPProcessorException(
					String.format(MessageException.ERR_FIELD_IS_MANDATORY, "query", message.getMessageType().name()));
		}

		if (subscribeMessage.getBody().getQueryType() == null) {
			throw new SSAPProcessorException(String.format(MessageException.ERR_FIELD_IS_MANDATORY, "queryType",
					message.getMessageType().name()));
		}

		// TODO: Detect sql injection

		return true;
	}

	@PostConstruct
	private void cleanSubscriptions() {

		this.repository.deleteAll();
	}

	@Transactional
	@Scheduled(fixedDelay = 300000)
	private void deleteOldSubscriptions() {
		ArrayList<SuscriptionNotificationsModel> subscriptionsToRemove = new ArrayList<SuscriptionNotificationsModel>();
		List<SuscriptionNotificationsModel> subscriptions = this.repository.findAll();
		for (SuscriptionNotificationsModel subscription : subscriptions) {

			Optional<IoTSession> session = securityPluginManager.getSession(subscription.getSessionKey());

			if (!session.isPresent())
				subscriptionsToRemove.add(subscription);

		}
		this.repository.delete(subscriptionsToRemove);
	}

}
