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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;
import com.indracompany.sofia2.persistence.interfaces.QueryAsTextDBRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QueryToolServiceImpl implements QueryToolService {

	@Autowired
	QueryAsTextDBRepository queryOps = null;

	@Autowired
	@Qualifier("MongoManageDBRepository")
	ManageDBRepository manageOps = null;

	@Autowired
	OntologyService ontologyService = null;

	private void hasUserPermission(String user, String ontology) throws Exception {
		if (!ontologyService.hasUserPermissionForQuery(user, ontology)) {
			throw new Exception("User:" + user + " has nos permission to query ontology " + ontology);
		}
	}

	private void hasClientPlatformPermisionForQuery(String clientPlatform, String ontology) throws Exception{
		if (!ontologyService.hasClientPlatformPermisionForQuery(clientPlatform, ontology)) {
			throw new Exception("Client Platform:" + clientPlatform + " has nos permission to query ontology " + ontology);
		}
	}

	@Override
	public String queryNativeAsJson(String user, String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		try {
			hasUserPermission(user, ontology);
			return queryOps.queryNativeAsJson(ontology, query, offset, limit);
		} catch (final Exception e) {
			log.error("Error queryNativeAsJson:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public String queryNativeAsJson(String user, String ontology, String query) throws DBPersistenceException {
		try {
			hasUserPermission(user, ontology);
			return queryOps.queryNativeAsJson(ontology, query);
		} catch (final Exception e) {
			log.error("Error queryNativeAsJson:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public String querySQLAsJson(String user, String ontology, String query, int offset) throws DBPersistenceException {
		try {
			hasUserPermission(user, ontology);
			return queryOps.querySQLAsJson(ontology, query, offset);
		} catch (final Exception e) {
			log.error("Error querySQLAsJson:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public String queryNativeAsJsonForPlatformClient(String clientPlatform, String ontology, String query, int offset,
			int limit) throws DBPersistenceException {

		try {
			hasClientPlatformPermisionForQuery(clientPlatform, ontology);
			return queryOps.queryNativeAsJson(ontology, query, offset, limit);
		} catch (final Exception e) {
			log.error("Error queryNativeAsJsonForPlatformClient:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

	@Override
	public String querySQLAsJsonForPlatformClient(String clientPlatform, String ontology, String query, int offset)
			throws DBPersistenceException {
		try {
			hasClientPlatformPermisionForQuery(clientPlatform, ontology);
			return queryOps.querySQLAsJson(ontology, query, offset);
		} catch (final Exception e) {
			log.error("Error querySQLAsJsonForPlatformClient:" + e.getMessage());
			throw new DBPersistenceException(e);
		}
	}

}
