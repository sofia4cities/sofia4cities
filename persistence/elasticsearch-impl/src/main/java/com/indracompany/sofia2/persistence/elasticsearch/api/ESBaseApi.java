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
package com.indracompany.sofia2.persistence.elasticsearch.api;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESBaseApi {

	private TransportClient client;

	@Value("${sofia2.database.elasticsearch.url:localhost}")
	private String host;

	@Value("${sofia2.database.elasticsearch.port:9300}")
	private String port;

	@PostConstruct
	void initializeIt() {
		
		try {
			System.setProperty("es.set.netty.runtime.available.processors", "false");
			Settings settings = Settings.builder().put("client.transport.ignore_cluster_name", true).build();
			client = new PreBuiltTransportClient(settings).addTransportAddress(getTransportAddress());

			log.info(String.format("Settings %s ", client.settings().toString()));
			System.out.println(String.format("Settings %s ", client.settings().toString()));
		} catch (Exception e) {
			log.info(String.format("Cannot Instantiate ElasticSearch Feature due to : %s ", e.getMessage()));
			log.error(String.format("Cannot Instantiate ElasticSearch Feature due to : %s ", e.getMessage()));
			System.out.println(String.format("Cannot Instantiate ElasticSearch Feature due to : %s ", e.getMessage()));
		}
		

	}

	private TransportAddress getTransportAddress() throws UnknownHostException {
		log.info(String.format("Connection details: host: %s. port:%s.", host, port));
		return new TransportAddress(InetAddress.getByName(host), Integer.parseInt(port));
	}

	public boolean isIndexExist(String index) {
		try {
			if (client.admin().indices().prepareExists(index).execute().actionGet().isExists()) {
				return true;
			}
		} catch (Exception exception) {
			log.error("isIndexExist: index error", exception);
		}
		return false;
	}

	public String createIndex(String index) {
		CreateIndexResponse response = client.admin().indices().prepareCreate(index).get();
		return response.index();
	}
	
	public UpdateResponse updateIndex(String index, String type, String id, String jsonData) {
		UpdateResponse response = null;
		try {
			log.info("updateIndex ");
			response = client.prepareUpdate(index, type, id).setDoc(jsonData).execute().get();
			log.info("response " + response);
			return response;
		} catch (Exception e) {
			log.error("UpdateIndex", e);
		}
		return null;
	}

	public boolean createType(String index, String type, String dataMapping) {
		// createIndex(index);
		return prepareIndex(index, type, dataMapping).isAcknowledged();
	}

	public boolean deleteIndex(String index) {

		DeleteIndexResponse response = null;
		boolean res = false;

		if (client.admin().indices().prepareExists(index).get().isExists()) {
			response = client.admin().indices().prepareDelete(index).get();
			res = response.isAcknowledged();
		}
		return res;
	}

	private PutMappingResponse prepareIndex(String index, String type, String dataMapping) {
		return client.admin().indices().preparePutMapping(index).setType(type).setSource(dataMapping, XContentType.JSON)
				.execute().actionGet();
	}

	public TransportClient getClient() {
		if (client==null) {
			System.out.println("CLIENT IS NULL");
			log.error("CLIENT IS NULL");
			return null;
		}
		
		else return client;
	}

}
