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
package com.indracompany.sofia2.persistence.hadoop.impala;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.hadoop.common.NameBeanConst;
import com.indracompany.sofia2.persistence.hadoop.resultset.DefaultResultSetExtractor;
import com.indracompany.sofia2.persistence.hadoop.util.HadoopQueryProcessor;
import com.indracompany.sofia2.persistence.interfaces.QueryAsTextDBRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@ConditionalOnBean(name = { NameBeanConst.IMPALA_TEMPLATE_JDBC_BEAN_NAME })
public class ImpalaQueryAsTextDBRepository implements QueryAsTextDBRepository {

	@Autowired
	@Qualifier(NameBeanConst.IMPALA_TEMPLATE_JDBC_BEAN_NAME)
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private HadoopQueryProcessor hivePricessor;

	@Override
	public String queryNativeAsJson(String ontology, String query, int offset, int limit)
			throws DBPersistenceException {
		return jdbcTemplate.query(hivePricessor.parse(query), new DefaultResultSetExtractor());
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
