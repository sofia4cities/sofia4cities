/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.persistence.hadoop.json;

public class TimestampType extends JsonType {

	public TimestampType(String name) {
		super(name);
	}

	@Override
	String convert() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"").append(name).append(" \": { ").append("\"type\": \"object\",").append("\"required\": [ ")
				.append("\"$date\"").append("],").append("\"properties\": {").append("\"$date\": {")
				.append("\"type\": \"string\",").append("\"format\": \"date-time\"").append("}").append("},")
				.append("\"additionalProperties\": false").append("}");

		return builder.toString();
	}
}
