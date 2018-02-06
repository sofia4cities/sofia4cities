/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.api.rest.api;

import java.util.Locale;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.rest.api.dto.ApiSuscripcionDTO;
import com.indracompany.sofia2.api.rest.api.fiql.ApiSuscripcionFIQL;
import com.indracompany.sofia2.api.service.api.ApiManagerService;

import lombok.extern.slf4j.Slf4j;

@Component("apiSuscripcionRestServiceImpl")
@Slf4j
public class ApiSuscriptionRestServiceImpl implements ApiSuscriptionRestService {
	
	
	
	Locale locale = LocaleContextHolder.getLocale();

	@Autowired
	private ApiManagerService apiManagerService;
	
	@Autowired
	private ApiSuscripcionFIQL apiSuscripcionFIQL;
	
	@Override
	public Response getApiSuscripciones(String identificacionApi, String tokenUsuario) throws Exception {
		return Response.ok(apiSuscripcionFIQL.toApiSuscripcionDTO(apiManagerService.findApiSuscriptions(identificacionApi, tokenUsuario))).build();
	}

	@Override
	public Response getApiSuscripcionesUsuario(String identificacionUsuario, String tokenUsuario) throws Exception {
		return Response.ok(apiSuscripcionFIQL.toApiSuscripcionDTO(apiManagerService.findApiSuscripcionesUser(identificacionUsuario))).build();
	}
	
	@Override
	public Response autorize(ApiSuscripcionDTO suscripcion, String tokenUsuario) throws Exception {
		apiManagerService.createSuscripcion(apiSuscripcionFIQL.copyProperties(suscripcion));
		Object parametros[]={suscripcion.getApiIdentification(),suscripcion.getUserId()};
		return Response.ok(parametros).type(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response autorizeUpdate(ApiSuscripcionDTO suscripcion, String tokenUsuario) throws Exception {
		apiManagerService.updateSuscripcion(apiSuscripcionFIQL.copyProperties(suscripcion));
		Object parametros[]={suscripcion.getApiIdentification(),suscripcion.getUserId()};
		return Response.ok(parametros).type(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response deleteAutorizacion(ApiSuscripcionDTO suscripcion, String tokenUsuario) throws Exception {
		apiManagerService.removeSuscripcionByUserAndAPI(apiSuscripcionFIQL.copyProperties(suscripcion));
		Object parametros[]={suscripcion.getApiIdentification(),suscripcion.getUserId()};
		return Response.ok(parametros).type(MediaType.APPLICATION_JSON).build();
	}

}
