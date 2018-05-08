package com.indracompany.sofia2.iotbroker.processor.impl;

import java.util.Collections;
import java.util.List;

import com.indracompany.sofia2.iotbroker.common.MessageException;
import com.indracompany.sofia2.iotbroker.common.exception.BaseException;
import com.indracompany.sofia2.iotbroker.common.exception.OntologySchemaException;
import com.indracompany.sofia2.iotbroker.common.exception.SSAPProcessorException;
import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLogMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

public class LogProcessor implements MessageTypeProcessor {

	@Override
	public SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message)
			throws BaseException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SSAPMessageTypes> getMessageTypes() {
		return Collections.singletonList(SSAPMessageTypes.LOG);
	}

	@Override
	public boolean validateMessage(SSAPMessage<? extends SSAPBodyMessage> message)
			throws OntologySchemaException, BaseException, Exception {
		final SSAPMessage<SSAPBodyLogMessage> logMessage = (SSAPMessage<SSAPBodyLogMessage>) message;
		if (logMessage.getBody().getMessage().isEmpty() || logMessage.getBody().getLevel() == null
				|| logMessage.getBody().getStatus() == null) {
			throw new SSAPProcessorException(String.format(MessageException.ERR_FIELD_IS_MANDATORY,
					"message, log level, status", message.getMessageType().name()));
		}
		return false;
	}

}
