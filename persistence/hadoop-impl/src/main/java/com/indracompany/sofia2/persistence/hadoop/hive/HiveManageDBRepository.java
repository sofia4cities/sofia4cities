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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.indracompany.sofia2.persistence.common.DescribeColumnData;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.hadoop.NameBeanConst;
import com.indracompany.sofia2.persistence.hadoop.common.CommonQuery;
import com.indracompany.sofia2.persistence.hadoop.config.HdfsConfiguration;
import com.indracompany.sofia2.persistence.hadoop.hive.table.HiveTable;
import com.indracompany.sofia2.persistence.hadoop.hive.table.HiveTableGenerator;
import com.indracompany.sofia2.persistence.hadoop.impala.ImpalaManageDBRepository;
import com.indracompany.sofia2.persistence.hadoop.rowmapper.HiveDescribeColumnRowMapper;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@ConditionalOnBean(name = { NameBeanConst.HIVE_TEMPLATE_JDBC_BEAN_NAME, NameBeanConst.IMPALA_TEMPLATE_JDBC_BEAN_NAME })
public class HiveManageDBRepository implements ManageDBRepository {

	@Autowired
	@Qualifier(NameBeanConst.HIVE_TEMPLATE_JDBC_BEAN_NAME)
	private JdbcTemplate hiveJdbcTemplate;

	@Autowired
	private ImpalaManageDBRepository impalaManageDBRepository;

	@Autowired
	private HiveTableGenerator hiveTableGenerator;

	@Autowired
	private HdfsConfiguration hdfsConfiguration;

	@Override
	public Map<String, Boolean> getStatusDatabase() throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createTable4Ontology(String ontology, String schema) throws DBPersistenceException {
		try {
			log.debug("create hive table for ontology " + ontology);
			HiveTable table = hiveTableGenerator.buildHiveTable(ontology, schema,
					hdfsConfiguration.getAbsolutePath(hdfsConfiguration.getOntologiesFolder(), ontology));
			hiveJdbcTemplate.execute(table.build());
			log.debug("hive table created successfully");
			impalaManageDBRepository.invalidateMetadata(table.getName());
			log.debug("impala invalidated metadata");
		} catch (DataAccessException e) {
			log.error("error creating hive table for ontology " + ontology, e);
			throw new DBPersistenceException(e);
		}
		return ontology;
	}

	@Override
	public List<DescribeColumnData> describeTable(String name) {

		List<DescribeColumnData> descriptors = new ArrayList<>();

		try {

			String sql = String.format(CommonQuery.DESCRIBE_TABLE, name);
			descriptors = hiveJdbcTemplate.query(sql, new HiveDescribeColumnRowMapper());

		} catch (DataAccessException e) {
			log.error("error describe hive table " + name, e);
			throw new DBPersistenceException(e);
		}

		return descriptors;
	}

	@Override
	public List<String> getListOfTables() throws DBPersistenceException {

		List<String> tables = null;

		try {
			tables = hiveJdbcTemplate.queryForList(CommonQuery.LIST_TABLES, String.class);
		} catch (DataAccessException e) {
			log.error("error getting all hive tables ", e);
			throw new DBPersistenceException(e);
		}

		return tables;
	}

	@Override
	public List<String> getListOfTables4Ontology(String ontology) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeTable4Ontology(String ontology) throws DBPersistenceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createIndex(String ontology, String attribute) throws DBPersistenceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createIndex(String ontology, String nameIndex, String attribute) throws DBPersistenceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createIndex(String sentence) throws DBPersistenceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropIndex(String ontology, String indexName) throws DBPersistenceException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getListIndexes(String ontology) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIndexes(String ontology) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validateIndexes(String ontology, String schema) throws DBPersistenceException {
		// TODO Auto-generated method stub

	}

	@Override
	public String exportToJson(String ontology, long startDateMillis) throws DBPersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long deleteAfterExport(String ontology, String query) {
		// TODO Auto-generated method stub
		return 0;
	}

}
