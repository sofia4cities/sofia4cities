/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.persistence.ContextData;

import lombok.Getter;
import lombok.Setter;

public class MongoDbContextData  {

	private static final long serialVersionUID = 1L;

	@Getter @Setter private ContextData contextData;
	private MongoDbDate timestamp;
	
	public MongoDbContextData(){}
	
	public MongoDbContextData(JsonNode node) {
		contextData = new ContextData(node);
		this.timestamp = new MongoDbDate(node.findValue("timestamp"));
	}
	
	public MongoDbContextData(ContextData cd) {
		contextData = cd;
		this.timestamp = new MongoDbDate();
	}


	public String toJson() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			throw new RuntimeException("Unable to serialize contextData.");
		}
	}
		
	@Override
	public String toString() {
		return "MongoDbContextData ["
				+ "timestamp=" + timestamp + 
				", user=" + contextData.getUser() + 
				", clientPlatform="	+ contextData.getClientPatform() + 
				", clientConnection=" +contextData.getClientConnection() + 
				", clientSession=" + contextData.getClientSession() +
				", timeZoneId=" + contextData.getTimezoneId();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MongoDbContextData))
			return false;
		MongoDbContextData contextData = (MongoDbContextData) other;
		return super.equals(other) && this.timestamp.equals(contextData.timestamp);
	}
}
