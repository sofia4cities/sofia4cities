package com.indracompany.sofia2.iotbroker.processor;

import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public interface MessageProcessor {
	<T extends SSAPBodyMessage>SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<T> message);

}
