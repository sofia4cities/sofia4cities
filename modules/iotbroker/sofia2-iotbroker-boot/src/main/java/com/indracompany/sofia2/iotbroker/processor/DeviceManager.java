package com.indracompany.sofia2.iotbroker.processor;

import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyReturnMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public interface DeviceManager {

	public <T extends SSAPBodyMessage>void registerActivity(SSAPMessage<T> request, SSAPMessage<SSAPBodyReturnMessage> response, IoTSession session);

}
