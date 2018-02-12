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

import java.util.Locale;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.rest.api.dto.ApiDTO;
import com.indracompany.sofia2.api.rest.api.fiql.ApiFIQL;
import com.indracompany.sofia2.api.service.api.ApiServiceRest;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.Api.ApiType;

@Component("apiRestServiceImpl")
public class APiRestServiceImpl implements ApiRestService {

	Locale locale = LocaleContextHolder.getLocale();

	@Autowired
	private ApiServiceRest apiService;

	@Autowired
	private ApiFIQL apiFIQL;

	@Override
	public Response getApi(String identificacion, String tokenUsuario) throws Exception {
		return Response.ok(apiFIQL.toApiDTO(apiService.findApi(identificacion, tokenUsuario))).build();
	}

	@Override
	public Response getApiFilter(String identificacion, String estado, String usuario, String tokenUsuario)
			throws Exception {
		return Response.ok(apiFIQL.toApiDTO(apiService.findApis(identificacion, estado, usuario, tokenUsuario)))
				.build();
	}

	@Override
	public Response create(ApiDTO api, String tokenUsuario) throws Exception {
		apiService.createApi(api, tokenUsuario);
		Object parametros[] = { api.getIdentification() };
		return Response.ok(parametros).type(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response update(ApiDTO api, String tokenUsuario) throws Exception {
		apiService.updateApi(api, tokenUsuario);
		Object parametros[] = { api.getIdentification() };
		return Response.ok(parametros).type(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response delete(ApiDTO api, String tokenUsuario) throws Exception {
		apiService.removeApi(api, tokenUsuario);
		Object parametros[] = { api.getIdentification(), api.getVersion().toString() };
		return Response.ok(parametros).type(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response deleteByIdentificacionNumversion(String identificacion, String numversion, String tokenUsuario)
			throws Exception {
		apiService.removeApiByIdentificacionNumversion(identificacion, numversion, tokenUsuario);
		Object parametros[] = { identificacion, numversion };
		return Response.ok(parametros).type(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response getApiUsuario(String idUsuario, String tokenUsuario) throws Exception {
		return Response.ok(apiFIQL.toApiDTO(apiService.findApisByUser(idUsuario, tokenUsuario))).build();
	}

	public ApiServiceRest getApiService() {
		return apiService;
	}

	public void setApiService(ApiServiceRest apiService) {
		this.apiService = apiService;
	}

	public ApiFIQL getApiFIQL() {
		return apiFIQL;
	}

	public void setApiFIQL(ApiFIQL apiFIQL) {
		this.apiFIQL = apiFIQL;
	}

	@Override
	public Response create(String indentifier, ApiType api, String token) throws Exception {
		Api apiRes = apiService.changeState(indentifier, api, token);
		if (apiRes!=null) {
			Object parametros[] = { apiRes };
			return Response.ok(parametros).type(MediaType.APPLICATION_JSON).build();
		}
		else return Response.ok("Cannot Change API STATE").type(MediaType.APPLICATION_JSON).build();
			
	}

}