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
	void initializeIt() throws UnknownHostException {
		Settings settings = Settings.builder().put("client.transport.ignore_cluster_name", true).build();
		client = new PreBuiltTransportClient(settings).addTransportAddress(getTransportAddress());
		log.info(String.format("Settings %s ", client.settings().toString()));

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
		return client;
	}

}
