/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb;


import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.indracompany.sofia2.persistence.util.CalendarAdapter;

public class MongoDbDate implements Serializable {

	private static final long serialVersionUID = 1L;

	private String $date;
	
	public MongoDbDate() {
		$date = CalendarAdapter.marshalUtcDate();
	}

	public MongoDbDate(String date) {
		this.$date = date;
	}
	
	public MongoDbDate(JsonNode node) {
		this.$date = node.findValue("$date").asText();
	}
	
	public JsonNode toJson() {
		return JsonNodeFactory.instance.objectNode().put("$date", this.$date);
    }
    
	public String get$date() {
		return $date;
	}
	
	public void set$date(String $date) {
		this.$date = $date;
	}

	@Override
	public String toString() {
		return "MongoDbDate [$date=" + $date + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MongoDbDate))
			return false;
		MongoDbDate date = (MongoDbDate) other;
		return this.$date.equals(date.$date);
	}
}