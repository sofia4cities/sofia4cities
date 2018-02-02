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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.api.rest.api.dto.ApiDTO;
import com.indracompany.sofia2.api.rest.api.dto.ApiHeaderDTO;
import com.indracompany.sofia2.api.rest.api.dto.ApiQueryParameterDTO;
import com.indracompany.sofia2.api.rest.api.dto.AutenticacionAtribDTO;
import com.indracompany.sofia2.api.rest.api.dto.AutenticacionDTO;
import com.indracompany.sofia2.api.rest.api.dto.OperacionDTO;
import com.indracompany.sofia2.api.rest.api.fiql.ApiFIQL;
import com.indracompany.sofia2.api.rest.api.fiql.AutenticacionFIQL;
import com.indracompany.sofia2.api.rest.api.fiql.HeaderFIQL;
import com.indracompany.sofia2.api.rest.api.fiql.OperacionFIQL;
import com.indracompany.sofia2.api.rest.api.fiql.QueryParameterFIQL;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.ApiAuthentication;
import com.indracompany.sofia2.config.model.ApiAuthenticationAttribute;
import com.indracompany.sofia2.config.model.ApiAuthenticationParameter;
import com.indracompany.sofia2.config.model.ApiHeader;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.ApiQueryParameter;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ApiAuthenticationAttributeRepository;
import com.indracompany.sofia2.config.repository.ApiAuthenticationParameterRepository;
import com.indracompany.sofia2.config.repository.ApiAuthenticationRepository;
import com.indracompany.sofia2.config.repository.ApiHeaderRepository;
import com.indracompany.sofia2.config.repository.ApiOperationRepository;
import com.indracompany.sofia2.config.repository.ApiQueryParameterRepository;
import com.indracompany.sofia2.config.repository.ApiRepository;
import com.indracompany.sofia2.service.user.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiServiceRest {

	@Autowired
	private UserService userService;

	@Autowired
	private ApiFIQL apiFIQL;

	@Autowired
	private ApiRepository apiRepository;

	@Autowired
	private ApiOperationRepository apiOperationRepository;

	@Autowired
	private ApiHeaderRepository apiHeaderRepository;

	@Autowired
	private ApiQueryParameterRepository apiQueryParameterRepository;

	@Autowired
	private ApiAuthenticationRepository apiAuthenticationRepository;

	@Autowired
	private ApiAuthenticationParameterRepository apiAuthenticationParameterRepository;

	@Autowired
	private ApiAuthenticationAttributeRepository apiAuthenticationAttributeRepository;

	Locale locale = LocaleContextHolder.getLocale();

	public Api findApi(String identificacion, String token) {
		Api api = getApiMaxVersion(identificacion);
		if (api != null) {
			if (authorized(api, token)) {
				return api;
			}
		} else {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.ApiNoExiste");
		}
		return null;
	}

	public List<Api> findApis(String identificacion, String estado, String usuario, String token) {
		List<Api> apis = null;
		String userFiltro = null;
		if (usuario != null && !usuario.equals("")) {
			User users = userService.getUser(usuario);
			if (users != null) {
				userFiltro = users.getUserId();
			} else {
				userFiltro = "USUARIOERROR";
			}
		}
		apis = apiRepository.findByIdentification(identificacion);
		return apis;
	}

	public void createApi(ApiDTO apiDTO, String token) {
		Api api = apiFIQL.copyProperties(apiDTO);

		Integer numVersion = 0;
		List<Api> apis = apiRepository.findByIdentification(api.getIdentification());
		for (Api apiBD : apis) {
			if (numVersion < apiBD.getNumversion()) {
				numVersion = apiBD.getNumversion();
			}
		}
		if (numVersion >= api.getNumversion()) {
			Object parametros[] = { numVersion };
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.wrongversionMin");
		}

		User user = userService.getUserByToken(token);

		api.setUser(user);

		api.setState("creada");
		apiRepository.saveAndFlush(api);

		// Se crean las operaciones
		createOperations(apiDTO.getOperations(), api);

		// Se crea la autenticacion
		createAutenticacion(apiDTO.getAuthentication(), api);
	}

	public void updateApi(ApiDTO apiDTO, String token) {
		try {
			Api api = apiFIQL.copyProperties(apiDTO);

			Api apiUpdate = apiRepository
					.findByIdentificationAndNumversion(api.getIdentification(), api.getNumversion()).get(0);
			if (authorized(api, token)) {
				apiUpdate = apiFIQL.copyProperties(apiUpdate, api);
				apiRepository.saveAndFlush(apiUpdate);

				// Se actualizan las operaciones (se eliminan las antiguas y se crean las
				// nuevas)
				updateOperaciones(apiDTO.getOperations(), apiUpdate);

				// Se actualiza la autorizacion y sus operaciones
				updateAutenticacion(apiDTO.getAuthentication(), apiUpdate);

			} else {
				throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoPermisosOperacion");
			}
		} catch (Exception e) {
			throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoApi");
		}
	}

	public void removeApi(ApiDTO apiDTO, String token) {
		try {
			Api api = apiFIQL.copyProperties(apiDTO);
			Api apiDelete = apiRepository
					.findByIdentificationAndNumversion(api.getIdentification(), api.getNumversion()).get(0);
			if (authorized(apiDelete, token)) {
				// Se eliminan las Operaciones
				removeOperaciones(apiDelete);

				// Se eliminan la autorizacion
				removeAutorizacion(apiDelete);

				// Se elimina el api
				apiRepository.delete(apiDelete);
			} else {
				throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoPermisosOperacion");
			}
		} catch (Exception e) {
			throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoApi");
		}
	}

	public void removeApiByIdentificacionNumversion(String identificacion, String numversion, String token) {
		Integer version = null;
		try {
			version = Integer.parseInt(numversion);
		} catch (Exception e) {
			throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.wrongversion");
		}
		try {
			Api apiDelete = apiRepository.findByIdentificationAndNumversion(identificacion, version).get(0);
			if (authorized(apiDelete, token)) {
				// Se eliminan las Operaciones
				removeOperaciones(apiDelete);

				// Se eliminan la autorizacion
				removeAutorizacion(apiDelete);

				// Se elimina el api
				apiRepository.delete(apiDelete);
			} else {
				throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoPermisosOperacion");
			}
		} catch (Exception e) {
			throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoApi");
		}
	}

	private void createOperations(ArrayList<OperacionDTO> operaciones, Api api) {
		for (OperacionDTO operacionDTO : operaciones) {
			ApiOperation operacion = OperacionFIQL.copyProperties(operacionDTO);
			operacion.setApi(api);
			apiOperationRepository.saveAndFlush(operacion);

			// Se crean los Headers
			createHeaders(operacion, operacionDTO.getHeaders());
			// Se crean los QueryParams
			createQueryParams(operacion, operacionDTO.getQueryParams());
		}
	}

	private void updateOperaciones(ArrayList<OperacionDTO> operacionesDTO, Api api) {
		removeOperaciones(api);
		createOperations(operacionesDTO, api);
	}

	private void removeOperaciones(Api api) {
		List<ApiOperation> operaciones = apiOperationRepository.findByApiOrderByOperationDesc(api);
		for (ApiOperation operacion : operaciones) {
			apiOperationRepository.delete(operacion);
		}
	}

	private void createHeaders(ApiOperation operacion, ArrayList<ApiHeaderDTO> headersDTO) {
		for (ApiHeaderDTO headerDTO : headersDTO) {
			ApiHeader apiHeader = HeaderFIQL.copyProperties(headerDTO);
			apiHeader.setApiOperation(operacion);
			apiHeaderRepository.saveAndFlush(apiHeader);
		}
	}

	/*
	 * private void createHeaders(ApiOperation operacion, ArrayList<ApiHeaderDTO>
	 * headersDTO) { Set<ApiHeader> apiheaders = new HashSet<ApiHeader>();
	 * 
	 * for (ApiHeaderDTO headerDTO : headersDTO) { ApiHeader apiHeader =
	 * HeaderFIQL.copyProperties(headerDTO); apiheaders.add(apiHeader); }
	 * operacion.setApiheaders(apiheaders);
	 * apiOperationRepository.saveAndFlush(operacion);
	 * 
	 * }
	 */

	private void createQueryParams(ApiOperation operacion, ArrayList<ApiQueryParameterDTO> queryParamsDTO) {
		for (ApiQueryParameterDTO queryParamDTO : queryParamsDTO) {
			ApiQueryParameter apiQueryParam = QueryParameterFIQL.copyProperties(queryParamDTO);
			apiQueryParam.setApiOperation(operacion);

			apiQueryParameterRepository.saveAndFlush(apiQueryParam);

		}
	}

	private void createAutenticacion(AutenticacionDTO autenticacionDTO, Api api) {
		if (autenticacionDTO != null) {
			ApiAuthentication authentication = AutenticacionFIQL.copyProperties(autenticacionDTO);
			authentication.setApi(api);
			apiAuthenticationRepository.saveAndFlush(authentication);

			// Se crean los parametros
			for (ArrayList<AutenticacionAtribDTO> parametroDTO : autenticacionDTO.getAutParametros()) {
				ApiAuthenticationParameter parameter = new ApiAuthenticationParameter();
				parameter.setApiAuthentication(authentication);
				apiAuthenticationParameterRepository.saveAndFlush(parameter);
				// Se crean los atributos
				for (AutenticacionAtribDTO atribDTO : parametroDTO) {
					ApiAuthenticationAttribute autparametroatrib = new ApiAuthenticationAttribute();
					autparametroatrib.setName(atribDTO.getName());
					autparametroatrib.setValue(atribDTO.getValue());
					autparametroatrib.setApiAuthenticationParameter(parameter);
					apiAuthenticationAttributeRepository.saveAndFlush(autparametroatrib);

				}
			}
		}
	}

	private void updateAutenticacion(AutenticacionDTO autenticacionDTO, Api apiUpdate) {
		removeAutorizacion(apiUpdate);
		createAutenticacion(autenticacionDTO, apiUpdate);

	}

	private void removeAutorizacion(Api apiDelete) {

	}

	public List<Api> findApisByUser(String userId, String token) {
		List<Api> apis = null;
		apis = apiRepository.findByUser(userService.getUser(userId));
		return apis;
	}

	public ApiFIQL getApiFIQL() {
		return apiFIQL;
	}

	public void setApiFIQL(ApiFIQL apiFIQL) {
		this.apiFIQL = apiFIQL;
	}

	public Api getApi(String identificacionApi) {
		Api api = null;
		List<Api> apis = apiRepository.findByIdentification(identificacionApi);
		for (Api apiAux : apis) {
			if (apiAux.getState().equals("publicada")) {
				api = apiAux;
			}
		}
		if (api == null) {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.ApiNoPublicada");
		}
		return api;
	}

	public Api getApiMaxVersion(String identificacionApi) {
		Api api = null;
		List<Api> apis = apiRepository.findByIdentification(identificacionApi);
		for (Api apiAux : apis) {
			if (api == null || api.getNumversion() < apiAux.getNumversion()) {
				api = apiAux;
			}
		}
		if (api == null) {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.ApiNoExiste");
		}
		return api;
	}

	public boolean authorized(Api api, String tokenUsuario) {
		User user = userService.getUserByToken(tokenUsuario);
		return true;
	}
}