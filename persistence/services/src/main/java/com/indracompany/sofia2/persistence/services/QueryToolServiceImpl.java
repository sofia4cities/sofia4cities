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
package com.indracompany.sofia2.persistence.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QueryToolServiceImpl implements QueryToolService {

	@Autowired
	BasicOpsDBRepository basicOps = null;

	@Autowired
	ManageDBRepository manageOps = null;

	@Autowired
	OntologyService ontologyService = null;

	private void hasUserPermission(String user, String ontology) throws Exception {
		if (!ontologyService.hasUserPermissionForQuery(user, ontology))
			throw new Exception("User:" + user + " has nos permission to query ontology " + ontology);
	}

	private void checkQueryIs4Ontology(String ontology, String query, boolean sql) throws Exception {
		if (sql == true) {
			if (query.toLowerCase().indexOf("from " + ontology.toLowerCase()) == -1)
				throw new Exception("The query " + query + " is not for the ontology selected:" + ontology);

		} else {
			if (query.indexOf("db.") == -1)
				return;
			if (query.indexOf("." + ontology + ".") == -1)
				throw new DBPersistenceException(
						"The query " + query + " is not for the ontology selected:" + ontology);
		}
	}

	@Override
	public String queryNativeAsJson(String user, String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		try {
			checkQueryIs4Ontology(ontology, query, false);
			hasUserPermission(user, ontology);
			return basicOps.queryNativeAsJson(ontology, query, offset, limit);
		} catch (Exception e) {
			log.error("Error queryNativeAsJson:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public String queryNativeAsJson(String user, String ontology, String query) throws DBPersistenceException {
		try {
			checkQueryIs4Ontology(ontology, query, false);
			hasUserPermission(user, ontology);
			if (query.indexOf(".createIndex(") != -1) {
				manageOps.createIndex(query);
				return "Created index indicated in the query:" + query;
			} else if (query.indexOf(".dropIndex(") != -1) {
				query = query.substring(query.indexOf(".dropIndex(") + 11, query.length());
				query = query.replace("\"", "");
				query = query.replace("'", "");
				String indexName = query.substring(0, query.indexOf(")"));
				manageOps.dropIndex(ontology, indexName);
				return "Drop index indicated in the query:" + query;
			} else if (query.indexOf(".getIndexes()") != -1) {
				return manageOps.getIndexes(ontology);
			} else if (query.indexOf(".remove(") != -1) {
				basicOps.deleteNative(ontology, query);
				return "Execute remove on ontology:" + ontology;
			} else if (query.indexOf(".drop") != -1) {
				return "Drop a collection from QueryTool not supported.";
			} else
				return basicOps.queryNativeAsJson(ontology, query);
		} catch (Exception e) {
			log.error("Error queryNativeAsJson:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public String querySQLAsJson(String user, String ontology, String query, int offset) throws DBPersistenceException {
		try {
			checkQueryIs4Ontology(ontology, query, true);
			hasUserPermission(user, ontology);
			return basicOps.querySQLAsJson(ontology, query, offset);
		} catch (Exception e) {
			log.error("Error querySQLAsJson:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

}
