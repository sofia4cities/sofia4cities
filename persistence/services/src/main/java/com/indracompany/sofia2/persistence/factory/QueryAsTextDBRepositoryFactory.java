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
package com.indracompany.sofia2.persistence.factory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.persistence.elasticsearch.ElasticSearchQueryAsTextDBRepository;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.hadoop.impala.ImpalaQueryAsTextDBRepository;
import com.indracompany.sofia2.persistence.interfaces.QueryAsTextDBRepository;
import com.indracompany.sofia2.persistence.mongodb.services.QueryAsTextMongoDBImpl;

@Component
public class QueryAsTextDBRepositoryFactory {

	@Autowired
	private QueryAsTextMongoDBImpl queryMongo;

	@Autowired
	private ElasticSearchQueryAsTextDBRepository queryElasticSearch;

	@Autowired
	private ImpalaQueryAsTextDBRepository impalaQueryAsTextDBRepository;

	@Autowired
	private OntologyService ontologyService;

	@Autowired
	private ClientPlatformService clientPlatformService;

	public QueryAsTextDBRepository getInstance(String ontologyId, String sessionUserId) throws DBPersistenceException {
		Ontology ds = ontologyService.getOntologyByIdentification(ontologyId, sessionUserId);
		RtdbDatasource dataSource = ds.getRtdbDatasource();
		return getInstance(dataSource);
	}

	public QueryAsTextDBRepository getInstanceClientPlatform(String ontologyId, String clientP)
			throws DBPersistenceException {
		ClientPlatform cp = clientPlatformService.getByIdentification(clientP);

		List<Ontology> ds = ontologyService.getOntologiesByClientPlatform(cp);

		Ontology result1 = ds.stream().filter(x -> ontologyId.equals(x.getIdentification())).findAny().orElse(null);

		if (result1 != null) {
			RtdbDatasource dataSource = result1.getRtdbDatasource();
			return getInstance(dataSource);
		} else
			return queryMongo;
	}

	public QueryAsTextDBRepository getInstance(RtdbDatasource dataSource) {
		if (dataSource.equals(RtdbDatasource.Mongo))
			return queryMongo;
		else if (dataSource.equals(RtdbDatasource.ElasticSearch))
			return queryElasticSearch;
		else if (dataSource.equals(RtdbDatasource.Hadoop))
			return impalaQueryAsTextDBRepository;
		else
			return queryMongo;
	}
}
