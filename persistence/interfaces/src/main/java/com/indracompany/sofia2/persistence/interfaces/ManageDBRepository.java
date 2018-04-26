/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
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
/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.interfaces;

import java.util.List;
import java.util.Map;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;

public interface ManageDBRepository {

	public final static String BDTR_MONGO_SCHEMA_KEY = "BDTR_MONGO_SCHEMA_KEY";
	public final static String BDTR_RELATIONAL_TABLE_FIELDS = "BDTR_RELATIONAL_TABLE_FIELDS";

	public Map<String, Boolean> getStatusDatabase() throws DBPersistenceException;

	public String createTable4Ontology(String ontology, String schema) throws DBPersistenceException;

	public List<String> getListOfTables() throws DBPersistenceException;

	public List<String> getListOfTables4Ontology(String ontology) throws DBPersistenceException;

	public void removeTable4Ontology(String ontology) throws DBPersistenceException;

	public void createIndex(String ontology, String attribute) throws DBPersistenceException;

	public void createIndex(String ontology, String nameIndex, String attribute) throws DBPersistenceException;

	public void createIndex(String sentence) throws DBPersistenceException;

	public void dropIndex(String ontology, String indexName) throws DBPersistenceException;

	public List<String> getListIndexes(String ontology) throws DBPersistenceException;

	public String getIndexes(String ontology) throws DBPersistenceException;

	public void validateIndexes(String ontology, String schema) throws DBPersistenceException;

	public void exportToJson(String ontology, long startDateMillis) throws DBPersistenceException;

}
