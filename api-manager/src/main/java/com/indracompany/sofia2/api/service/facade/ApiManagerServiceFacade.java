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
package com.indracompany.sofia2.api.service.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.service.ontology.OntologyService;
import com.indracompany.sofia2.service.token.TokenService;
import com.indracompany.sofia2.service.user.UserService;



@Service
public class ApiManagerServiceFacade {

	@Autowired
	protected UserService userService;
	
	@Autowired(required=false)
	protected TokenService tokenService;
	
	@Autowired
	protected OntologyService ontologyService;
	
	@Autowired 
	protected ApiManagerServiceFacade serviceFacade;

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public TokenService getTokenService() {
		return tokenService;
	}

	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	public OntologyService getOntologyService() {
		return ontologyService;
	}

	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	public ApiManagerServiceFacade getServiceFacade() {
		return serviceFacade;
	}

	public void setServiceFacade(ApiManagerServiceFacade serviceFacade) {
		this.serviceFacade = serviceFacade;
	}
	
	
	
}
