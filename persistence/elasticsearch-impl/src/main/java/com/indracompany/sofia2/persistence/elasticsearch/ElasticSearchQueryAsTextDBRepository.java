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
package com.indracompany.sofia2.persistence.elasticsearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.interfaces.QueryAsTextDBRepository;

import lombok.extern.slf4j.Slf4j;

@Component("QueryAsTextElasticSearchDBRepository")
@Scope("prototype")
@Slf4j
public class ElasticSearchQueryAsTextDBRepository implements QueryAsTextDBRepository {
	
	@Autowired
	@Qualifier("ElasticSearchBasicOpsDBRepository")
	private BasicOpsDBRepository elasticSearchBasicOpsDBRepository;
	

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		query=query.toLowerCase();
		return elasticSearchBasicOpsDBRepository.queryNativeAsJson(ontology, query, offset, limit);
	}

	@Override
	public String queryNativeAsJson(String ontology, String query) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		query=query.toLowerCase();
		return elasticSearchBasicOpsDBRepository.queryNativeAsJson(ontology, query);
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		ontology=ontology.toLowerCase();
		query=query.toLowerCase();
		return elasticSearchBasicOpsDBRepository.querySQLAsJson(ontology, query, offset);
	}

}
