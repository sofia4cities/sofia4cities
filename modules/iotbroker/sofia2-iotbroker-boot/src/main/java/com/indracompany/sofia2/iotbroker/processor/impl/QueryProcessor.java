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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.exception.OntologySchemaException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.common.util.SSAPUtils;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationModel.OperationType;
import com.indracompany.sofia2.router.service.app.model.OperationModel.QueryType;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterService;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyQueryMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.enums.SSAPQueryType;

@Component
public class QueryProcessor implements MessageTypeProcessor {
	@Autowired
	private RouterService routerService;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	SecurityPluginManager securityPluginManager;

	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message)
			throws BaseException, Exception {

		final SSAPMessage<SSAPBodyQueryMessage> queryMessage = (SSAPMessage<SSAPBodyQueryMessage>) message;
		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		responseMessage.setBody(new SSAPBodyReturnMessage());
		responseMessage.getBody().setOk(true);
		final Optional<IoTSession> session = securityPluginManager.getSession(queryMessage.getSessionKey());

		final OperationModel model = new OperationModel();

		model.setBody(queryMessage.getBody().getQuery());
		model.setOntologyName(queryMessage.getBody().getOntology());
		model.setOperationType(OperationType.QUERY);
		if( SSAPQueryType.SQL.equals(queryMessage.getBody().getQueryType())) {
			model.setQueryType(OperationModel.QueryType.SQLLIKE);
		}
		else {
			model.setQueryType(QueryType.valueOf(queryMessage.getBody().getQueryType().name()));
		}
		
		session.ifPresent(s -> model.setUser(s.getUserID()));
		session.ifPresent(s -> model.setClientPlatformId(s.getClientPlatform()));

		final NotificationModel modelNotification= new NotificationModel();
		modelNotification.setOperationModel(model);

		OperationResultModel result;
		String responseStr = null;
		String messageStr= null;
		try {
			result = routerService.query(modelNotification);
			responseStr = result.getResult();
			messageStr = result.getMessage();
			responseMessage.getBody().setData(objectMapper.readTree(responseStr));

		}
		catch (final Exception e) {
			// TODO LOG

			final String error=MessageException.ERR_DATABASE;
			responseMessage = SSAPUtils.generateErrorMessage(message, SSAPErrorCode.PROCESSOR, error);
			if(messageStr != null) {
				responseMessage.getBody().setError(messageStr);
			}
		}

		return responseMessage;
	}

	@Override
	public List<SSAPMessageTypes> getMessageTypes() {
		return Collections.singletonList(SSAPMessageTypes.QUERY);
	}

	@Override
	public boolean validateMessage(SSAPMessage<? extends SSAPBodyMessage> message)
			throws OntologySchemaException, BaseException, Exception {
		final SSAPMessage<SSAPBodyQueryMessage> queryMessage = (SSAPMessage<SSAPBodyQueryMessage>) message;

		if( StringUtils.isEmpty(queryMessage.getBody().getQuery()) ) {
			throw new SSAPProcessorException(String.format(MessageException.ERR_FIELD_IS_MANDATORY, "query" ,message.getMessageType().name()));
		}

		if( queryMessage.getBody().getQueryType() == null ) {
			throw new SSAPProcessorException(String.format(MessageException.ERR_FIELD_IS_MANDATORY, "queryType" ,message.getMessageType().name()));
		}

		//TODO: Detect sql injection

		return true;
	}

}
