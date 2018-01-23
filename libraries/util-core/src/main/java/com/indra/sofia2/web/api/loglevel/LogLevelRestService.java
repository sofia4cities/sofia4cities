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
package com.indra.sofia2.web.api.loglevel;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiError;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParam;
import org.jsondoc.core.pojo.ApiParamType;
import org.jsondoc.core.pojo.ApiVerb;

@Produces({ MediaType.APPLICATION_JSON})
@Consumes({ MediaType.APPLICATION_JSON})
@Path("/loglevel")
@Api(name = "Log level Service", description = "Sofia2 log level service")
public interface LogLevelRestService {
	@GET
	@Path("/")
	@ApiMethod(
			path="/current/loglevel",
	        verb=ApiVerb.GET,
	        description="Returns the current log level",
	        produces={MediaType.APPLICATION_JSON},
	        consumes={MediaType.APPLICATION_JSON}
	)
	@ApiErrors(apierrors={
	        @ApiError(code="400", description="Bad Request"),
	        @ApiError(code="501", description="Internal Server Error")
	})	
	public Response getLogLevel() throws Exception;
	
	@POST
	@Path("/{loglevel}")
	@ApiMethod(
	        path="/current/loglevel/{loglevel}",
	        verb=ApiVerb.POST,
	        description="Modifies the log level of all the loggers",
	        produces={MediaType.APPLICATION_JSON},
	        consumes={MediaType.APPLICATION_JSON}
	)
	@ApiErrors(apierrors={
	        @ApiError(code="400", description="Bad request"),
	        @ApiError(code="500", description="Internal server error")
	})
	public Response updateLogLevel(
			@PathParam("loglevel") @ApiParam(name = "loglevel", description = "A valid Log4j log level (TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF, ALL)", paramType = ApiParamType.PATH, required = true) String logLevel)
			throws Exception;
	
	@POST
	@Path("/{loglevel}/{logger}")
	@ApiMethod(
	        path="/current/loglevel/{loglevel}/{logger}",
	        verb=ApiVerb.POST,
	        description="Modifies the log level of the specified logger",
	        produces={MediaType.APPLICATION_JSON},
	        consumes={MediaType.APPLICATION_JSON}
	)
	@ApiErrors(apierrors={
	        @ApiError(code="400", description="Bad request"),
	        @ApiError(code="500", description="Internal server error")
	})
	public Response updateLoggersLevel(
			@PathParam("loglevel") @ApiParam(name = "loglevel", description = "A valid Log4j log level (TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF, ALL)", paramType = ApiParamType.PATH, required = true) String logLevel,
			@PathParam("logger") @ApiParam(name = "logger", description = "The logger to modify", paramType = ApiParamType.PATH, required = true) String logger)
			throws Exception;


}