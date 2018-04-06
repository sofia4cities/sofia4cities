package com.indracompany.sofia2.persistence.elasticsearch.api;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESUpdateService {

	@Autowired
	ESBaseApi connector;

	public UpdateResponse updateIndex(String index, String type, String id, XContentBuilder jsonData) {
        UpdateResponse response = null;
        try {
            System.out.println("updateIndex ");
            response = connector.getClient().prepareUpdate(index, type, id)
                    .setDoc(jsonData)
                    .execute().get();
            System.out.println("response " + response);
            return response;
        } catch (Exception e) {
            log.error("UpdateIndex", e);
        }
        return null;
    }
	
	 public boolean updateById(String index, String type, String id, String jsonString ){
	       
	        UpdateRequest updateRequest = new UpdateRequest();
	        updateRequest.index(index);
	        updateRequest.type(type);
	        updateRequest.id(id);
	        updateRequest.doc(jsonString);
	        boolean success = true ;
	        try {
	            UpdateResponse updateResponse = connector.getClient().update(updateRequest).get();
	        } catch (Exception e) {
	        	log.error(e.getMessage(),e);
	       
	        } 
	        return success ;
	    }


}