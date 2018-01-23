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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiError;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;

@Produces({ MediaType.APPLICATION_JSON})
@Consumes({ MediaType.APPLICATION_JSON})
@Path("/monitoring")
@Api(name = "Monitoring Service", description = "Sofia2 REST monitoring service")
public interface MonitoringRestService {
	
	@GET
	@Path("/")
	@ApiMethod(
			path="/current/monitoring",
	        verb=ApiVerb.GET,
	        		description="Performs the health checks on this module",
	        produces={MediaType.APPLICATION_JSON},
	        consumes={MediaType.APPLICATION_JSON}
	)

	@ApiErrors(apierrors={
	        @ApiError(code="400", description="Bad Request"),
	        @ApiError(code="501", description="Internal Server Error")
	})	
	public Response getMonitoringData() throws Exception;
	
	@GET
	@Path("/cdb-pool")
	@ApiMethod(
			path="/current/monitoring/cdb-pool",
	        verb=ApiVerb.GET,
	        description="Returns the current status of the configuration database pool",
	        produces={MediaType.APPLICATION_JSON},
	        consumes={MediaType.APPLICATION_JSON}
	)

	@ApiErrors(apierrors={
	        @ApiError(code="400", description="Bad Request"),
	        @ApiError(code="501", description="Internal Server Error")
	})	
	public Response getCdbPoolStatus() throws Exception;
	
	@GET
	@Path("/hdb-pool")
	@ApiMethod(
			path="/current/monitoring/hdb-pool",
	        verb=ApiVerb.GET,
	        description="Returns the current status of the real-time historic database pool",
	        produces={MediaType.APPLICATION_JSON},
	        consumes={MediaType.APPLICATION_JSON}
	)

	@ApiErrors(apierrors={
	        @ApiError(code="400", description="Bad Request"),
	        @ApiError(code="501", description="Internal Server Error")
	})	
	public Response getHadoopHdbPoolStatus() throws Exception;
	
	@GET
	@Path("/grid-statistics")
	@ApiMethod(
			path="/current/monitoring/grid-statistics",
	        verb=ApiVerb.GET,
	        description="Returns the statistics of the datagrids associated to this Sofia2 core",
	        produces={MediaType.APPLICATION_JSON},
	        consumes={MediaType.APPLICATION_JSON}
	)

	@ApiErrors(apierrors={
	        @ApiError(code="400", description="Bad Request"),
	        @ApiError(code="501", description="Internal Server Error")
	})	
	public Response getGridStatistics() throws Exception;
}
