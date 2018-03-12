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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.AuthenticationException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPComplianceException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@Component
public class JoinProcessor implements MessageTypeProcessor {

	@Autowired
	SecurityPluginManager securityManager;

	@Autowired
	ObjectMapper mapper;

	@SuppressWarnings("unchecked")
	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message)
			throws SSAPComplianceException, AuthenticationException {
		final SSAPMessage<SSAPBodyJoinMessage> join = (SSAPMessage<SSAPBodyJoinMessage>) message;
		final SSAPMessage<SSAPBodyReturnMessage> response = new SSAPMessage<>();
		response.setBody(new SSAPBodyReturnMessage());
		response.getBody().setOk(true);
		try {
			response.getBody().setData(mapper.readTree("{}"));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (StringUtils.isEmpty(join.getBody().getToken())) {
			throw new SSAPComplianceException(String.format(MessageException.ERR_FIELD_IS_MANDATORY, "token", message.getMessageType().name()));
		}

		final Optional<IoTSession> session = securityManager.authenticate(join.getBody().getToken(), join.getBody().getClientPlatform(), join.getBody().getClientPlatformInstance());
		session.ifPresent( s -> {
			response.setSessionKey(s.getSessionKey());
			try {
				response.getBody().setData(mapper.readTree("{\"sessionKey\":\""+s.getSessionKey()+"\"}"));
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		} );

		if ( !StringUtils.isEmpty(response.getSessionKey()) ) {
			response.setDirection(SSAPMessageDirection.RESPONSE);
			response.setMessageId(join.getMessageId());
			response.setMessageType(SSAPMessageTypes.JOIN);
		} else {
			throw new AuthenticationException(MessageException.ERR_SESSIONKEY_NOT_ASSINGED);
		}

		return response;
	}

	@Override
	public List<SSAPMessageTypes> getMessageTypes() {
		return Collections.singletonList(SSAPMessageTypes.JOIN);
	}

	@Override
	public boolean validateMessage(SSAPMessage<? extends SSAPBodyMessage> message) throws SSAPProcessorException {
		final SSAPMessage<SSAPBodyJoinMessage> join = (SSAPMessage<SSAPBodyJoinMessage>) message;

		if(StringUtils.isEmpty(join.getBody().getClientPlatform()) || StringUtils.isEmpty(join.getBody().getClientPlatformInstance()))
		{
			throw new SSAPProcessorException(String.format(MessageException.ERR_FIELD_IS_MANDATORY, "ClientPlatform",join.getMessageType().name()));
		}

		return true;
	}

}
