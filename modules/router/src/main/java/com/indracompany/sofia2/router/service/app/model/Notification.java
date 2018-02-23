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
package com.indracompany.sofia2.router.service.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Notification implements Serializable{

	private String ontologyId;
	private String ontologyName;
	
	private List<String> instanceOntologyIds=new ArrayList<String>();
	private String messageType;
	private String sessionKey;
	private String messageId;
	private String operationType;
	
	private String body;
	private String objectId;
	private String notificationEntityId;
	
	public String getOntologyId() {
		return ontologyId;
	}
	public void setOntologyId(String ontologyId) {
		this.ontologyId = ontologyId;
	}
	public String getOntologyName() {
		return ontologyName;
	}
	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}
	public List<String> getInstanceOntologyIds() {
		return instanceOntologyIds;
	}
	public void setInstanceOntologyIds(List<String> instanceOntologyIds) {
		this.instanceOntologyIds = instanceOntologyIds;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getNotificationEntityId() {
		return notificationEntityId;
	}
	public void setNotificationEntityId(String notificationEntityId) {
		this.notificationEntityId = notificationEntityId;
	}
	
	
}