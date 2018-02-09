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
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.mongodb.MongoBasicOpsDBRepository;
import com.indracompany.sofia2.persistence.services.QueryToolService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QueryToolServiceMongoDbImpl implements QueryToolService {

	@Autowired
	MongoBasicOpsDBRepository mongoRepo = null;

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		try {
			return mongoRepo.queryNativeAsJson(ontology, query, offset, limit);
		} catch (Exception e) {
			throw new DBPersistenceException("Error executing query:" + e.getMessage(), e);
		}
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		try {
			return mongoRepo.querySQLAsJson(ontology, query, offset);
		} catch (Exception e) {
			throw new DBPersistenceException("Error executing query:" + e.getMessage(), e);
		}
	}

}
