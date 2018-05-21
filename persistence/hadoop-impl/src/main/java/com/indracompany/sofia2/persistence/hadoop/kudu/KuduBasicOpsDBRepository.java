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
package com.indracompany.sofia2.persistence.hadoop.kudu;

import static com.indracompany.sofia2.persistence.hadoop.NameBeanConst.IMPALA_MANAGE_DB_REPO_BEAN_NAME;
import static com.indracompany.sofia2.persistence.hadoop.NameBeanConst.IMPALA_TEMPLATE_JDBC_BEAN_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.hadoop.config.condition.HadoopEnabledCondition;
import com.indracompany.sofia2.persistence.hadoop.util.JsonRelationalHelperKuduImpl;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Conditional(HadoopEnabledCondition.class)
public class KuduBasicOpsDBRepository implements BasicOpsDBRepository {

	@Autowired
	@Qualifier(IMPALA_TEMPLATE_JDBC_BEAN_NAME)
	private JdbcTemplate impalaJdbcTemplate;

	@Autowired
	@Qualifier(IMPALA_MANAGE_DB_REPO_BEAN_NAME)
	private ManageDBRepository manageDBRepository;

	@Autowired
	private JsonRelationalHelperKuduImpl jsonRelationalHelperKuduImpl;

	@Override
	public String insert(String ontology, String schema, String instance) throws DBPersistenceException {
		log.debug("insert instance " + instance + "into ontology " + ontology);

		try {

			String id = UUID.randomUUID().toString();
			String statement = jsonRelationalHelperKuduImpl.getInsertStatement(ontology, schema, instance, id);
			impalaJdbcTemplate.execute(statement);

			// return "{\""+TABLE_COLUMN_OID+"\":\"" + id + "\"}";

		} catch (Exception e) {
			log.error("error insert instance ", e);
			throw new DBPersistenceException(e);
		}
		return null;
	}

	@Override
	public List<BulkWriteResult> insertBulk(String ontology, String schema, List<String> instances, boolean order,
			boolean includeIds) throws DBPersistenceException {

		List<BulkWriteResult> result = new ArrayList<>();

		if (instances != null) {
			for (String instance : instances) {

				BulkWriteResult insertResult = new BulkWriteResult();

				try {

					String id = insert(ontology, schema, instance);

					insertResult.setId(id);
					insertResult.setOk(true);

				} catch (Exception e) {
					log.error("error inserting bulk instance " + instance, e);
					insertResult.setOk(false);
				}

				result.add(insertResult);
			}
		}

		return result;
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
