package com.indracompany.sofia2.iotbroker.processor;

import java.util.function.Consumer;

import com.indracompany.sofia2.ssap.SSAPMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyIndicationMessage;

public interface GatewayNotifier {

	void addSubscriptionListener(String key, Consumer<SSAPMessage<SSAPBodyIndicationMessage>> c);

	void notify(SSAPMessage<SSAPBodyIndicationMessage> indication);

}
