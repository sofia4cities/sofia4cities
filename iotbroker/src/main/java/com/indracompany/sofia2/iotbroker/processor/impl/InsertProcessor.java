package com.indracompany.sofia2.iotbroker.processor.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.iotbroker.processor.MessageTypeProcessor;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

@Profile({"dummy-dev"})
@Component
public class InsertProcessor implements MessageTypeProcessor {

	@Override
	public <T extends SSAPBodyMessage> SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<T> message) {
		// TODO Auto-generated method stub
		return null;
	}

}
