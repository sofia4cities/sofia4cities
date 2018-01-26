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
package com.indracompany.sofia2.api.rest.api;

import javax.ws.rs.core.Response;

import com.indracompany.sofia2.api.rest.api.dto.ApiDTO;

import io.swagger.annotations.Api;
@Api("/apis")
public class ApiRestServiceImpl implements ApiRestService {

	@Override
	public Response getApi(String identificacion, String token) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response getApiFilter(String identificacion, String estado, String usuario, String token) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response create(ApiDTO api, String token) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response update(ApiDTO suscripcion, String token) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response delete(ApiDTO suscripcion, String token) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response deleteByIdentificacionNumversion(String identificacion, String numversion, String token)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response getApiUsuario(String idUsuario, String token) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
