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

import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESUpdateService {

	@Autowired
	ESBaseApi connector;

	public UpdateResponse updateIndex(String index, String type, String id, String jsonString) throws InterruptedException, ExecutionException {
		UpdateResponse response = null;
        
		log.info("updateIndex ");
		response = connector.getClient().prepareUpdate(index, type, id)
				.setDoc(jsonString)
				.execute().get();
		log.info("response " + response);
		return response;

    }
	
	public UpdateResponse updateById(String index, String type, String id, String jsonString ) throws InterruptedException, ExecutionException{
		log.info("updateById ");
	
		UpdateResponse response = null;
		
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(index);
		updateRequest.type(type);
		updateRequest.id(id);
		updateRequest.doc(jsonString, XContentType.JSON);
	     
		response = connector.getClient().update(updateRequest).get();
		log.info("updateById response " + response);
		return response;

	}
	 
	 
	 public SearchResponse updateByQuery(String index, String type, String jsonScript ) throws InterruptedException, ExecutionException{
		 
		 log.info("updateByQuery ");
		 SearchResponse response = null ;
		 
		 UpdateByQueryRequestBuilder ubqrb = UpdateByQueryAction.INSTANCE.newRequestBuilder(connector.getClient());

		 Script script = new Script(jsonScript);
		 
		 response=  ubqrb.source(index)
		 	.script(script)
		 	.source().setTypes(type)
		 	.execute()
		 	.get();
		
		 log.info("updateByQuery response " + response);
		 return response;
  
	 }
	 
	 public SearchResponse updateByQueryAndFilter(String index, String type, String jsonScript, String jsonFilter ) throws InterruptedException, ExecutionException{
		 
		 log.info("updateByQuery ");
		 SearchResponse response = null ;
		 
		 UpdateByQueryRequestBuilder ubqrb = UpdateByQueryAction.INSTANCE.newRequestBuilder(connector.getClient());

		 Script script = new Script(jsonScript);
		 
		 WrapperQueryBuilder build = QueryBuilders.wrapperQuery(jsonFilter);
		 response=  ubqrb.source(index)
				 .script(script)
				 .filter(build)
				 .source().setTypes(type)
				 .execute()
				 .get();
		
		 log.info("updateByQuery response " + response);
		 return response;
  
	 }

	
}