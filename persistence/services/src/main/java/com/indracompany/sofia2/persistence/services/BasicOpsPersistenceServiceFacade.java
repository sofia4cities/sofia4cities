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
package com.indracompany.sofia2.persistence.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.factory.BasicOpsDBRepositoryFactory;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BasicOpsPersistenceServiceFacade implements BasicOpsDBRepository, NativeBasicOpsRepository {

	@Autowired
	private BasicOpsDBRepositoryFactory basicOpsDBRepositoryFactory;

	@Autowired
	private OntologyRepository ontologyRepository;

	public RtdbDatasource getOntologyDataSource(String ontologyId) {
		Ontology ds = ontologyRepository.findByIdentification(ontologyId);
		return ds.getRtdbDatasource();
	}

	public Ontology getOntology(String ontologyId) {
		return ontologyRepository.findByIdentification(ontologyId);
	}

	public long updateNative(RtdbDatasource dataSource, String collection, String query, String data)
			throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(dataSource).updateNative(collection, query, data);
	}

	public long deleteNative(RtdbDatasource dataSource, String collection, String query) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(dataSource).deleteNative(collection, query);
	}

	public long countNative(RtdbDatasource dataSource, String collectionName, String query)
			throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(dataSource).countNative(collectionName, query);
	}

	@Override
	public String insert(String ontology, String schema, String instance) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).insert(ontology, schema, instance);
	}

	@Override
	public List<BulkWriteResult> insertBulk(String ontology, String schema, List<String> instances, boolean order,
			boolean includeIds) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).insertBulk(ontology, schema, instances, order,
				includeIds);
	}

	@Override
	public long updateNative(String ontology, String updateStmt) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).updateNative(ontology, updateStmt);
	}

	@Override
	public List<String> queryNative(String ontology, String query) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).queryNative(ontology, query);
	}

	@Override
	public List<String> queryNative(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).queryNative(ontology, query, offset, limit);
	}

	@Override
	public String queryNativeAsJson(String ontology, String query) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).queryNativeAsJson(ontology, query);
	}

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).queryNativeAsJson(ontology, query, offset, limit);
	}

	@Override
	public String findById(String ontology, String objectId) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).findById(ontology, objectId);
	}

	@Override
	public String querySQLAsJson(String ontology, String query) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).querySQLAsJson(ontology, query);
	}

	@Override
	public String querySQLAsTable(String ontology, String query) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).querySQLAsTable(ontology, query);
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).querySQLAsJson(ontology, query, offset);
	}

	@Override
	public String querySQLAsTable(String ontology, String query, int offset) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).querySQLAsTable(ontology, query, offset);
	}

	@Override
	public String findAllAsJson(String ontology) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).findAllAsJson(ontology);
	}

	@Override
	public String findAllAsJson(String ontology, int limit) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).findAllAsJson(ontology, limit);
	}

	@Override
	public List<String> findAll(String ontology) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).findAll(ontology);
	}

	@Override
	public List<String> findAll(String ontology, int limit) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).findAll(ontology, limit);
	}

	@Override
	public long count(String ontology) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).count(ontology);
	}

	@Override
	public long delete(String ontology) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontology).delete(ontology);
	}

	@Override
	public long deleteNativeById(String ontologyName, String objectId) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontologyName).deleteNativeById(ontologyName, objectId);
	}

	@Override
	public long updateNativeByObjectIdAndBodyData(String ontologyName, String objectId, String body)
			throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(ontologyName).updateNativeByObjectIdAndBodyData(ontologyName,
				objectId, body);
	}

	@Override
	public long updateNative(String collection, String query, String data) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(collection).updateNative(collection, query, data);
	}

	@Override
	public long deleteNative(String collection, String query) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(collection).deleteNative(collection, query);
	}

	@Override
	public long countNative(String collectionName, String query) throws DBPersistenceException {
		return basicOpsDBRepositoryFactory.getInstance(collectionName).countNative(collectionName, query);
	}

}
