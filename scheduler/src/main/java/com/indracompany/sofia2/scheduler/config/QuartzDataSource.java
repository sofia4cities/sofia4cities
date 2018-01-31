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
package com.indracompany.sofia2.scheduler.config;


import static com.indracompany.sofia2.scheduler.PropertyNames.SCHEDULER_PREFIX;
import static com.indracompany.sofia2.scheduler.PropertyNames.SCHEDULER_PROPERTIES_LOCATION;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnResource(resources = SCHEDULER_PROPERTIES_LOCATION)
public class QuartzDataSource {
	
	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource")
	public DataSourceProperties sofiaDataSourceProperties() {
	    return new DataSourceProperties();
	}
	
	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource")
	public DataSource sofiaDataSource() {
	    return sofiaDataSourceProperties().initializeDataSourceBuilder().build();
	}
	
	@Bean	
	@ConfigurationProperties(SCHEDULER_PREFIX)
	public DataSourceProperties quartzDataSourceProperties() {
	    return new DataSourceProperties();
	}
	
	@Bean(name="quartzDatasource")
	@ConfigurationProperties(SCHEDULER_PREFIX)
	public DataSource barDataSource() {
	    return quartzDataSourceProperties().initializeDataSourceBuilder().build();
	}

}
