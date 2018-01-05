package com.indracompany.sofia2.iotbroker.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.indracompany.sofia2.common.exception.AuthenticationException;
import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPComplianceException;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPlugin;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPErrorCode;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

@Profile({"dummy-dev"})
@Component
public class JoinProcessor implements MessageTypeProcessor {

	@Autowired
	SecurityPluginManager securityManager;
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends SSAPBodyMessage> SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<T> message) throws SSAPComplianceException, AuthenticationException {
		SSAPMessage<SSAPBodyJoinMessage> join = (SSAPMessage<SSAPBodyJoinMessage>) message;
		SSAPMessage<SSAPBodyReturnMessage> response = new SSAPMessage<>();
		
		if(StringUtils.isEmpty(join.getBody().getToken())) {
			throw new SSAPComplianceException(MessageException.ERR_TOKEN_IS_MANDATORY);
		}
		
		List<String> sessionKeys = securityManager.authenticate(message);
		
		if(sessionKeys.size() > 0) {
			response.setDirection(SSAPMessageDirection.RESPONSE);
			response.setMessageId(join.getMessageId());
			response.setMessageType(SSAPMessageTypes.JOIN);
			response.setSessionKey(sessionKeys.get(0));
		}
		else {
			throw new AuthenticationException(MessageException.ERR_SESSIONKEY_NOT_ASSINGED);
		}
		
		return response;
	}

}
