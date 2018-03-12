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

import java.util.ArrayList;
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
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteByIdMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyDeleteMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@Component
public class DeleteProcessor implements MessageTypeProcessor {

	@Autowired
	private RouterService routerService;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	SecurityPluginManager securityPluginManager;

	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message) {

		if(SSAPMessageTypes.DELETE.equals(message.getMessageType())) {
			final SSAPMessage<SSAPBodyDeleteMessage> deleteMessage = (SSAPMessage<SSAPBodyDeleteMessage>) message;
			return processDelete(deleteMessage);
		}

		if(SSAPMessageTypes.DELETE_BY_ID.equals(message.getMessageType())) {
			final SSAPMessage<SSAPBodyDeleteByIdMessage> deleteMessage = (SSAPMessage<SSAPBodyDeleteByIdMessage>) message;
			return processDeleteById(deleteMessage);
		}

		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		responseMessage = SSAPUtils.generateErrorMessage(message, SSAPErrorCode.PROCESSOR, "Mesage not supported");
		return responseMessage;
	}

	private SSAPMessage<SSAPBodyReturnMessage> processDeleteById(SSAPMessage<SSAPBodyDeleteByIdMessage> message) {
		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		responseMessage.setBody(new SSAPBodyReturnMessage());
		responseMessage.getBody().setOk(true);
		final Optional<IoTSession> session = securityPluginManager.getSession(message.getSessionKey());
		final OperationModel model = new OperationModel();
		model.setObjectId(message.getBody().getId());
		model.setOntologyName(message.getBody().getOntology());
		model.setOperationType(OperationType.DELETE);
		model.setQueryType(QueryType.NATIVE);
		model.setUser(session.get().getUserID());
		model.setClientPlatformId(session.get().getClientPlatform());
		//		model.setBody(message.getBody().getData().toString());

		final NotificationModel modelNotification= new NotificationModel();
		modelNotification.setOperationModel(model);

		OperationResultModel result;
		String responseStr = null;
		String messageStr= null;
		try {
			result = routerService.delete(modelNotification);
			responseStr = result.getResult();
			messageStr = result.getMessage();
			final String response = String.format("{\"nDeleted\":%s}",responseStr);
			responseMessage.getBody().setData(objectMapper.readTree(response));

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

	private SSAPMessage<SSAPBodyReturnMessage> processDelete( SSAPMessage<SSAPBodyDeleteMessage> message) {

		SSAPMessage<SSAPBodyReturnMessage> responseMessage = new SSAPMessage<>();
		responseMessage.setBody(new SSAPBodyReturnMessage());
		responseMessage.getBody().setOk(true);
		final Optional<IoTSession> session = securityPluginManager.getSession(message.getSessionKey());

		final OperationModel model = new OperationModel();
		model.setOntologyName(message.getBody().getOntology());
		model.setOperationType(OperationType.DELETE);
		model.setQueryType(QueryType.NATIVE);
		model.setBody(message.getBody().getQuery());
		model.setUser(session.get().getUserID());
		model.setClientPlatformId(session.get().getClientPlatform());

		final NotificationModel modelNotification= new NotificationModel();
		modelNotification.setOperationModel(model);

		OperationResultModel result;
		String responseStr = null;
		String messageStr= null;
		try {
			result = routerService.delete(modelNotification);
			messageStr = result.getMessage();
			responseStr = result.getResult();

			final String response = String.format("{\"nDeleted\":%s}",responseStr);
			responseMessage.getBody().setData(objectMapper.readTree(response));

		} catch (final Exception e) {
			// TODO: LOG

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
		final List<SSAPMessageTypes> types = new ArrayList<>();
		types.add(SSAPMessageTypes.DELETE);
		types.add(SSAPMessageTypes.DELETE_BY_ID);
		return types;
	}

	@Override
	public boolean validateMessage(SSAPMessage<? extends SSAPBodyMessage> message)
			throws OntologySchemaException, BaseException, Exception {

		if(SSAPMessageTypes.DELETE.equals(message.getMessageType())) {
			final SSAPMessage<SSAPBodyDeleteMessage> deleteMessage = (SSAPMessage<SSAPBodyDeleteMessage>) message;
			return validateDelete(deleteMessage);
		}

		if(SSAPMessageTypes.DELETE_BY_ID.equals(message.getMessageType())) {
			final SSAPMessage<SSAPBodyDeleteByIdMessage> deleteMessage = (SSAPMessage<SSAPBodyDeleteByIdMessage>) message;
			return validateDeleteById(deleteMessage);
		}

		return false;
	}

	private boolean validateDeleteById(SSAPMessage<SSAPBodyDeleteByIdMessage> message) throws SSAPProcessorException {
		if( StringUtils.isEmpty(message.getBody().getId()) ) {
			throw new SSAPProcessorException(String.format(MessageException.ERR_FIELD_IS_MANDATORY, "id" ,message.getMessageType().name()));
		}

		return true;
	}

	private boolean validateDelete(SSAPMessage<SSAPBodyDeleteMessage> message) throws SSAPProcessorException {
		if( StringUtils.isEmpty(message.getBody().getQuery()) ) {
			throw new SSAPProcessorException(String.format(MessageException.ERR_FIELD_IS_MANDATORY, "quey", message.getMessageType().name()));
		}
		return true;
	}

}
