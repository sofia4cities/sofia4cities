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
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.jmx.mbean;

import java.util.Map;
import java.util.Map.Entry;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import com.indra.sofia2.support.bbdd.sib.persistence.DAOTRPersistence;
import com.indra.sofia2.support.util.monitoring.healthchecks.SystemHealthChecksManagerImpl;

@Component
@ManagedResource(objectName = "SOFIA2:type=Monitorizacion,name=MbSibHealthCheck", description = "Chequeo rápido del estado del SIB")
public class MbSibHealth {

	@Autowired
    @Qualifier("DAODiskDBTR")
	private DAOTRPersistence daoTr;

	@Autowired
    @Qualifier("dataSource")
	private DataSource dataSource;

	@Autowired(required = false)
    @Qualifier("dataSourceBdh")
	private DataSource hdbDataSource;

	@Autowired
	private SystemHealthChecksManagerImpl systemHealthChecks;

	@ManagedOperation(description = "Estado de la BDC")
	public boolean doCdbHealthCheck() {
		return systemHealthChecks.cdbHealthCheck().toBoolean();
	}

	@ManagedOperation(description = "Estado de las BDTRs")
	public Map<String, Boolean> doRtdbHealthCheck() {
		
		return systemHealthChecks.rtdbHealthCheck();
	}

	@ManagedOperation(description = "Estado de la BDH")
	public boolean doHdbHealthCheck() {
		return systemHealthChecks.hdfsHealthCheck().toBoolean();
	}

	@ManagedOperation(description = "Estado de SIB TOOLS")
	public boolean doSibToolsHealthCheck() {
		return systemHealthChecks.scriptHttpConnectivityCheck().toBoolean();
	}

	@ManagedOperation(description = "Estado del SIB")
	public boolean doSibHealthCheck() {
		boolean bdtrsOk=true;
		for(Entry<String,Boolean> entry:this.doRtdbHealthCheck().entrySet()){
			if(!entry.getValue()){
				bdtrsOk=false;
			}
		}
		return this.doCdbHealthCheck() && bdtrsOk
				&& this.doHdbHealthCheck() && this.doSibToolsHealthCheck();
	}

}
