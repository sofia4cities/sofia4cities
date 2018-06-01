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
package com.indracompany.sofia2.api.rest.api.fiql;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.api.rest.api.dto.ApiDTO;
import com.indracompany.sofia2.api.rest.api.dto.AutenticacionAtribDTO;
import com.indracompany.sofia2.api.rest.api.dto.AutenticacionDTO;
import com.indracompany.sofia2.api.rest.api.dto.OperacionDTO;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.ApiAuthentication;
import com.indracompany.sofia2.config.model.ApiAuthenticationAttribute;
import com.indracompany.sofia2.config.model.ApiAuthenticationParameter;
import com.indracompany.sofia2.config.model.ApiOperation;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ApiAuthenticationRepository;
import com.indracompany.sofia2.config.repository.ApiOperationRepository;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApiFIQL {

	@Autowired
	private UserService userService;
	@Autowired
	private OntologyService ontologyService;

	@Autowired
	private ApiOperationRepository operationRepository;

	@Autowired
	private ApiAuthenticationRepository authenticationRepository;

	public static final String API_PUBLICA = "PUBLIC";
	public static final String API_PRIVADA = "PRIVATE";

	private  Locale locale = LocaleContextHolder.getLocale();

	private  DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	public List<ApiDTO> toApiDTO(List<Api> apis) {
		List<ApiDTO> apisDTO = new ArrayList<ApiDTO>();
		for (Api api : apis) {
			apisDTO.add(toApiDTO(api));
		}
		return apisDTO;
	}

	public ApiDTO toApiDTO(Api api) {
		ApiDTO apiDTO = new ApiDTO();

		apiDTO.setIdentification(api.getIdentification());
		apiDTO.setVersion(api.getNumversion());
		if (api.isPublic()) {
			apiDTO.setType(API_PUBLICA);
		} else {
			apiDTO.setType(API_PRIVADA);
		}
		apiDTO.setCategory(api.getCategory().toString());
		if (api.getEndpointExt() != null && !api.getEndpointExt().equals("")) {
			apiDTO.setExternalApi(true);
		} else {
			apiDTO.setExternalApi(false);
		}
		if (api.getOntology() != null) {
			apiDTO.setOntologyId(api.getOntology().getId());
		}
		apiDTO.setEndpoint(api.getEndpoint());
		apiDTO.setEndpointExt(api.getEndpointExt());
		apiDTO.setDescription(api.getDescription());
		apiDTO.setMetainf(api.getMetaInf());
		apiDTO.setImageType(api.getImageType());
		apiDTO.setStatus(api.getState());
		apiDTO.setCreationDate(df.format(api.getCreatedAt()));
		if (api.getUser() != null) {
			apiDTO.setUserId(api.getUser().getUserId());
		}

		// Se copian las Operaciones
		ArrayList<OperacionDTO> operacionesDTO = new ArrayList<OperacionDTO>();
		List<ApiOperation> operaciones = operationRepository.findByApiOrderByOperationDesc(api);
		for (ApiOperation operacion : operaciones) {
			OperacionDTO operacionDTO = OperationFIQL.toOperacionDTO(operacion);
			operacionesDTO.add(operacionDTO);
		}

		// Se copia el Objeto Autenticacion
		ApiAuthentication autenticacion = null;
		List<ApiAuthentication> autenticaciones = authenticationRepository.findAllByApi(api);
		if (autenticaciones != null && autenticaciones.size() > 0) {
			autenticacion = autenticaciones.get(0);
			AutenticacionDTO autenticacionDTO = AuthenticationFIQL.toAutenticacionDTO(autenticacion);

			// Se copian los parámetros
			Set<ApiAuthenticationParameter> parametros = autenticacion.getApiAuthenticationParameters();
			ArrayList<ArrayList<AutenticacionAtribDTO>> parametrosDTO = new ArrayList<ArrayList<AutenticacionAtribDTO>>();
			for (ApiAuthenticationParameter parametro : parametros) {
				// Se copian los atributos
				ArrayList<AutenticacionAtribDTO> atributosDTO = new ArrayList<AutenticacionAtribDTO>();
				for (ApiAuthenticationAttribute atrib : parametro.getApiautenticacionattribs()) {
					AutenticacionAtribDTO atribDTO = new AutenticacionAtribDTO();
					atribDTO.setName(atrib.getName());
					atribDTO.setValue(atrib.getValue());
					atributosDTO.add(atribDTO);
				}
				parametrosDTO.add(atributosDTO);
			}
			autenticacionDTO.setAuthParameters(parametrosDTO);

			apiDTO.setAuthentication(autenticacionDTO);
		}

		apiDTO.setOperations(operacionesDTO);

		return apiDTO;
	}

	public Api copyProperties(ApiDTO apiDTO, User user) {
		Api api = new Api();

		api.setIdentification(apiDTO.getIdentification());
		api.setNumversion(apiDTO.getVersion());
		
		api.setPublic(apiDTO.getIsPublic());
		
		api.setCategory(Api.ApiCategories.valueOf(apiDTO.getCategory()));

		if (apiDTO.getOntologyId() != null && !apiDTO.getOntologyId().equals("")) {
			Ontology ont = ontologyService.getOntologyById(apiDTO.getOntologyId(), user.getUserId());
			api.setOntology(ont);
		}

		api.setApiType(apiDTO.getType());
		api.setEndpoint(apiDTO.getEndpoint());
		api.setEndpointExt(apiDTO.getEndpointExt());
		api.setDescription(apiDTO.getDescription());
		api.setMetaInf(apiDTO.getMetainf());
		api.setState(apiDTO.getStatus());

		try {
			if (apiDTO.getCreationDate() != null && !apiDTO.getCreationDate().equals("")) {
				api.setCreatedAt(df.parse(apiDTO.getCreationDate()));
			}
		} catch (ParseException ex) {
			throw new IllegalArgumentException("com.indra.sofia2.web.api.services.WrongDateFormat");
		}

		if (apiDTO.getUserId() != null) {
			User userApiDTO = userService.getUser(apiDTO.getUserId());
			if (userApiDTO != null) {
				api.setUser(userApiDTO);
			}
		}

		return api;
	}

	public Api copyProperties(Api apiUpdate, Api api) {
		apiUpdate.setIdentification(api.getIdentification());
		apiUpdate.setNumversion(api.getNumversion());
		apiUpdate.setPublic(api.isPublic());
		apiUpdate.setCategory(api.getCategory());

		apiUpdate.setOntology(api.getOntology());

		apiUpdate.setEndpoint(api.getEndpoint());
		apiUpdate.setEndpointExt(api.getEndpointExt());
		apiUpdate.setDescription(api.getDescription());
		apiUpdate.setMetaInf(api.getMetaInf());
		apiUpdate.setState(api.getState());
		apiUpdate.setCreatedAt(api.getCreatedAt());

		return apiUpdate;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ApiOperationRepository getOperationRepository() {
		return operationRepository;
	}

	public void setOperationRepository(ApiOperationRepository operationRepository) {
		this.operationRepository = operationRepository;
	}

	public OntologyService getOntologyService() {
		return ontologyService;
	}

	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	public ApiAuthenticationRepository getAuthenticationRepository() {
		return authenticationRepository;
	}

	public void setAuthenticationRepository(ApiAuthenticationRepository authenticationRepository) {
		this.authenticationRepository = authenticationRepository;
	}

}
