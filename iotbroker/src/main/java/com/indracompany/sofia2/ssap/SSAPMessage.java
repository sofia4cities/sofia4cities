package com.indracompany.sofia2.ssap;

import java.io.Serializable;

import com.indracompany.sofia2.ssap.body.SSAPBodyEmptyMessage;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;

public final class SSAPMessage<T extends SSAPBodyMessage>implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String messageId;
	protected String sessionKey;
	protected String ontology;
	protected SSAPMessageDirection direction;
	protected SSAPMessageTypes messageType;
//	protected SSAPBodyJoinMessage body;
	protected T body = (T) new SSAPBodyEmptyMessage();
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getOntology() {
		return ontology;
	}
	public void setOntology(String ontology) {
		this.ontology = ontology;
	}
	public SSAPMessageDirection getDirection() {
		return direction;
	}
	public void setDirection(SSAPMessageDirection direction) {
		this.direction = direction;
	}
	public SSAPMessageTypes getMessageType() {
		return messageType;
	}
	public void setMessageType(SSAPMessageTypes messageType) {
		this.messageType = messageType;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T body) {
		this.body = body;
	}
	
}
