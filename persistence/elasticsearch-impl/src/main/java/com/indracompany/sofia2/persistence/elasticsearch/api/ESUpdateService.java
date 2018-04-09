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