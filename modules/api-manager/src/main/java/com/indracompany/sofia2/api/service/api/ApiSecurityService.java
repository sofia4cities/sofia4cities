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
package com.indracompany.sofia2.api.service.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.ApiSuscription;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyUserAccess;
import com.indracompany.sofia2.config.model.OntologyUserAccessType;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserApi;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.OntologyUserAccessRepository;
import com.indracompany.sofia2.config.services.user.UserService;


@Service
public class ApiSecurityService {

	@Autowired
	ApiManagerService apiManagerService;
	
	@Autowired
	ApiServiceRest apiServiceRest;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OntologyUserAccessRepository ontologyUserAccessRepository;
	
	public boolean isAdmin(final User user) {
		return (Role.Type.ROLE_ADMINISTRATOR.name().equalsIgnoreCase(user.getRole().getId()));
	}
	
	public boolean isCol(final User user) {
		return (Role.Type.ROLE_OPERATIONS.name().equalsIgnoreCase(user.getRole().getId()));
	}
	
	public boolean isUser(final User user) {
		return (Role.Type.ROLE_USER.name().equalsIgnoreCase(user.getRole().getId()));
	}
	
	public User getUser(String userId) {
		return userService.getUser(userId);
	}
	
	public User getUserByApiToken(String token) {
		return userService.getUserByToken(token);
	}

	public UserToken getUserToken(User userId, String token) {
		return this.userService.getUserToken(userId.getUserId(), token);
	}
	
/*	public  boolean authorized(Api api, String tokenUsuario, String roleName) {
		User user = getUserByApiToken(tokenUsuario);
		Role role = user.getRole();
		return role.getId().equalsIgnoreCase(roleName);
	}*/
	
	public boolean authorized(Api api, String tokenUsuario) {
		User user = getUserByApiToken(tokenUsuario);
		if (isAdmin(user) || user.getUserId().equals(api.getUser().getUserId())){
			return true;
		} else {
			throw new AuthorizationServiceException("com.indra.sofia2.web.api.services.NoPermisosOperacionUsuario") ;
		}
	}
	
	
	
	public boolean checkOntologyOperationPermission(Api api, User user, String query, String queryType){
		return true;
	}
	
	public boolean checkUserApiPermission(Api api, User user) {

		if(api==null || user==null) return false;
		
		boolean autorizado=false;
		
		//Si es administrador u otro rol, pero o propietario o esta suscrito al API
		if (Role.Type.ROLE_ADMINISTRATOR.name().equalsIgnoreCase(user.getRole().getId())){//Rol administrador
			autorizado=true;
			
		}else if(api.getUser().getUserId()!=null && api.getUser().getUserId().equals(user.getUserId())){//Otro rol pero propietario
			autorizado=true;
		}else{
			//No administrador, ni propietario pero está suscrito
			UserApi suscriptionApi=null;
			try{
				suscriptionApi=apiServiceRest.findApiSuscriptions(api, user);
			} catch (Exception e) {}
			
			if(suscriptionApi!=null){
				autorizado=true;
			}
		}
		
		return autorizado;
	}
	
	public boolean checkApiAvailable(Api api, User user){
		
		if (api==null || user == null) return false;
		
		boolean can = api.getState().name().equalsIgnoreCase(Api.ApiStates.CREATED.name()) && (api.getUser().getUserId().equals(user.getUserId()));
		if (can) return true;
		else {
			String state = api.getState().name();
			can =  (state.equalsIgnoreCase(Api.ApiStates.PUBLISHED.name()) || 
					state.equalsIgnoreCase(Api.ApiStates.DEPRECATED.name()) ||
					state.equalsIgnoreCase(Api.ApiStates.DEVELOPMENT.name()) );
			return can;
		}
		
	}
	
	public Boolean checkRole(User user, Ontology ontology, boolean insert){
		
		Boolean authorize=false;
		//If the role is Manager always allows the operation
		if (Role.Type.ROLE_ADMINISTRATOR.name().equalsIgnoreCase(user.getRole().getId())){//Rol administrador
			authorize=true;
			
		}else{
			
			if(ontology.getUser().getUserId().equals(user.getUserId())){//Si es el propietario
				return true;
			}
						
			//If other role, it checks whether the user is associated with ontology
			List<OntologyUserAccess> uo = ontologyUserAccessRepository.findByUser(user);
			if(uo==null){
				return false;
			}
			
			for (OntologyUserAccess usuarioOntologia : uo){
				if (usuarioOntologia.getOntology().getId().equals(ontology.getId())){
					
					String name =  usuarioOntologia.getOntologyUserAccessType().getName();
					
					if (name!=null) {
						
						if (OntologyUserAccessType.Type.ALL.name().equalsIgnoreCase(name)) {
							authorize=true;
							break;
						}
						else if (OntologyUserAccessType.Type.INSERT.name().equalsIgnoreCase(name)) {
							if (insert) {
								authorize=true;
								break;
							}
							else {
								authorize=false;
								break;
							}
						}
						else if  (OntologyUserAccessType.Type.QUERY.name().equalsIgnoreCase(name)) {
							authorize=true;
							break;
						}
					}
					
					else {
						authorize=false;
						break;
					}
				}
			}
			
		}
		return authorize;
	}
	
	
}
