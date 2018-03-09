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