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

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.DocumentResult;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESDeleteService {

	@Autowired
	ESBaseApi connector;

	public boolean deleteById(String index, String type, String id) {
		try {
			DocumentResult d = connector.getHttpClient().execute(new Delete.Builder(id)
		            .index(index)
		            .type(type)
		            .build());
			
			log.info("Document has been deleted..."+id+" "+ d.isSucceeded());
			
			return d.isSucceeded();
			
		} catch (Exception ex) {
			log.error("Exception occurred while delete Document : " + ex, ex);
		}
		return false;
	}
	
	public boolean deleteAll(String index, String type) {
		long result = deleteByQuery(index,type,connector.queryAll);
		if (result==-1) return false;
		else return true;
	}
	
	public long deleteByQuery(String index,String type , String jsonQueryString) {
		 DeleteByQuery deleteByQuery = new DeleteByQuery.Builder(jsonQueryString)
	                .addIndex(index)
	                .addType(type)
	                .build();
		 try {
			JestResult result = connector.getHttpClient().execute(deleteByQuery);
			if (result.isSucceeded())
				return result.getJsonObject().getAsJsonObject("_indices").getAsJsonObject(index).get("deleted").getAsLong();
			else return -1;
		} catch (IOException e) {
			log.error("Exception occurred while delete Document : " + e, e);
			return -1;
		}
	}
}