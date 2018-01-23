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

import java.util.Locale;

import org.joda.time.DateTime;
import org.springframework.context.i18n.LocaleContextHolder;

import com.indra.jee.arq.spring.core.infraestructura.log.I18nLog;
import com.indra.jee.arq.spring.core.infraestructura.log.I18nLogFactory;
import com.netflix.hystrix.HystrixCommand;

public class DbPoolHystrixMonitoring extends HystrixCommand<DbConnectionPoolStatusDTO>{

	public enum POOL_T {CDB, HADOOP_HDB};
	
	private static final I18nLog LOG = I18nLogFactory.getLogI18n(DbPoolHystrixMonitoring.class);
	
	private DbConnectionPoolHealthService dbConnectionPoolHealthService;
	
	private POOL_T pool_type;
	
	public DbPoolHystrixMonitoring(Setter setter, DbConnectionPoolHealthService dbConnectionPoolHealthService, POOL_T poolType) {
        super(setter);
        this.dbConnectionPoolHealthService = dbConnectionPoolHealthService;
        this.pool_type = poolType;
    }
	
	@Override
	protected DbConnectionPoolStatusDTO run() throws Exception {
		Locale locale = LocaleContextHolder.getLocale();
		DbConnectionPoolStatusDTO dto;
		switch (pool_type){
		case CDB:
			dto = this.dbConnectionPoolHealthService.getCdbPoolStatus();
			break;
		default:
			dto = this.dbConnectionPoolHealthService.getHdbPoolStatus();
			break;
		}
		dto.setStatusMessage(LOG.getMensaje(locale, "monitoring.dbpool.ok"));
		return dto;
	}
	
	@Override
    protected DbConnectionPoolStatusDTO getFallback() {
		Locale locale = LocaleContextHolder.getLocale();
		DbConnectionPoolStatusDTO dto = new DbConnectionPoolStatusDTO();
		dto.setCurrentTimestamp(new DateTime().toString());
		dto.setStatusMessage(LOG.getMensaje(locale, "monitoring.dbpool.ko"));		
		return dto;
    }	
}
