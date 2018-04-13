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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.indracompany.sofia2.ssap.body.SSAPBodyUnsubscribeMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@Component
public class UnsubscribeProcessor implements MessageTypeProcessor {

	@Autowired
	private RouterSuscriptionService routerService;
	@Autowired
	SecurityPluginManager securityPluginManager;
	@Autowired
	ObjectMapper objectMapper;


	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message)
			throws BaseException, Exception {
		final SSAPMessage<SSAPBodyUnsubscribeMessage> unsubscribeMessage = (SSAPMessage<SSAPBodyUnsubscribeMessage>) message;
		SSAPMessage<SSAPBodyReturnMessage> response = new SSAPMessage<>();
		response.setBody(new SSAPBodyReturnMessage());

		final Optional<IoTSession> session = securityPluginManager.getSession(unsubscribeMessage.getSessionKey());

		final SuscriptionModel model = new SuscriptionModel();
		model.setSuscriptionId(unsubscribeMessage.getBody().getSubscriptionId());
		model.setSessionKey(unsubscribeMessage.getSessionKey());
		session.ifPresent(s -> model.setUser(s.getUserID()));

		OperationResultModel routerResponse = null;
		try {
			routerResponse = routerService.unSuscribe(model);
		} catch (final Exception e1) {
			// TODO: LOG
			response = SSAPUtils.generateErrorMessage(unsubscribeMessage, SSAPErrorCode.PROCESSOR, e1.getMessage());
			return response;
		}
		final String errorCode = routerResponse.getErrorCode();
		final String messageResponse = routerResponse.getMessage();
		final String operation = routerResponse.getOperation();
		final String result = routerResponse.getResult();
		System.out.println(errorCode + " " + messageResponse + " " + operation + " " + result);

		if (!StringUtils.isEmpty(routerResponse.getErrorCode())) {
			response = SSAPUtils.generateErrorMessage(unsubscribeMessage, SSAPErrorCode.PROCESSOR,
					routerResponse.getErrorCode());
			return response;

		}
		response.setDirection(SSAPMessageDirection.RESPONSE);
		response.setMessageType(SSAPMessageTypes.UNSUBSCRIBE);
		response.setSessionKey(unsubscribeMessage.getSessionKey());
		response.getBody().setOk(true);
		response.getBody().setError(routerResponse.getErrorCode());
		final String dataStr = "{\"message\": \"" + routerResponse.getMessage() + "\"}";
		JsonNode data;
		try {
			data = objectMapper.readTree(dataStr);
			response.getBody().setData(data);
		} catch (final IOException e) {
			// TODO: LOG
			response = SSAPUtils.generateErrorMessage(unsubscribeMessage, SSAPErrorCode.PROCESSOR, e.getMessage());
			return response;
		}

		return response;

	}

	@Override
	public List<SSAPMessageTypes> getMessageTypes() {
		return Collections.singletonList(SSAPMessageTypes.UNSUBSCRIBE);
	}

	@Override
	public boolean validateMessage(SSAPMessage<? extends SSAPBodyMessage> message)
			throws OntologySchemaException, BaseException, Exception {
		final SSAPMessage<SSAPBodyUnsubscribeMessage> unsubscribeMessage = (SSAPMessage<SSAPBodyUnsubscribeMessage>) message;

		if (StringUtils.isEmpty(unsubscribeMessage.getBody().getSubscriptionId())) {
			throw new SSAPProcessorException(String.format(MessageException.ERR_FIELD_IS_MANDATORY, "subscriptionId",
					message.getMessageType().name()));
		}

		return true;
	}

}
