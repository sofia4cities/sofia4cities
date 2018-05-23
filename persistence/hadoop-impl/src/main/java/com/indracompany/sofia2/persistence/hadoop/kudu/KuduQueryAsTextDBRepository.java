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
package com.indracompany.sofia2.persistence.hadoop.kudu;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.hadoop.NameBeanConst;
import com.indracompany.sofia2.persistence.hadoop.config.condition.HadoopEnabledCondition;
import com.indracompany.sofia2.persistence.hadoop.resultset.KuduResultSetExtractor;
import com.indracompany.sofia2.persistence.hadoop.util.QueryProcessor;
import com.indracompany.sofia2.persistence.interfaces.QueryAsTextDBRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository(NameBeanConst.KUDU_QUERY_REPO_BEAN_NAME)
@Conditional(HadoopEnabledCondition.class)
public class KuduQueryAsTextDBRepository implements QueryAsTextDBRepository {

	@Autowired
	@Qualifier(NameBeanConst.IMPALA_TEMPLATE_JDBC_BEAN_NAME)
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private QueryProcessor queryProcessor;

	@PostConstruct
	public void sss() {
		log.info("dadasdasd");
	}

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		return jdbcTemplate.query(queryProcessor.parse(query), new KuduResultSetExtractor());
	}

	@Override
	public String queryNativeAsJson(String ontology, String query) throws DBPersistenceException {
		return queryNativeAsJson(ontology, query, -1, -1);
	}

	@Override
	public String querySQLAsJson(String ontology, String query, int offset) throws DBPersistenceException {
		return queryNativeAsJson(ontology, query, offset, -1);
	}

}
