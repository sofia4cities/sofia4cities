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
package com.indracompany.sofia2.persistence.mongodb.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.QueryAsTextDBRepository;
import com.indracompany.sofia2.persistence.mongodb.MongoBasicOpsDBRepository;
import com.indracompany.sofia2.persistence.mongodb.MongoNativeManageDBRepository;
import com.indracompany.sofia2.persistence.mongodb.UtilMongoDB;

import lombok.extern.slf4j.Slf4j;

@Component("QueryAsTextMongoDBRepository")
@Scope("prototype")
@Slf4j
public class QueryAsTextMongoDBImpl implements QueryAsTextDBRepository {

	@Autowired
	@Qualifier("MongoBasicOpsDBRepository")
	MongoBasicOpsDBRepository mongoRepo = null;

	@Autowired
	@Qualifier("MongoManageDBRepository")
	MongoNativeManageDBRepository manageRepo = null;

	@Autowired
	UtilMongoDB utils = null;

	private void checkQueryIs4Ontology(String ontology, String query, boolean sql) throws Exception {
		query = query.replace("\n", "");
		if (sql == true) {
			if (query.toLowerCase().indexOf("from " + ontology.toLowerCase()) == -1
					&& query.toLowerCase().indexOf("join " + ontology.toLowerCase()) == -1)
				throw new Exception("The query '" + query + "' is not for the ontology selected: " + ontology);
		} else {
			if (query.indexOf("db.") == -1)
				return;
			if (query.indexOf("." + ontology + ".") == -1)
				throw new DBPersistenceException(
						"The query " + query + " is not for the ontology selected:" + ontology);
		}
	}

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		try {
			checkQueryIs4Ontology(ontology, query, false);
			return mongoRepo.queryNativeAsJson(ontology, query, offset, limit);
		} catch (Exception e) {
			log.error("Error queryNativeAsJson:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public String queryNativeAsJson(String ontology, String query) throws DBPersistenceException {
		String queryContent = null;
		try {
			checkQueryIs4Ontology(ontology, query, false);
			queryContent = utils.getQueryContent(query);
			if (query.indexOf(".createIndex(") != -1) {
				manageRepo.createIndex(query);
				return "Created index indicated in the query:" + query;
			} else if (query.indexOf(".insert(") != -1) {
				return "Inserted row with id:" + mongoRepo.insert(ontology, "", queryContent);
			} else if (query.indexOf(".update(") != -1) {
				return "Updated " + mongoRepo.updateNative(ontology, queryContent) + " rows";
			} else if (query.indexOf(".dropIndex(") != -1) {
				query = query.substring(query.indexOf(".dropIndex(") + 11, query.length());
				query = query.replace("\"", "");
				query = query.replace("'", "");
				String indexName = query.substring(0, query.indexOf(")"));
				manageRepo.dropIndex(ontology, indexName);
				return "Dropped index indicated in the query:" + query;
			} else if (query.indexOf(".getIndexes()") != -1) {
				return manageRepo.getIndexes(ontology);
			} else if (query.indexOf(".remove(") != -1) {
				long number = mongoRepo.deleteNative(ontology, queryContent);
				return "Deleted " + number + " rows from ontology:" + ontology;
			} else if (query.indexOf(".count(") != -1) {
				return "" + mongoRepo.countNative(ontology, queryContent);
			} else if (query.indexOf(".drop") != -1) {
				return "Drop a collection from QueryTool not supported.";
			} else
				return mongoRepo.queryNativeAsJson(ontology, query);
		} catch (Exception e) {
			log.error("Error queryNativeAsJson:" + e.getMessage(), e);
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		try {
			checkQueryIs4Ontology(ontology, query, true);
			return mongoRepo.querySQLAsJson(ontology, query, offset);
		} catch (Exception e) {
			log.error("Error querySQLAsJson:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

}
