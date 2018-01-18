/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
