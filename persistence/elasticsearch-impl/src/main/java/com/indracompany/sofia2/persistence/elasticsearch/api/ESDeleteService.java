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

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/***
 * add delete by query plugin to Elastisearch $ bin/plugin install
 * delete-by-query
 */
@Service
@Slf4j
public class ESDeleteService {

	@Autowired
	ESBaseApi connector;

	public boolean deleteById(String index, String type, String id) {
		try {
			DeleteResponse deleteResponse = connector.getClient().prepareDelete(index, type, id).get();
			if (deleteResponse != null) {
				deleteResponse.status();
				deleteResponse.toString();
				log.info("Document has been deleted...");
				return true;
			}
		} catch (Exception ex) {
			log.error("Exception occurred while delete Document : " + ex, ex);
		}
		return false;
	}

	public boolean deleteAll(String index, String type) {
		BulkRequestBuilder bulkRequest = connector.getClient().prepareBulk();
		SearchResponse response = connector.getClient().prepareSearch(index).setTypes(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setExplain(true).execute().actionGet();
		for (SearchHit hit : response.getHits()) {
			String id = hit.getId();
			bulkRequest.add(connector.getClient().prepareDelete(index, type, id).request());
		}
		BulkResponse bulkResponse = bulkRequest.get();	
		log.info("Documents have been deleted...");
		return bulkResponse.hasFailures() == true ? false : true;
	}
	
	

	public long deleteByQuery(String index , String jsonQueryString) {
		WrapperQueryBuilder build = QueryBuilders.wrapperQuery(jsonQueryString);
		BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(connector.getClient())
			    .filter(build) 
			    .source(index)                                  
			    .get();                                             
		long deleted = response.getDeleted();    
		return deleted;
	}
}