package com.indracompany.sofia2.iotbroker.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.plugin.iotbroker.security.SecurityPluginManager;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

@Profile({"dummy-dev"})
@Component
public class LeaveProcessor implements MessageTypeProcessor {

	@Autowired
	SecurityPluginManager securityManager;
	@Override
	public <T extends SSAPBodyMessage> SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<T> message)
			throws BaseException {
		String sessionKey=message.getSessionKey();
		
		securityManager.closeSession(sessionKey);
		
//		return sessionKey;
		return null;
	}

}
