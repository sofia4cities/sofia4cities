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
package com.indracompany.sofia2.persistence.hadoop.hive;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.hadoop.NameBeanConst;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;

@Repository
@ConditionalOnBean(name = NameBeanConst.HIVE_TEMPLATE_JDBC_BEAN_NAME)
public class HiveBasicOpsDBRepository implements BasicOpsDBRepository {

	@Override
	public String insert(String ontology, String schema, String instance) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BulkWriteResult> insertBulk(String ontology, String schema, List<String> instances, boolean order,
			boolean includeIds) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long updateNative(String ontology, String updateStmt) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long updateNative(String collection, String query, String data) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long deleteNative(String collection, String query) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> queryNative(String ontology, String query) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> queryNative(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String queryNativeAsJson(String ontology, String query) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findById(String ontology, String objectId) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String querySQLAsJson(String ontology, String query) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String querySQLAsTable(String ontology, String query) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String querySQLAsTable(String ontology, String query, int offset) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findAllAsJson(String ontology) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findAllAsJson(String ontology, int limit) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findAll(String ontology) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findAll(String ontology, int limit) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count(String ontology) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long delete(String ontology) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long countNative(String collectionName, String query) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long deleteNativeById(String ontologyName, String objectId) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long updateNativeByObjectIdAndBodyData(String ontologyName, String objectId, String body)
			throws DBPersistenceException {
		// TODO Auto-generated method stub
		return 0;
	}

}
