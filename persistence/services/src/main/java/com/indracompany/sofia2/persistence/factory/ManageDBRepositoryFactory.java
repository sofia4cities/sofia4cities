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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Ontology.RtdbDatasource;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.persistence.elasticsearch.ElasticSearchManageDBRepository;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.hadoop.hive.HiveManageDBRepository;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;
import com.indracompany.sofia2.persistence.mongodb.MongoNativeManageDBRepository;

@Component
public class ManageDBRepositoryFactory {

	@Autowired
	private MongoNativeManageDBRepository mongoManage;

	@Autowired
	private ElasticSearchManageDBRepository elasticManage;

	@Autowired
	private HiveManageDBRepository hiveManageDBRepository;

	@Autowired
	private OntologyRepository ontologyRepository;

	public ManageDBRepository getInstance(String ontologyId) throws DBPersistenceException {
		Ontology ds = ontologyRepository.findByIdentification(ontologyId);
		RtdbDatasource dataSource = ds.getRtdbDatasource();
		return getInstance(dataSource);
	}

	public ManageDBRepository getInstance(RtdbDatasource dataSource) throws DBPersistenceException {
		if (dataSource.equals(RtdbDatasource.Mongo))
			return mongoManage;
		else if (dataSource.equals(RtdbDatasource.ElasticSearch))
			return elasticManage;
		else if (dataSource.equals(RtdbDatasource.Hadoop)) {
			return hiveManageDBRepository;
		} else
			return mongoManage;
	}

}
