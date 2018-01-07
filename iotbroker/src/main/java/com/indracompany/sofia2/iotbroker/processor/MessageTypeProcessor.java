package com.indracompany.sofia2.iotbroker.processor;

import com.indracompany.sofia2.common.exception.BaseException;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public interface MessageTypeProcessor {
	SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<? extends SSAPBodyMessage> message) throws BaseException, Exception;
}
