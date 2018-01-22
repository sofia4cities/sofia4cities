package com.indracompany.sofia2.controlpanel.utils;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppWebUtils {

	@Autowired
	private MessageSource messageSource;
	
	public Authentication getAuthentication() {
		return  SecurityContextHolder.getContext().getAuthentication();
	}
	
	public String getRole() {
		Authentication auth = getAuthentication();
		if (auth==null) return null;
		return auth.getAuthorities().toArray()[0].toString();
	}

	public String getMessage(String key,String valueDefault){
		try{
			return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
		}catch (Exception e){
			log.debug("Key:"+key+" not found. Returns:"+valueDefault);
			return valueDefault;
		}
	}

}
