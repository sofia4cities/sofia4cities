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
import com.indracompany.sofia2.service.ontology.OntologyService;
import com.indracompany.sofia2.service.user.UserService;

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
	
	public static final String API_PUBLICA="PUBLICA";
	public static final String API_PRIVADA="PRIVADA";
	

	static Locale locale = LocaleContextHolder.getLocale();
	
	static DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	

	public  List<ApiDTO> toApiDTO(List<Api> apis) {
		List<ApiDTO> apisDTO = new ArrayList<ApiDTO>();
		for (Api api : apis) {
			apisDTO.add(toApiDTO(api));
		}
		return apisDTO;
	}
	
	public  ApiDTO toApiDTO(Api api) {
		ApiDTO apiDTO = new ApiDTO();
		
		apiDTO.setIdentificacion(api.getIdentification());
		apiDTO.setNumversion(api.getNumversion());
		if (api.isPublic()){
			apiDTO.setTipo(API_PUBLICA);
		} else {
			apiDTO.setTipo(API_PRIVADA);
		}
		apiDTO.setCategoria(api.getCategory());
		if (api.getEndpointExt()!=null && !api.getEndpointExt().equals("")){
			apiDTO.setApiexterna(true);
		} else {
			apiDTO.setApiexterna(false);
		}
		if (api.getOntologyId()!=null){
			apiDTO.setOntologiaId(api.getOntologyId().getId());
		}
		apiDTO.setEndpoint(api.getEndpoint());
		apiDTO.setEndpointExt(api.getEndpointExt());
		apiDTO.setDescripcion(api.getDescription());
		apiDTO.setMetainf(api.getMetaInf());
		apiDTO.setTipoimagen(api.getImageType());
		apiDTO.setEstado(api.getState());
		apiDTO.setFechaalta(df.format(api.getCreatedAt()));
		if (api.getUserId()!=null && !api.getUserId().equals("")){
			User user = userService.getUser(api.getUserId());
			apiDTO.setUsuarioId(user.getUserId());
		}
		
		// Se copian las Operaciones
		ArrayList<OperacionDTO> operacionesDTO = new ArrayList<OperacionDTO>();
		List<ApiOperation> operaciones = operationRepository.findByApiIdOrderByOperationDesc(api);
		for (ApiOperation operacion : operaciones) {
			OperacionDTO operacionDTO = OperacionFIQL.toOperacionDTO(operacion);
			operacionesDTO.add(operacionDTO);
		}
		
		// Se copia el Objeto Autenticacion
		ApiAuthentication autenticacion = null;
		List<ApiAuthentication> autenticaciones = authenticationRepository.findAllByApiId(api);
		if (autenticaciones!=null && autenticaciones.size()>0){
			autenticacion= autenticaciones.get(0);
			AutenticacionDTO autenticacionDTO = AutenticacionFIQL.toAutenticacionDTO(autenticacion);
			
			// Se copian los parámetros
			Set<ApiAuthenticationParameter> parametros = autenticacion.getApiAuthenticationParameters();
			ArrayList<ArrayList<AutenticacionAtribDTO>> parametrosDTO = new ArrayList<ArrayList<AutenticacionAtribDTO>>();
			for (ApiAuthenticationParameter parametro : parametros) {
				// Se copian los atributos
				ArrayList<AutenticacionAtribDTO> atributosDTO = new ArrayList<AutenticacionAtribDTO>();
				for (ApiAuthenticationAttribute atrib : parametro.getApiautenticacionattribs()) {
					AutenticacionAtribDTO atribDTO = new AutenticacionAtribDTO();
					atribDTO.setNombre(atrib.getName());
					atribDTO.setValor(atrib.getValue());
					atributosDTO.add(atribDTO);
				}
				parametrosDTO.add(atributosDTO);
			}
			autenticacionDTO.setAutParametros(parametrosDTO);
			
			apiDTO.setAutenticacion(autenticacionDTO);
		}
		
		apiDTO.setOperaciones(operacionesDTO);
		
		return apiDTO;
	}

	public  Api copyProperties(ApiDTO apiDTO) {
		Api api = new Api();

		api.setIdentification(apiDTO.getIdentificacion());
		api.setNumversion(apiDTO.getNumversion());
		if (apiDTO.getTipo().equals(API_PUBLICA)){
			api.setPublic(true);
		} else {
			api.setPublic(false);
		}
		api.setCategory(apiDTO.getCategoria());

		if (apiDTO.getOntologiaId()!=null && !apiDTO.getOntologiaId().equals("")){
			Ontology ont = ontologyService.getOntologyById(apiDTO.getOntologiaId());
			api.setOntologyId(ont);
		}
		
		api.setEndpoint(apiDTO.getEndpoint());
		api.setEndpointExt(apiDTO.getEndpointExt());
		api.setDescription(apiDTO.getDescripcion());
		api.setMetaInf(apiDTO.getMetainf());
		api.setState(apiDTO.getEstado());
	
		try {
			if (apiDTO.getFechaalta()!=null && !apiDTO.getFechaalta().equals("")){
				api.setCreatedAt(df.parse(apiDTO.getFechaalta()));
			}
		} catch (ParseException ex) {
			throw new IllegalArgumentException( "com.indra.sofia2.web.api.services.WrongDateFormat");
		}
		
		if (apiDTO.getUsuarioId()!=null){
			User user = userService.getUser(api.getUserId());
			if (user!=null){
				api.setUserId(user.getUserId());
			}
		}
		
		return api;
	}

	public  Api copyProperties(Api apiUpdate, Api api) {
		apiUpdate.setIdentification(api.getIdentification());
		apiUpdate.setNumversion(api.getNumversion());
		apiUpdate.setPublic(api.isPublic());
		apiUpdate.setCategory(api.getCategory());
		
		apiUpdate.setOntologyId(api.getOntologyId());
		
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
