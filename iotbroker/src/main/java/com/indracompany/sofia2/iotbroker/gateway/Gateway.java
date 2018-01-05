package com.indracompany.sofia2.iotbroker.gateway;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;

import com.indracompany.sofia2.iotbroker.processor.MessageProcessor;

public abstract class Gateway {
	@Autowired
	MessageProcessor messageProcessor;
	
	public abstract void startGateway(boolean clearState);
	public abstract void listen(MessageProcessor processor);
	public abstract void stopGateway();
	
	@PostConstruct
	public void init() {
		this.startGateway(true);
		this.listen(messageProcessor);	
		
	}
	
	@PreDestroy
	public void destroy() {
		this.stopGateway();
		
	}
//	SSAPMessage<SSAPBodyReturnMessage> process(SSAPMessage<?> mensaje);

}
