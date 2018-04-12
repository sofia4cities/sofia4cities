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
package com.indracompany.sofia2.persistence.elasticsearch;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESCountService;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESDataService;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESDeleteService;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESInsertService;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESUpdateService;
import com.indracompany.sofia2.persistence.elasticsearch.sql.connector.ElasticSearchSQLDbHttpConnector;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Component("ElasticSearchBasicOpsDBRepository")
@Scope("prototype")
@Lazy
@Slf4j
public class ElasticSearchBasicOpsDBRepository implements BasicOpsDBRepository {

	@Autowired
	private ESCountService eSCountService;
	@Autowired
	private ESDataService eSDataService;
	@Autowired
	private ESDeleteService eSDeleteService;
	@Autowired
	private ESInsertService eSInsertService;
	@Autowired
	private ESUpdateService eSUpdateService;
	@Autowired
	private ElasticSearchSQLDbHttpConnector elasticSearchSQLDbHttpConnector;
	
	
	/*@Value("${sofia2.database.elasticsearch.database:es_sofia2_s4c}")
	@Getter
	@Setter
	private String database;
	*/
	
	@Override
	public String insert(String ontology, String instance) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		log.info(String.format("ElasticSearchBasicOpsDBRepository : Loading content: %s into elasticsearch  %s", instance, ontology));
		String output;
		try {
			//output = eSInsertService.load(database, ontology, instance);
			output = eSInsertService.load(ontology, ontology, instance);
		} catch (Exception e) {
			throw new DBPersistenceException("Error inserting instance :"+instance+" into :"+ontology,e);
		}
		return output;
	}

	@Override
	public List<BulkWriteResult> insertBulk(String ontology, List<String> instances, boolean order, boolean includeIds)
			throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		throw new DBPersistenceException("Not Implemented Already");
	}

	@Override
	public long updateNative(String ontology, String updateStmt) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		log.info(String.format("ElasticSearchBasicOpsDBRepository :Update Native"));
		SearchResponse output=null;
		try {
			//output = eSUpdateService.updateByQuery(database, ontology, updateStmt);
			output = eSUpdateService.updateByQuery(ontology, ontology, updateStmt);
		} catch (InterruptedException | ExecutionException e) {
			throw new DBPersistenceException("Error in operation ES updateNative : "+e.getMessage(),e);
		}
		return output.getHits().totalHits;
	}

	@Override
	public long updateNative(String collection, String query, String data) throws DBPersistenceException {
		collection=collection.toLowerCase();
		log.info(String.format("ElasticSearchBasicOpsDBRepository :Update Native"));
		SearchResponse output=null;
		try {
			//output = eSUpdateService.updateByQueryAndFilter(database, collection, data, query);
			output = eSUpdateService.updateByQueryAndFilter(collection, collection, data, query);
		} catch (InterruptedException | ExecutionException e) {
			throw new DBPersistenceException("Error in operation ES updateNative : "+e.getMessage(),e);
		}
		return output.getHits().totalHits;
	}

	@Override
	public long deleteNative(String collection, String query) throws DBPersistenceException {
		throw new DBPersistenceException("Not Implemented Already");
	}

	@Override
	public List<String> queryNative(String ontology, String query) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		log.info(String.format("ElasticSearchBasicOpsDBRepository : queryNative: %s into elasticsearch %s %s", query, ontology));
		return eSDataService.findQueryData(query, ontology);
	}

	@Override
	public List<String> queryNative(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		log.info(String.format("ElasticSearchBasicOpsDBRepository : queryNative: %s into elasticsearch %s %s", query, ontology));
		return eSDataService.findQueryData(query, ontology);
	}

	@Override
	public String queryNativeAsJson(String ontology, String query) throws DBPersistenceException {
		log.info(String.format("ElasticSearchBasicOpsDBRepository : queryNativeAsJson: %s into elasticsearch %s %s", query, ontology));
		return queryNative(ontology,query).get(0);
	}

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		log.info(String.format("ElasticSearchBasicOpsDBRepository : queryNativeAsJson: %s into elasticsearch %s %s", query, ontology));
		return queryNative(ontology,query).get(0);
	}

	@Override
	public String findById(String ontology, String objectId) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		GetResponse getResponse =  eSDataService.findByIndex(ontology, ontology, objectId);
		//GetResponse getResponse =  eSDataService.findByIndex(database, ontology, objectId);
		String output = getResponse.getSourceAsString();
		return output;
		
	}

	@Override
	public String querySQLAsJson(String ontology, String query) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		return elasticSearchSQLDbHttpConnector.queryAsJson(query, 0);
	}

	@Override
	public String querySQLAsTable(String ontology, String query) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		throw new DBPersistenceException("Not Implemented Already");
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		return elasticSearchSQLDbHttpConnector.queryAsJson(query, offset);
	}

	@Override
	public String querySQLAsTable(String ontology, String query, int offset) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		throw new DBPersistenceException("Not Implemented Already");
	}

	@Override
	public String findAllAsJson(String ontology) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		//List<String> output = eSDataService.findAllByType(ontology, database);
		List<String> output = eSDataService.findAllByType(ontology, ontology);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(output);
		return json;
	}

	@Override
	public String findAllAsJson(String ontology, int limit) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		throw new DBPersistenceException("Not Implemented Already");
	}

	@Override
	public List<String> findAll(String ontology) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		//List<String> output = eSDataService.findAllByType(ontology, database);
		List<String> output = eSDataService.findAllByType(ontology, ontology);
		return output;
	}

	@Override
	public List<String> findAll(String ontology, int limit) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		throw new DBPersistenceException("Not Implemented Already");
	}

	@Override
	public long count(String ontology) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		//return eSCountService.getMatchAllQueryCountByType(ontology,database);
		return eSCountService.getMatchAllQueryCountByType(ontology,ontology);
	}

	@Override
	public long delete(String ontology) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		long count = count(ontology);
		//boolean all = eSDeleteService.deleteAll(database, ontology);
		boolean all = eSDeleteService.deleteAll(ontology, ontology);
		if (all) return count;
		else return -1;
	}

	@Override
	public long countNative(String collectionName, String query) throws DBPersistenceException {
		throw new DBPersistenceException("Not Implemented Already");
	}

	@Override
	public long deleteNativeById(String ontologyName, String objectId) throws DBPersistenceException {
		ontologyName=ontologyName.toLowerCase();
		//boolean all = eSDeleteService.deleteById(database, ontologyName, objectId);
		boolean all = eSDeleteService.deleteById(ontologyName, ontologyName, objectId);
		if (all) return 1;
		else return -1;
	}

	@Override
	public long updateNativeByObjectIdAndBodyData(String ontologyName, String objectId, String body)
			throws DBPersistenceException {
		ontologyName=ontologyName.toLowerCase();
		UpdateResponse response=null;
		 try {
			// response = eSUpdateService.updateById(database,ontologyName,objectId,body);
			 response = eSUpdateService.updateById(ontologyName,ontologyName,objectId,body);
		} catch (InterruptedException | ExecutionException e) {
			throw new DBPersistenceException("Error in Update Native:"+e.getMessage(),e);
		}
		 
		 return 1;
	}

}
