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
package com.indra.sofia2.web.api.monitoring;

import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indra.sofia2.grid.monitoring.GridStatisticsMonitor;
import com.indra.sofia2.support.util.monitoring.SystemStatusCheckService;
import com.indra.sofia2.support.util.monitoring.databases.DbConnectionPoolHealthService;
import com.indra.sofia2.support.util.monitoring.databases.DbConnectionPoolStatusDTO;
import com.indra.sofia2.support.util.rest.ResponseBuilder;

@Component("monitoringRestService")
public class MonitoringRestServiceImpl implements MonitoringRestService {

	@Autowired
	private DbConnectionPoolHealthService dbConnectionPoolHealthService;

	@Autowired
	private GridStatisticsMonitor gridStatisticsMonitor;

	@Autowired
	private SystemStatusCheckService statusCheckService;

	@Override
	public Response getMonitoringData() throws Exception {
		return ResponseBuilder.buildResponse(statusCheckService.getSystemStatus());
	}

	@Override
	public Response getCdbPoolStatus() throws Exception {
		DbConnectionPoolStatusDTO dto;
		try {
			dto = this.dbConnectionPoolHealthService.getCdbPoolStatus();
			dto.setStatusMessage("The status of the database connections pool has been verified successfully");
			return ResponseBuilder.buildResponse(dto);
		} catch (Throwable e) {
			dto = new DbConnectionPoolStatusDTO();
			dto.setCurrentTimestamp(new DateTime().toString());
			dto.setStatusMessage("Unable to verify the status of the database connections pool");
			return ResponseBuilder.buildResponse(dto);
		}
	}

	@Override
	public Response getHadoopHdbPoolStatus() throws Exception {
		DbConnectionPoolStatusDTO dto;
		try {
			dto = this.dbConnectionPoolHealthService.getHdbPoolStatus();
			dto.setStatusMessage("The status of the database connections pool has been verified successfully");
			return ResponseBuilder.buildResponse(dto);
		} catch (Throwable e) {
			dto = new DbConnectionPoolStatusDTO();
			dto.setCurrentTimestamp(new DateTime().toString());
			dto.setStatusMessage("Unable to verify the status of the database connections pool");
			return ResponseBuilder.buildResponse(dto);
		}
	}

	@Override
	public Response getGridStatistics() throws Exception {
		return ResponseBuilder.buildResponse(gridStatisticsMonitor.getGridStatistics());
	}
}
