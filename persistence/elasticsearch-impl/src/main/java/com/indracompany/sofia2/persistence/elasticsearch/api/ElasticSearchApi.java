package com.indracompany.sofia2.persistence.elasticsearch.api;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.io.ByteStreams;


@Service
public class ElasticSearchApi {
	
	private TransportClient client;
	
	@Value("${sofia2.database.elasticsearch.url:http://localhost}")
	private String host;
	
	@Value("${sofia2.database.elasticsearch.url:9300}")
	private String port;
	
	@PostConstruct
	void initializeIt() throws UnknownHostException {
		Settings settings = Settings.builder().put("client.transport.ignore_cluster_name",true).build();
		client = new PreBuiltTransportClient(settings).addTransportAddress(getTransportAddress());
	}
	
	private TransportAddress getTransportAddress() throws UnknownHostException {
		System.out.println(String.format("Connection details: host: %s. port:%s.", host, port));
		return new TransportAddress(InetAddress.getByName(host), Integer.parseInt(port));
	}
	
	
	public String createIndex(String index) {
		deleteIndex(index);
        CreateIndexResponse response = client.admin().indices().prepareCreate(index).get();
        return response.index();
	}
	
	public PutMappingResponse prepareIndex(String index, String type, String dataMapping) {   
		return client.admin().indices().preparePutMapping(index).setType(type).setSource(dataMapping, XContentType.JSON).execute().actionGet();
	}
	
	public boolean createTable(String index, String type, String dataMapping) {
		createIndex(index);
		return prepareIndex(index, type, dataMapping).isAcknowledged();
	}

    public boolean deleteIndex(String index) {
    	
    	DeleteIndexResponse response=null;
    	boolean res = false;
    	
        if(client.admin().indices().prepareExists(index).get().isExists()){
        	response = client.admin().indices().prepareDelete(index).get();
        	res = response.isAcknowledged();
        }
        return res;
    }
    
    public BulkResponse loadBulkFromFile(String jsonPath, String defaultIndex) throws Exception {
		System.out.println(String.format("Loading file %s into elasticsearch cluster", jsonPath));

		BulkRequestBuilder bulkBuilder = client.prepareBulk();
		byte[] buffer = ByteStreams.toByteArray(new FileInputStream(jsonPath));
		bulkBuilder.add(buffer, 0, buffer.length, defaultIndex, null, XContentType.JSON);
		BulkResponse response = bulkBuilder.get();

		if(response.hasFailures()) {
			throw new Exception(String.format("Failed during bulk load of file %s. failure message: %s", jsonPath, response.buildFailureMessage()));
		}
		return response;
	}
    
    public BulkResponse loadBulk(String content, String defaultIndex) throws Exception {
	
		BulkRequestBuilder bulkBuilder = client.prepareBulk();
		byte[] buffer = content.getBytes();
		bulkBuilder.add(buffer, 0, buffer.length, defaultIndex, null, XContentType.JSON);
		BulkResponse response = bulkBuilder.get();

		if(response.hasFailures()) {
			throw new Exception(String.format("Failed during bulk load failure message: %s", response.buildFailureMessage()));
		}
		return response;
	}

	public TransportClient getClient() {
		return client;
	}
	
	

}
