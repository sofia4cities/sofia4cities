package com.indracompany.sofia2.ssap.util;

import com.indracompany.sofia2.ssap.SSAPErrorCode;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.SSAPMessageDirection;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public class SSAPMessageGenerator {

	public static SSAPMessage<SSAPBodyReturnMessage> generateResponseErrorMessage(SSAPMessage<? extends SSAPBodyMessage> message, 
			SSAPErrorCode errorCode, String errorMessage) {
		SSAPMessage<SSAPBodyReturnMessage> m = null;
		m = new SSAPMessage<>();
		m.setDirection(SSAPMessageDirection.ERROR);
		m.setMessageId(message.getMessageId());
		m.setSessionKey(message.getSessionKey());
		m.setBody(new SSAPBodyReturnMessage());
		m.getBody().setOk(false);
		m.getBody().setErrorCode(errorCode);
		m.getBody().setError(errorMessage);
		
		return m;
	}
}
