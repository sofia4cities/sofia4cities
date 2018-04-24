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

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.elasticsearch.api.ESCountService;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESDataService;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESDeleteService;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESInsertService;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESNativeService;
import com.indracompany.sofia2.persistence.elasticsearch.api.ESUpdateService;
import com.indracompany.sofia2.persistence.elasticsearch.sql.connector.ElasticSearchSQLDbHttpConnector;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;

import lombok.extern.slf4j.Slf4j;


@Component("ElasticSearchBasicOpsDBRepository")
@Scope("prototype")
@Lazy
@Slf4j
public class ElasticSearchBasicOpsDBRepository implements BasicOpsDBRepository {

	private static final String NOT_IMPLEMENTED_ALREADY = "Not Implemented Already";
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
	
	@Autowired
	private ESNativeService eSNativeService;
	
	
	@Override
	public String insert(String ontology, String instance) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		log.info(String.format("ElasticSearchBasicOpsDBRepository : Loading content: %s into elasticsearch  %s", instance, ontology));
		List<BulkWriteResult> output=null;
		try {
			List<String> instances = new ArrayList<String>();
			instances.add(instance);
			output = eSInsertService.load(ontology, ontology, instances);
			return output.get(0).getId();
		} catch (Exception e) {
			throw new DBPersistenceException("Error inserting instance :"+instance+" into :"+ontology,e);
		}
		
	}

	@Override
	public List<BulkWriteResult> insertBulk(String ontology, List<String> instances, boolean order, boolean includeIds)
			throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		try {
			return eSInsertService.load(ontology, ontology, instances);
		} catch (Exception e) {
			throw new DBPersistenceException("Error inserting instances :"+instances.size()+" into :"+ontology,e);
		}
	}

	@Override
	@Deprecated
	public long updateNative(String ontology, String updateStmt) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		log.info(String.format("ElasticSearchBasicOpsDBRepository :Update Native"));
		SearchResponse output=null;
		try {
			output = eSNativeService.updateByQuery(ontology, ontology, updateStmt);
		} catch (Exception e) {
			throw new DBPersistenceException("Error in operation ES updateNative : "+e.getMessage(),e);
		}
		return output.getHits().totalHits;
	}

	@Override
	@Deprecated
	public long updateNative(String collection, String query, String data) throws DBPersistenceException {
		collection=collection.toLowerCase();
		SearchResponse output=null;
		try {
			output = eSNativeService.updateByQueryAndFilter(collection, collection, data, query);
		} catch (Exception e) {
			throw new DBPersistenceException("Error in operation ES updateNative : "+e.getMessage(),e);
		}
		return output.getHits().totalHits;
	}

	@Override
	public long deleteNative(String ontology, String query) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		return eSDeleteService.deleteByQuery(ontology, ontology, query);
	}

	@Override
	public List<String> queryNative(String ontology, String query) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		return eSDataService.findQueryData(query, ontology);
	}

	@Override
	public List<String> queryNative(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		return eSDataService.findAllByType(ontology, query, offset, limit);
	}

	@Override
	public String queryNativeAsJson(String ontology, String query) throws DBPersistenceException {
		return eSDataService.findQueryDataAsJson(query, ontology);
	}

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		String output = eSDataService.findAllByTypeAsJson(ontology, offset,limit);
		return output;
	}

	@Override
	public String findById(String ontology, String objectId) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		String getResponse =  eSDataService.findByIndex(ontology, ontology, objectId);
		return getResponse;
		
	}

	@Override
	public String querySQLAsJson(String ontology, String query) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		return elasticSearchSQLDbHttpConnector.queryAsJson(query, 200);
	}

	//TODO IMPLEMENT
	@Override
	public String querySQLAsTable(String ontology, String query) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		return elasticSearchSQLDbHttpConnector.queryAsJson(query, offset);
	}

	//TODO IMPLEMENT
	@Override
	public String querySQLAsTable(String ontology, String query, int offset) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		throw new DBPersistenceException(NOT_IMPLEMENTED_ALREADY);
	}

	@Override
	public String findAllAsJson(String ontology) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		String output = eSDataService.findAllByTypeAsJson(ontology,200);
		return output;
	}

	@Override
	public String findAllAsJson(String ontology, int limit) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		String output = eSDataService.findAllByTypeAsJson(ontology,limit);
		return output;
	}

	@Override
	public List<String> findAll(String ontology) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		List<String> output = eSDataService.findAllByType(ontology);
		return output;
	}

	@Override
	public List<String> findAll(String ontology, int limit) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		List<String> output = eSDataService.findAllByType(ontology,limit);
		return output;
	}

	@Override
	public long count(String ontology) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		return eSCountService.getMatchAllQueryCountByType(ontology,ontology);
	}

	@Override
	public long delete(String ontology) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		long count = count(ontology);
		boolean all = eSDeleteService.deleteAll(ontology, ontology);
		if (all) return count;
		else return -1;
	}

	@Override
	public long countNative(String ontology, String jsonQueryString) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		return eSCountService.getQueryCount(jsonQueryString, ontology);
	}

	@Override
	public long deleteNativeById(String ontologyName, String objectId) throws DBPersistenceException {
		ontologyName=ontologyName.toLowerCase();
		boolean all = eSDeleteService.deleteById(ontologyName, ontologyName, objectId);
		if (all) return 1;
		else return -1;
	}

	@Override
	public long updateNativeByObjectIdAndBodyData(String ontologyName, String objectId, String body)
			throws DBPersistenceException {
		ontologyName=ontologyName.toLowerCase();
		
		 try {
			 boolean  response = eSUpdateService.updateIndex(ontologyName,ontologyName,objectId,body);
			 
			 if (response==true) return 1;
			 else return -1;
		} catch (Exception e) {
			throw new DBPersistenceException("Error in Update Native:"+e.getMessage(),e);
		}

	}
}
