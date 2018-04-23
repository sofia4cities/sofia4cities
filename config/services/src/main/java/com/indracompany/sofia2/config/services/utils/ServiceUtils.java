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
package com.indracompany.sofia2.config.services.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.User;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ServiceUtils {

	@Autowired
	private MessageSource messageSource;
	
	public static final String AUDIT_COLLECTION_NAME = "Audit_";
	
	public Authentication getAuthentication() {
		return  SecurityContextHolder.getContext().getAuthentication();
	}
	
	public String getUserId() {
		Authentication auth = getAuthentication();
		if (auth==null) return null;
		return auth.getName();
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
	
	public void setSessionAttribute(HttpServletRequest request, String name,Object o)
	{
		WebUtils.setSessionAttribute(request, name, o);
	}
	
	public String jsonStringToString(String json)
	{
		
		ObjectMapper objectMapper = new ObjectMapper();
		String formattedJson=null;
		
		try {
			JsonNode tree = objectMapper.readValue(json,JsonNode.class);
			formattedJson = tree.toString();
		} catch (Exception e) {
			log.error("Exception reached "+e.getMessage(),e);
		}
		
		return formattedJson;
	}
	
	public static User getUser(){
		User user=null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth instanceof AnonymousAuthenticationToken){
			return null;
		}
		/*if (auth!=null){
			if (auth.getDetails()!=null){
			if (auth instanceof SocialAuthenticationToken){
					user = (User)((SocialAuthenticationToken)auth).getPrincipal();
				}else{
					user = ((AuthenticationContainer)((UsernamePasswordAuthenticationToken)auth).getDetails()).getUserDetail();
					if (user==null){
						user = User.findUser((((AuthenticationContainer)((UsernamePasswordAuthenticationToken)auth).getDetails()).getUserDetail()).getId());
					}
				}
			}
		}*/
		
		return null;
	}
	
	public static String getAuditCollectionName (String userId) {		
		return AUDIT_COLLECTION_NAME + userId;
	}
}
