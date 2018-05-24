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
package com.indracompany.sofia2.persistence.hadoop.missing;

import static com.indracompany.sofia2.persistence.hadoop.common.HadoopMessages.NOT_SUPPORTED;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;

@Repository
public class DefaultBasicOpsDBRepository implements BasicOpsDBRepository {

	@Override
	public String insert(String ontology, String schema, String instance) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public List<BulkWriteResult> insertBulk(String ontology, String schema, List<String> instances, boolean order,
			boolean includeIds) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public long updateNative(String ontology, String updateStmt) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public long updateNative(String collection, String query, String data) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public long deleteNative(String collection, String query) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public List<String> queryNative(String ontology, String query) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public List<String> queryNative(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public String queryNativeAsJson(String ontology, String query) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public String findById(String ontology, String objectId) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public String querySQLAsJson(String ontology, String query) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public String querySQLAsTable(String ontology, String query) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public String querySQLAsTable(String ontology, String query, int offset) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public String findAllAsJson(String ontology) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public String findAllAsJson(String ontology, int limit) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public List<String> findAll(String ontology) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public List<String> findAll(String ontology, int limit) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public long count(String ontology) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public long delete(String ontology) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public long countNative(String collectionName, String query) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public long deleteNativeById(String ontologyName, String objectId) throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

	@Override
	public long updateNativeByObjectIdAndBodyData(String ontologyName, String objectId, String body)
			throws DBPersistenceException {
		throw new DBPersistenceException(NOT_SUPPORTED);
	}

}
