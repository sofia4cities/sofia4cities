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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.jmx.mbean.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class ConnectedKP implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String kpId;
	private String instanceId;
	private String sessionkey;
	private long lastSSAPTimestamp;
	private long lastIndicationTimestamp;
	private long maxProcessingTime;
	private int numberOfProcessedMessages;
	private int maxProcessedMessageSize;
	private int avgProcessedMessageSize;
	private long avgProcessingTime;
	private Queue<String> latestSSAP;
	
	private int maxSSAPMessageStored; 
	
	public ConnectedKP(String kpId, String instanceId, String sessionkey, int maxSSAPMessageStored) {
		this.kpId = kpId;
		this.instanceId = instanceId;
		this.sessionkey = sessionkey;
		this.lastSSAPTimestamp=0;
		this.lastIndicationTimestamp=0;
		this.maxProcessingTime=0;
		this.numberOfProcessedMessages=0;
		this.maxProcessedMessageSize=0;
		this.avgProcessedMessageSize=0;
		this.avgProcessingTime=0;
		this.latestSSAP=new LinkedList<String>();
		
		this.maxSSAPMessageStored=maxSSAPMessageStored;
	}
	
	
	public synchronized void addSSAPMessage(String ssapMessage, long processingTime){
		//Borra mensajes siguiendo algoritmo FIFO
		while(latestSSAP.size()>maxSSAPMessageStored){
			latestSSAP.remove();
		}
		
		//Añade el nuevo mensaje
		latestSSAP.add(ssapMessage);
		
		//Actualiza estadisticas
		this.lastSSAPTimestamp=new java.util.Date().getTime();
		this.numberOfProcessedMessages++;
		
		if(processingTime > this.maxProcessingTime){
			this.maxProcessingTime=processingTime;
		}
		
		if(ssapMessage.getBytes().length>this.maxProcessedMessageSize){
			this.maxProcessedMessageSize=ssapMessage.getBytes().length;
		}
		
		this.avgProcessedMessageSize=((this.avgProcessedMessageSize*(this.numberOfProcessedMessages-1))+ssapMessage.getBytes().length)/this.numberOfProcessedMessages;
		
		this.avgProcessingTime=((this.avgProcessingTime*(this.numberOfProcessedMessages-1))+processingTime)/this.numberOfProcessedMessages;
		
	}
	
	public void setMaxSSAPMessageStored(int maxSSAPMessageStored){
		this.maxSSAPMessageStored=maxSSAPMessageStored;
		
		while(latestSSAP.size()>=maxSSAPMessageStored){
			latestSSAP.remove();
		}
	}
	

	public String getKpId() {
        return this.kpId;
    }

	public void setKpId(String kpId) {
        this.kpId = kpId;
    }

	public String getInstanceId() {
        return this.instanceId;
    }

	public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

	public String getSessionkey() {
        return this.sessionkey;
    }

	public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

	public long getLastSSAPTimestamp() {
        return this.lastSSAPTimestamp;
    }

	public void setLastSSAPTimestamp(long lastSSAPTimestamp) {
        this.lastSSAPTimestamp = lastSSAPTimestamp;
    }

	public long getLastIndicationTimestamp() {
        return this.lastIndicationTimestamp;
    }

	public void setLastIndicationTimestamp(long lastIndicationTimestamp) {
        this.lastIndicationTimestamp = lastIndicationTimestamp;
    }

	public long getMaxProcessingTime() {
        return this.maxProcessingTime;
    }

	public void setMaxProcessingTime(long maxProcessingTime) {
        this.maxProcessingTime = maxProcessingTime;
    }

	public int getNumberOfProcessedMessages() {
        return this.numberOfProcessedMessages;
    }

	public void setNumberOfProcessedMessages(int numberOfProcessedMessages) {
        this.numberOfProcessedMessages = numberOfProcessedMessages;
    }

	public int getMaxProcessedMessageSize() {
        return this.maxProcessedMessageSize;
    }

	public void setMaxProcessedMessageSize(int maxProcessedMessageSize) {
        this.maxProcessedMessageSize = maxProcessedMessageSize;
    }

	public int getAvgProcessedMessageSize() {
        return this.avgProcessedMessageSize;
    }

	public void setAvgProcessedMessageSize(int avgProcessedMessageSize) {
        this.avgProcessedMessageSize = avgProcessedMessageSize;
    }

	public long getAvgProcessingTime() {
        return this.avgProcessingTime;
    }

	public void setAvgProcessingTime(long avgProcessingTime) {
        this.avgProcessingTime = avgProcessingTime;
    }

	public Queue<String> getLatestSSAP() {
        return this.latestSSAP;
    }

	public void setLatestSSAP(Queue<String> latestSSAP) {
        this.latestSSAP = latestSSAP;
    }

	public int getMaxSSAPMessageStored() {
        return this.maxSSAPMessageStored;
    }

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

	public static ConnectedKP fromJsonToConnectedKP(String json) {
        return new JSONDeserializer<ConnectedKP>().use(null, ConnectedKP.class).deserialize(json);
    }

	public static String toJsonArray(Collection<ConnectedKP> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<ConnectedKP> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<ConnectedKP> fromJsonArrayToConnectedKPs(String json) {
        return new JSONDeserializer<List<ConnectedKP>>().use(null, ArrayList.class).use("values", ConnectedKP.class).deserialize(json);
    }
}
