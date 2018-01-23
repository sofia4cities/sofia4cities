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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2015  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/

package com.indra.sofia2.support.util.monitoring.databases;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.indra.jee.arq.spring.core.infraestructura.accesodatos.ArqSpringDataSource;

@Component
public class DbConnectionPoolHealthServiceImpl implements DbConnectionPoolHealthService{
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;

	@Autowired(required = false)
	@Qualifier("dataSourceBdh")
	private DataSource hdbDataSource;

	@Override
	public DbConnectionPoolStatusDTO getCdbPoolStatus() {
		return checkPool(dataSource);
	}

	@Override
	public DbConnectionPoolStatusDTO getHdbPoolStatus() {
		return checkPool(hdbDataSource);
	}
	
	private DbConnectionPoolStatusDTO checkPool(DataSource ds){
		ArqSpringDataSource datasource = (ArqSpringDataSource) ds;
		DbConnectionPoolStatusDTO status = new DbConnectionPoolStatusDTO();
		status.setCurrentTimestamp(new DateTime().toString());
		status.setMaxIdle(datasource.getMaxIdle());
		status.setMaxActive(datasource.getMaxActive());
		status.setNumActive(datasource.getNumActive());
		status.setNumIdle(datasource.getNumIdle());
		status.setClosed(datasource.isClosed());
		return status;
	}	
}
