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

import static com.indracompany.sofia2.persistence.hadoop.common.NameBeanConst.KUDU_BASIC_OPS_BEAN_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.persistence.elasticsearch.ElasticSearchBasicOpsDBRepository;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.mongodb.MongoBasicOpsDBRepository;

@Component
public class BasicOpsDBRepositoryFactory {

	@Autowired
	private ElasticSearchBasicOpsDBRepository elasticBasicOps;

	@Autowired
	private MongoBasicOpsDBRepository mongoBasicOps;

	@Autowired
	private OntologyRepository ontologyRepository;

	@Autowired
	@Qualifier(KUDU_BASIC_OPS_BEAN_NAME)
	private BasicOpsDBRepository kuduBasicOpsDBRepository;

	public BasicOpsDBRepository getInstance(String ontologyId) throws DBPersistenceException {
		Ontology ds = ontologyRepository.findByIdentification(ontologyId);
		RtdbDatasource dataSource = ds.getRtdbDatasource();
		return getInstance(dataSource);
	}

	public BasicOpsDBRepository getInstance(RtdbDatasource dataSource) throws DBPersistenceException {
		if (RtdbDatasource.Mongo.equals(dataSource))
			return mongoBasicOps;
		else if (RtdbDatasource.ElasticSearch.equals(dataSource))
			return elasticBasicOps;
		else if (RtdbDatasource.Kudu.equals(dataSource)) {
			return kuduBasicOpsDBRepository;
		} else
			return mongoBasicOps;
	}

}
