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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.api.rest.api.dto.ApiSuscripcionDTO;
import com.indracompany.sofia2.api.service.api.ApiServiceRest;
import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.ApiSuscription;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ApiRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApiSuscripcionFIQL {
	
	
	@Autowired
	private ApiServiceRest apiServiceRest;
	
	static Locale locale = LocaleContextHolder.getLocale();
	
	static DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	

	public  List<ApiSuscripcionDTO> toApiSuscripcionDTO(List<ApiSuscription> suscripciones) {
		List<ApiSuscripcionDTO> suscripcionesDTO = new ArrayList<ApiSuscripcionDTO>();
		for (ApiSuscription suscripcion : suscripciones) {
			suscripcionesDTO.add(toApiSuscripcionDTO(suscripcion));
		}
		return suscripcionesDTO;
	}
	
	private  ApiSuscripcionDTO toApiSuscripcionDTO(ApiSuscription suscripcion) {
		ApiSuscripcionDTO suscripcionDTO = new ApiSuscripcionDTO();
		String id = suscripcion.getApi().getIdentification();
		suscripcionDTO.setApiIdentification(id);
		User user = apiServiceRest.getUser(suscripcion.getUser().getUserId());
		
		suscripcionDTO.setUserId(user.getUserId());
		if (suscripcion.getInitDate()!=null){
			suscripcionDTO.setInitDate(df.format(suscripcion.getInitDate()));
		}
		if (suscripcion.getInitDate()!=null){
			suscripcionDTO.setEndDate(df.format(suscripcion.getEndDate()));
		}
		suscripcionDTO.setActive(suscripcion.getIsActive());
		return suscripcionDTO;
	}

	public  ApiSuscription copyProperties(ApiSuscripcionDTO suscripcion) {
		ApiSuscription apiSuscripcion = new ApiSuscription();
		apiSuscripcion.setApi(apiServiceRest.getApi(suscripcion.getApiIdentification()));
		
		User user = apiServiceRest.getUser(suscripcion.getUserId());
		
		if (user!=null){
			apiSuscripcion.setUser(user);
		} else {
			throw new IllegalArgumentException("WrongUser");
		}
		
		apiSuscripcion.setIsActive(suscripcion.getActive());
		try {
			if (suscripcion.getInitDate()!=null && !suscripcion.getInitDate().equals("")){
				apiSuscripcion.setInitDate(df.parse(suscripcion.getInitDate()));
			}
		} catch (ParseException ex) {
			throw new IllegalArgumentException("WrongInitDateFormat");
		}
		try {
			if (suscripcion.getInitDate()!=null && !suscripcion.getInitDate().equals("")){
				apiSuscripcion.setEndDate(df.parse(suscripcion.getEndDate()));
			}
		} catch (ParseException ex) {
			throw new IllegalArgumentException("WrongFinishDateFormat");
		}
		
		return apiSuscripcion;
	}
	
	
}
