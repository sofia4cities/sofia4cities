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
package com.indra.sofia2.web.api.version;

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
@Path("/version")
@Api(name = "Versión del módulo", description = "Servicio REST para recuperar la versión del módulo")
public interface VersionRestService {

	@GET
	@Path("/")
	@ApiMethod(
			path="/current/version",
	        verb=ApiVerb.GET,
	        description="Obtiene la información de la versión via REST",
	        produces={MediaType.APPLICATION_JSON},
	        consumes={MediaType.APPLICATION_JSON}
	)

	@ApiErrors(apierrors={
	        @ApiError(code="400", description="Bad Request"),
	        @ApiError(code="501", description="Internal Server Error")
	})	
	public Response getVersion() throws Exception;
	
	
}
