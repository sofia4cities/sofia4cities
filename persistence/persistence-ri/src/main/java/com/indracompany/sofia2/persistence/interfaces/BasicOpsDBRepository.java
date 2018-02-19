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

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.util.BulkWriteResult;

public interface BasicOpsDBRepository {

	public String insert(String ontology, String instance) throws DBPersistenceException;

	public List<BulkWriteResult> insertBulk(String ontology, List<String> instances, boolean order, boolean includeIds)
			throws DBPersistenceException;

	public void updateNative(String ontology, String updateStmt) throws DBPersistenceException;

	public void updateNative(String collection, String query, String data) throws DBPersistenceException;

	public void deleteNative(String collection, String query) throws DBPersistenceException;

	public List<String> queryNative(String ontology, String query) throws DBPersistenceException;

	public List<String> queryNative(String ontology, String query, int offset, int limit) throws DBPersistenceException;

	public String queryNativeAsJson(String ontology, String query) throws DBPersistenceException;

	public String queryNativeAsJson(String ontology, String query, int offset, int limit) throws DBPersistenceException;

	public String findById(String ontology, String objectId) throws DBPersistenceException;

	public String querySQLAsJson(String ontology, String query) throws DBPersistenceException;

	public String querySQLAsTable(String ontology, String query) throws DBPersistenceException;

	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException;

	public String querySQLAsTable(String ontology, String query, int offset) throws DBPersistenceException;

	public String findAllAsJson(String ontology) throws DBPersistenceException;

	public String findAllAsJson(String ontology, int limit) throws DBPersistenceException;

	public List<String> findAll(String ontology) throws DBPersistenceException;

	public List<String> findAll(String ontology, int limit) throws DBPersistenceException;

	public long count(String ontology) throws DBPersistenceException;

	public long countNative(String collectionName, String query) throws DBPersistenceException;

}
