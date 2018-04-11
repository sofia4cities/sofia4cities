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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.iotbroker.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.plugable.impl.security.SecurityPluginManager;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@Component
public class LeaveProcessor implements MessageTypeProcessor {

	@Autowired
	SecurityPluginManager securityManager;
	@Autowired
	ObjectMapper objectMapper;

	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message)
			throws BaseException {
		final String sessionKey = message.getSessionKey();

		securityManager.closeSession(sessionKey);

		final SSAPMessage<SSAPBodyReturnMessage> response = new SSAPMessage<>();
		SSAPBodyReturnMessage body = new SSAPBodyReturnMessage();
		String dataStr = "{\"message\":\"Disconnected\"}";
		JsonNode data;
		try {
			data = objectMapper.readTree(dataStr);
			response.getBody().setData(data);
		} catch (final IOException e) {
			// TODO: LOG
			throw new SSAPProcessorException("Couldn't generate body data message");
		}
		response.setBody(new SSAPBodyReturnMessage());
		response.setDirection(SSAPMessageDirection.RESPONSE);
		response.setMessageType(SSAPMessageTypes.LEAVE);
		response.setSessionKey(sessionKey);
		response.getBody().setOk(true);

		return response;
	}

	@Override
	public List<SSAPMessageTypes> getMessageTypes() {
		return Collections.singletonList(SSAPMessageTypes.LEAVE);
	}

	@Override
	public boolean validateMessage(SSAPMessage<? extends SSAPBodyMessage> message) {
		return true;
	}

}
