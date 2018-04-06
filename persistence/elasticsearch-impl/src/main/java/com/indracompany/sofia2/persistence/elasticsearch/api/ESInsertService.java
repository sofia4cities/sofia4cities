package com.indracompany.sofia2.persistence.elasticsearch.api;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.io.ByteStreams;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESInsertService {

	@Autowired
	ESBaseApi connector;

    public String load(String index, String type, String jsonDoc ) {
    	log.info(String.format("Loading content: %s into elasticsearch %s %s", jsonDoc, index, type));
    	IndexResponse response= connector.getClient().prepareIndex(index, type).setSource(jsonDoc).get();
    	return response.getId();
    }
    
  
    public boolean loadBulkFromArray(String index, String type, List<String> docs)  throws Exception {
    	log.info(String.format("Ingest content: %s Size of Files into elasticsearch %s %s", docs.size(), index, type));
        BulkRequestBuilder bulkRequest = connector.getClient().prepareBulk();
        docs.forEach(doc -> bulkRequest.add(connector.getClient().prepareIndex(index, type).setSource(doc)));
       
        if(bulkRequest.get().hasFailures()) {
  			throw new Exception(String.format("Failed during bulk load of files "));
  		}
        return bulkRequest.get().hasFailures();
    }
    
    public BulkResponse loadBulkFromFile(String index, File jsonPath) throws Exception {
  		log.info(String.format("Loading file %s into elasticsearch cluster", jsonPath));

  		BulkRequestBuilder bulkBuilder = connector.getClient().prepareBulk();
  		byte[] buffer = ByteStreams.toByteArray(new FileInputStream(jsonPath));
  		bulkBuilder.add(buffer, 0, buffer.length, index, null, XContentType.JSON);
  		BulkResponse response = bulkBuilder.get();

  		if(response.hasFailures()) {
  			throw new Exception(String.format("Failed during bulk load of file %s. failure message: %s", jsonPath, response.buildFailureMessage()));
  		}
  		return response;
  	}
    
    public BulkResponse loadBulkFromFileResource(String index, String jsonPath) throws Exception {
  		log.info(String.format("Loading file %s into elasticsearch cluster", jsonPath));

  		BulkRequestBuilder bulkBuilder = connector.getClient().prepareBulk();
  		byte[] buffer = ByteStreams.toByteArray(new FileInputStream(jsonPath));
  		bulkBuilder.add(buffer, 0, buffer.length, index, null, XContentType.JSON);
  		BulkResponse response = bulkBuilder.get();

  		if(response.hasFailures()) {
  			throw new Exception(String.format("Failed during bulk load of file %s. failure message: %s", jsonPath, response.buildFailureMessage()));
  		}
  		return response;
  	}
      
    public BulkResponse loadBulkFromJson(String index, String content) throws Exception {
  	
    	BulkRequestBuilder bulkBuilder = connector.getClient().prepareBulk();
    	byte[] buffer = content.getBytes();
    	bulkBuilder.add(buffer, 0, buffer.length, index, null, XContentType.JSON);
  		BulkResponse response = bulkBuilder.get();
  		
  		if(response.hasFailures()) {
  			throw new Exception(String.format("Failed during bulk load failure message: %s", response.buildFailureMessage()));
  		}
  		return response;
    }


}