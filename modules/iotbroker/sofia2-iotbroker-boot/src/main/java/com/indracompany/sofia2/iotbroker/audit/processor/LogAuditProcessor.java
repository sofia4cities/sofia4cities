package com.indracompany.sofia2.iotbroker.audit.processor;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.indracompany.sofia2.iotbroker.audit.aop.MessageAuditProcessor;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEvent;
import com.indracompany.sofia2.iotbroker.audit.bean.IotBrokerAuditEventFactory;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.gateway.GatewayInfo;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLogMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogAuditProcessor implements MessageAuditProcessor {

	@Override
	public IotBrokerAuditEvent process(SSAPMessage<? extends SSAPBodyMessage> message, IoTSession session,
			GatewayInfo info) {
		log.debug("Processing log message");
		SSAPBodyLogMessage logMessage = (SSAPBodyLogMessage) message.getBody();
		String textMessage = "Log operation for device " + session.getClientPlatformInstance();
		return IotBrokerAuditEventFactory.builder().build().createIotBrokerAuditEvent(logMessage, textMessage, session,
				info);
	}

	@Override
	public List<SSAPMessageTypes> getMessageTypes() {
		return Collections.singletonList(SSAPMessageTypes.LOG);
	}

}
