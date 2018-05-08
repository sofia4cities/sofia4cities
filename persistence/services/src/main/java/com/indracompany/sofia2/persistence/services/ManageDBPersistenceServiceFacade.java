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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.persistence.common.DescribeColumnData;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.factory.ManageDBRepositoryFactory;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ManageDBPersistenceServiceFacade implements ManageDBRepository, NativeManageDBRepository {

	@Autowired
	private ManageDBRepositoryFactory manageDBRepositoryFactory;

	public Map<String, Boolean> getStatusDatabase(RtdbDatasource dataSource) throws DBPersistenceException {
		return manageDBRepositoryFactory.getInstance(dataSource).getStatusDatabase();
	}

	public List<String> getListOfTables(RtdbDatasource dataSource) throws DBPersistenceException {
		return manageDBRepositoryFactory.getInstance(dataSource).getListOfTables();
	}

	public void createIndex(RtdbDatasource dataSource, String sentence) throws DBPersistenceException {
		manageDBRepositoryFactory.getInstance(dataSource).createIndex(sentence);
	}

	public List<DescribeColumnData> describeTable(RtdbDatasource dataSource, String name) {
		return manageDBRepositoryFactory.getInstance(dataSource).describeTable(name);
	}

	@Override
	public String createTable4Ontology(String ontology, String schema) throws DBPersistenceException {
		return manageDBRepositoryFactory.getInstance(ontology).createTable4Ontology(ontology, schema);
	}

	@Override
	public List<String> getListOfTables4Ontology(String ontology) throws DBPersistenceException {
		return manageDBRepositoryFactory.getInstance(ontology).getListOfTables4Ontology(ontology);
	}

	@Override
	public void removeTable4Ontology(String ontology) throws DBPersistenceException {
		manageDBRepositoryFactory.getInstance(ontology).removeTable4Ontology(ontology);

	}

	@Override
	public void createIndex(String ontology, String attribute) throws DBPersistenceException {
		manageDBRepositoryFactory.getInstance(ontology).createIndex(ontology, attribute);

	}

	@Override
	public void createIndex(String ontology, String nameIndex, String attribute) throws DBPersistenceException {
		manageDBRepositoryFactory.getInstance(ontology).createIndex(ontology, nameIndex, attribute);

	}

	@Override
	public void dropIndex(String ontology, String indexName) throws DBPersistenceException {
		manageDBRepositoryFactory.getInstance(ontology).dropIndex(ontology, indexName);

	}

	@Override
	public List<String> getListIndexes(String ontology) throws DBPersistenceException {
		return manageDBRepositoryFactory.getInstance(ontology).getListIndexes(ontology);
	}

	@Override
	public String getIndexes(String ontology) throws DBPersistenceException {
		return manageDBRepositoryFactory.getInstance(ontology).getIndexes(ontology);
	}

	@Override
	public void validateIndexes(String ontology, String schema) throws DBPersistenceException {
		manageDBRepositoryFactory.getInstance(ontology).validateIndexes(ontology, schema);

	}

	@Override
	public Map<String, Boolean> getStatusDatabase() throws DBPersistenceException {
		throw new DBPersistenceException(
				"Method not executable, please use same definition with RtdbDatasource parameter");
	}

	@Override
	public List<String> getListOfTables() throws DBPersistenceException {
		throw new DBPersistenceException(
				"Method not executable, please use same definition with RtdbDatasource parameter");
	}

	@Override
	public void createIndex(String sentence) throws DBPersistenceException {
		throw new DBPersistenceException(
				"Method not executable, please use same definition with RtdbDatasource parameter");
	}

	@Override
	public String exportToJson(String ontology, long startDateMillis) {
		throw new DBPersistenceException(
				"Method not executable, please use same definition with RtdbDatasource parameter");

	}

	public String exportToJson(RtdbDatasource rtdbDatasource, String ontology, long startDateMillis)
			throws DBPersistenceException {
		return manageDBRepositoryFactory.getInstance(rtdbDatasource).exportToJson(ontology, startDateMillis);

	}

	@Override
	public long deleteAfterExport(String ontology, String query) {
		throw new DBPersistenceException(
				"Method not executable, please use same definition with RtdbDatasource parameter");
	}

	public long deleteAfterExport(RtdbDatasource rtdbDatasource, String ontology, String query) {
		return manageDBRepositoryFactory.getInstance(rtdbDatasource).deleteAfterExport(ontology, query);
	}

	@Override
	public List<DescribeColumnData> describeTable(String name) {

		throw new DBPersistenceException(
				"Method not executable, please use same definition with RtdbDatasource parameter");

	}

}
