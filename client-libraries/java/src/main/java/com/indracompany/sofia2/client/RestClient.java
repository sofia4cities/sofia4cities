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
package com.indracompany.sofia2.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.indracompany.sofia2.ssap.enums.SSAPQueryType;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class RestClient {

	private String sessionKey;
	private String restServer;
	private final static String IOTBROKER_CONTEXT = "iotbroker";
	private final static String IOTBROKER_URL = "http://localhost:8081/iotbroker";
	private final static String JOIN_GET = "rest/client/join";
	private final static String LEAVE_GET = "rest/client/leave";
	private final static String LIST_GET = "rest/ontology";
	private final static String INSERT_POST = "rest/ontology";

	private OkHttpClient client;

	public RestClient(String restServer) {
		this.restServer = restServer;
	}

	/**
	 * Creates a REST session.
	 *
	 * @param token
	 *            The token associated with the device/client
	 * @param clientPlatform
	 *            The device/client identification
	 * @param clientPlatformInstance
	 *            The instance of the device
	 * @param timeout
	 *            Time in seconds for waiting response from Broker
	 * @return The session key for the session established between client and IoT
	 *         Broker
	 * 
	 */
	public String connect(String token, String clientPlatform, String clientPlatformInstance) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		HttpUrl urlJoinWithParams = new HttpUrl.Builder().scheme(HttpUrl.parse(this.restServer).scheme())
				.host(HttpUrl.parse(this.restServer).host()).port(HttpUrl.parse(this.restServer).port())
				.addPathSegment(HttpUrl.parse(this.restServer).pathSegments().get(0)).addEncodedPathSegments(JOIN_GET)
				.addQueryParameter("token", token).addQueryParameter("clientPlatform", clientPlatform)
				.addEncodedQueryParameter("clientPlatformId", clientPlatformInstance).build();
		Request request = new Request.Builder().url(urlJoinWithParams).get().build();

		client = new OkHttpClient();

		Response response = client.newCall(request).execute();
		log.info("Trying to join Iotbroker...");
		JsonNode session = mapper.readTree(response.body().string());
		try {
			this.sessionKey = session.get("sessionKey").asText();
			log.info("Session key is :" + this.sessionKey);
		} catch (Exception e) {
			log.error("Could not get session key");
			e.printStackTrace();
		}

		return this.sessionKey;

	}

	public List<JsonNode> getOntologyInstances(String ontology) {
		ObjectMapper mapper = new ObjectMapper();
		TypeFactory typeFactory = mapper.getTypeFactory();

		List<JsonNode> instances = new ArrayList<JsonNode>();
		HttpUrl urlJoinWithParams = new HttpUrl.Builder().scheme(HttpUrl.parse(this.restServer).scheme())
				.host(HttpUrl.parse(this.restServer).host()).port(HttpUrl.parse(this.restServer).port())
				.addPathSegment(HttpUrl.parse(this.restServer).pathSegments().get(0)).addEncodedPathSegments(LIST_GET)
				.addPathSegment(ontology).addEncodedQueryParameter("query", "db." + ontology + ".find()")
				.addQueryParameter("queryType", SSAPQueryType.NATIVE.name()).build();
		Request request = new Request.Builder().url(urlJoinWithParams).addHeader("Authorization", this.sessionKey).get().build();

		try {
			Response response = client.newCall(request).execute();
			String instancesAsText = response.body().string();
			instancesAsText = instancesAsText.replaceAll("\\\\\\\"", "\"").replace("\"{", "{").replace("}\"","}");
			System.out.print(instancesAsText);
			instances = mapper.readValue(instancesAsText,
					typeFactory.constructCollectionType(List.class, JsonNode.class));
		} catch (IOException e) {
			log.error("Could not get instances");
			e.printStackTrace();
		}
		return instances;

	}

	/**
	 * Publishes a message through MQTT session.
	 *
	 * @param ontology
	 *            Ontology associated with the message
	 * @param jsonData
	 *            Ontology message payload
	 * 
	 */
	public String insertInstance(String ontology, String instance) {
		ObjectMapper mapper = new ObjectMapper();
		HttpUrl urlJoinWithParams = new HttpUrl.Builder().scheme(HttpUrl.parse(this.restServer).scheme())
				.host(HttpUrl.parse(this.restServer).host()).port(HttpUrl.parse(this.restServer).port())
				.addPathSegment(HttpUrl.parse(this.restServer).pathSegments().get(0))
				.addEncodedPathSegments(INSERT_POST).addPathSegment(ontology).build();
		RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), instance);

		Request request = new Request.Builder().url(urlJoinWithParams).post(body)
				.addHeader("Authorization", this.sessionKey).build();
		String idInsert = null;
		try {
			Response response = client.newCall(request).execute();
			idInsert = mapper.readTree(response.body().string()).get("id").asText();
			log.info("Inserted ontology instance, id returned: " + idInsert);
		} catch (IOException e) {
			log.error("Could not insert instance");
			e.printStackTrace();
		}

		return idInsert;

	}

	/**
	 * Closes REST session.
	 *
	 **/
	public void disconnect() {

		Request request = new Request.Builder().url(this.restServer + "/" + LEAVE_GET)
				.addHeader("Authorization", this.sessionKey).get().build();

		try {
			client.newCall(request).execute();
		} catch (IOException e) {
			log.error("Session already expired");
		}
		log.info("Disconnected");
		this.sessionKey = null;

	}

}
