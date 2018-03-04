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
package com.indracompany.sofia2.iotbroker.processor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.AuthenticationException;
import com.indracompany.sofia2.iotbroker.common.exception.AuthorizationException;
import com.indracompany.sofia2.iotbroker.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.exception.OntologySchemaException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.common.util.SSAPUtils;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPErrorCode;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.util.SSAPMessageGenerator;

@Component
public class MessageProcessorDelegate implements MessageProcessor {

	@Autowired
	SecurityPluginManager securityPluginManager;

	@Autowired
	List<MessageTypeProcessor> processors;

	@Autowired
	private ApplicationContext context;

	@Override
	public <T extends SSAPBodyMessage> SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<T> message) {

		// TODO: PRE-PROCESSORS
		// DONE: PROCESS
		// DONE: CHECK SSAP COMPLIANCE
		// DONE: CHECK CREDENTIALS
		// DONE: CHECK AUTHRIZATIONS & PERMISSIONS
		// TODO: VALIDATE ONTOLOGY SCHEMA IF NECESSARY
		// DONE: GET PROCESSOR AN PROCESS
		// TODO: POST-PROCESSORS
		// DONE: RETURN

		SSAPMessage<SSAPBodyReturnMessage> response = null;

		try {

			final Optional<SSAPMessage<SSAPBodyReturnMessage>> validation = this.validateMessage(message);


			if (validation.isPresent()) {
				return validation.get();
			}

			final MessageTypeProcessor processor = proxyProcesor(message);

			processor.validateMessage(message);
			response = processor.process(message);

			if(!SSAPMessageDirection.ERROR.equals(response.getDirection())) {
				response.setDirection(SSAPMessageDirection.RESPONSE);
				response.setMessageId(message.getMessageId());
				response.setMessageType(message.getMessageType());
				response.setOntology(message.getOntology());
			}

		} catch (final SSAPProcessorException e) {
			response = SSAPUtils.generateErrorMessage(message, SSAPErrorCode.PROCESSOR,
					String.format(e.getMessage(), message.getMessageType().name()));
		} catch (final AuthorizationException e) {
			response = SSAPUtils.generateErrorMessage(message, SSAPErrorCode.AUTHORIZATION,
					String.format(e.getMessage(), message.getMessageType().name()));
		} catch (final AuthenticationException e) {
			response = SSAPUtils.generateErrorMessage(message, SSAPErrorCode.AUTENTICATION,
					String.format(e.getMessage(), message.getMessageType().name()));
		} catch (final OntologySchemaException e) {
			response = SSAPUtils.generateErrorMessage(message, SSAPErrorCode.PROCESSOR,
					String.format(e.getMessage(), message.getMessageType().name()));
		} catch (final BaseException e) {
			response = SSAPUtils.generateErrorMessage(message, SSAPErrorCode.PROCESSOR,
					String.format(e.getMessage(), message.getMessageType().name()));
		} catch (final Exception e) {
			response = SSAPUtils.generateErrorMessage(message, SSAPErrorCode.PROCESSOR,
					String.format(e.getMessage(), message.getMessageType().name()));
		}

		return response;
	}

	public Optional<SSAPMessage<SSAPBodyReturnMessage>> validateMessage(SSAPMessage<? extends SSAPBodyMessage> message)
	{
		SSAPMessage<SSAPBodyReturnMessage> response = null;

		// Check presence of sessionKey and authorization of sessionKey
		if (message.getBody().isSessionKeyMandatory() && StringUtils.isEmpty(message.getSessionKey())) {
			response = SSAPMessageGenerator.generateResponseErrorMessage(message, SSAPErrorCode.PROCESSOR,
					String.format(MessageException.ERR_FIELD_IS_MANDATORY, "Sessionkey", message.getMessageType().name()));

			return Optional.of(response);
		}

		if (message.getBody().isSessionKeyMandatory()) {
			if(!securityPluginManager.checkSessionKeyActive(message.getSessionKey())) {
				response = SSAPMessageGenerator.generateResponseErrorMessage(message, SSAPErrorCode.AUTENTICATION,
						String.format(MessageException.ERR_SESSIONKEY_NOT_VALID, message.getMessageType().name()));
			}
		}

		// Check if ontology is present and autorization for ontology
		if (message.getBody().isOntologyMandatory()) {
			if(StringUtils.isEmpty(message.getOntology())) {
				response = SSAPMessageGenerator.generateResponseErrorMessage(message, SSAPErrorCode.PROCESSOR,
						String.format(MessageException.ERR_ONTOLOGY_SCHEMA, message.getMessageType().name()));
				return Optional.of(response);
			}

			if(!securityPluginManager.checkAuthorization(message.getMessageType(), message.getOntology(), message.getSessionKey())) {
				response = SSAPMessageGenerator.generateResponseErrorMessage(message, SSAPErrorCode.AUTHORIZATION,
						String.format(MessageException.ERR_ONTOLOGY_AUTH, message.getMessageType().name()));
				return Optional.of(response);
			}
		}

		return Optional.empty();

	}

	public MessageTypeProcessor proxyProcesor(SSAPMessage<? extends SSAPBodyMessage> message)
			throws SSAPProcessorException {

		if (null == message.getMessageType()) {
			throw new SSAPProcessorException(MessageException.ERR_SSAP_MESSAGETYPE_MANDATORY_NOT_NULL);
		}

		final SSAPMessageTypes type = message.getMessageType();

		final List<MessageTypeProcessor> filteredProcessors = processors.stream()
				.filter(p -> p.getMessageTypes().contains(type)).collect(Collectors.toList());

		if (filteredProcessors.isEmpty()) {
			throw new SSAPProcessorException(
					String.format(MessageException.ERR_PROCESSOR_NOT_FOUND, message.getMessageType()));
		}

		return filteredProcessors.get(0);

	}

}
