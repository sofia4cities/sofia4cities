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
package com.indracompany.sofia2.simulator.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class FieldRandomizerServiceImpl implements FieldRandomizerService {

	private static final String FIXED_NUMBER = "FIXED_NUMBER";
	private static final String FIXED_STRING = "FIXED_STRING";
	private static final String FIXED_DATE = "FIXED_DATE";
	private static final String FIXED_INTEGER = "FIXED_INTEGER";
	private static final String COSINE_NUMBER = "COSINE_NUMBER";
	private static final String SINE_NUMBER = "SINE NUMBER";
	private static final String RANDOM_NUMBER = "RANDOM_NUMBER";
	private static final String RANDOM_INTEGER = "RANDOM_INTEGER";
	private static final String RANDOM_DATE = "RANDOM_DATE";
	private static final String RANDOM_STRING = "RANDOM_STRING";
	private static final String NULL = "NULL";

	@Override
	public JsonNode randomizeFields(JsonNode json, JsonNode schema) {
		ObjectMapper mapper = new ObjectMapper();

		JsonNode map = schema;
		String context = schema.fields().next().getKey();
		Iterator<String> fields = json.fieldNames();
		while (fields.hasNext()) {
			String field = fields.next();
			String function = json.path(field).get("function").asText();
			String finalField = null;
			String path = "/" + context;
			// if field is embbed object
			if (field.contains(".")) {
				String array[] = field.split("\\.");
				finalField = array[array.length - 1];
				for (int s = 0; s < array.length - 1; s++) {
					if (map.at(path).isArray())
						path = path + "/0";
					path = path + "/" + array[s];

				}

			} else {
				finalField = field;
				// path= path + "/"+ field;
			}
			if (map.at(path).isArray())
				path = path + "/0";

			switch (function) {
			case FIXED_NUMBER:
				((ObjectNode) map.at(path)).put(finalField, json.path(field).get("value").asDouble());
				break;
			case FIXED_INTEGER:
				((ObjectNode) map.at(path)).put(finalField, json.path(field).get("value").asInt());
				break;
			case RANDOM_NUMBER:
				((ObjectNode) map.at(path)).put(finalField,
						this.randomizeDouble(json.path(field).get("from").asDouble(),
								json.path(field).get("to").asDouble(), json.path(field).get("precision").asInt()));
				break;
			case RANDOM_INTEGER:
				((ObjectNode) map.at(path)).put(finalField,
						this.randomizeInt(json.path(field).get("from").asInt(), json.path(field).get("to").asInt()));
				break;
			case COSINE_NUMBER:
				double angleCos = Math.toRadians(json.path(field).get("angle").asDouble());
				double multiplierCos = json.path(field).get("multiplier").asDouble();
				((ObjectNode) map.at(path)).put(finalField, Math.cos(angleCos) * multiplierCos);
				break;
			case SINE_NUMBER:
				double angleSin = Math.toRadians(json.path(field).get("angle").asDouble());
				double multiplierSin = json.path(field).get("multiplier").asDouble();
				((ObjectNode) map.at(path)).put(finalField, Math.sin(angleSin) * multiplierSin);
				break;
			case FIXED_STRING:
				((ObjectNode) map.at(path)).put(finalField, json.path(field).get("value").asText());
				break;
			case RANDOM_STRING:
				((ObjectNode) map.at(path)).put(finalField,
						this.randomizeStrings(json.path(field).get("list").asText()));
				break;
			case FIXED_DATE:
				Date date;
				try {
					DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					date = df.parse(json.path(field).get("value").asText());
				} catch (ParseException e) {
					date = new Date();
				}
				JsonNode dateJson = mapper.createObjectNode();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

				((ObjectNode) dateJson).put("$date", df.format(date));
				((ObjectNode) map.at(path)).set(finalField, dateJson);

				break;
			case RANDOM_DATE:
				Date dateFrom;
				Date dateTo;
				Date dateRandom = new Date();
				;

				try {
					DateFormat dfr = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					dateFrom = dfr.parse(json.path(field).get("from").asText());
					dateTo = dfr.parse(json.path(field).get("to").asText());
					dateRandom = this.randomizeDate(dateFrom, dateTo);
				} catch (ParseException e) {
					dateRandom = new Date();
				}
				JsonNode dateRandomJson = mapper.createObjectNode();
				df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
				((ObjectNode) dateRandomJson).put("$date", df.format(dateRandom));
				((ObjectNode) map.at(path)).set(finalField, dateRandomJson);
				break;
			case NULL:
				// ((ObjectNode) map.at(path)).put(finalField, "null");
				break;

			}

		}

		return map;
	}

	public String randomizeStrings(String list) {
		List<String> words = new ArrayList<String>(Arrays.asList(list.split(",")));
		if (words.size() >= 1) {
			int selection = this.randomizeInt(0, words.size() - 1);
			return words.get(selection);
		} else
			return list;

	}

	public int randomizeInt(int min, int max) {
		Random random = new Random();
		int randomInt = random.nextInt((max - min) + 1) + min;
		return randomInt;
	}

	public double randomizeDouble(double min, double max, int precision) {
		Random random = new Random();
		Double randomDouble = min + (max - min) * random.nextDouble();
		Double randomDoubleTruncated = BigDecimal.valueOf(randomDouble).setScale(precision, RoundingMode.HALF_UP)
				.doubleValue();
		return randomDoubleTruncated;
	}

	public Date randomizeDate(Date from, Date to) {

		ThreadLocalRandom th = ThreadLocalRandom.current();
		Date randomDate = new Date(th.nextLong(from.getTime(), to.getTime()));
		return randomDate;

	}
}
