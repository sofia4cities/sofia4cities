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
package com.indracompany.sofia2.controlpanel.controller.ontology;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyUserAccess;
import com.indracompany.sofia2.config.model.OntologyUserAccessType;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

@RunWith(MockitoJUnitRunner.class)
public class OntologyControllerTest {
 
	@Mock
    private OntologyService ontologyService;
	@Mock
	private AppWebUtils utils;
	
	@InjectMocks
    private OntologyController ontologyController;
 
    private MockMvc mockMvc;
 
    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(ontologyController).build();
    }
    
    private OntologyUserAccess ontologyUserAccessCreator(String ontologyId, String userOntologyOwnerId,
    		String userIdToGiveAccessId, String accessTypeName, String accessId) {
    	
    	User userAuthorized = new User();
    	userAuthorized.setUserId(userIdToGiveAccessId);
    	
    	User userOntologyOwner = new User();
    	userOntologyOwner.setUserId(userOntologyOwnerId);
    	
    	Ontology ontology = new Ontology();  
    	ontology.setId(ontologyId);
    	ontology.setUser(userOntologyOwner);
    	
    	OntologyUserAccessType type = new OntologyUserAccessType();
    	type.setName(accessTypeName);
    	
    	OntologyUserAccess access = new OntologyUserAccess();
    	access.setId(accessId);
    	access.setOntology(ontology);
    	access.setUser(userAuthorized);
    	access.setOntologyUserAccessType(type);
    	
    	return access;
    }
    
    @Test
    public void when__correctParametersAreSentToCreate__OntologyAccessIsCreated() throws Exception {
    	
    	OntologyUserAccess access = ontologyUserAccessCreator("ontologyId", "administrator", "user", "ALL", "accessId");
    	String sessionUserId = "administrator";
    	
    	given(utils.getUserId()).willReturn(sessionUserId);
    	given(utils.isAdministrator()).willReturn(true);
    	
    	given(ontologyService.getOntologyById(access.getOntology().getId())).willReturn(access.getOntology());
    	given(ontologyService.getOntologyUserAccessByOntologyIdAndUserId(access.getOntology().getId(), access.getUser().getUserId())).willReturn(access);
    	
		mockMvc.perform(post("/ontologies/authorization")
							.param("accesstype", access.getOntologyUserAccessType().getName())
							.param("ontology", access.getOntology().getId())
							.param("user", access.getUser().getUserId()))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id", is(access.getId())))
				.andExpect(jsonPath("$.userId", is(access.getUser().getUserId())))
				.andExpect(jsonPath("$.typeName", is(access.getOntologyUserAccessType().getName())));
    }
    
    
    @Test
    public void when__sessionUserIsNotAdminOrOwner__ontologyAccessCreationIsForbidden() throws Exception {
    	OntologyUserAccess access = ontologyUserAccessCreator("ontologyId", "somebody", "user", "ALL", "accessId");
    	String sessionUserId = "unknown";
    	
    	given(utils.getUserId()).willReturn(sessionUserId);
    	given(utils.isAdministrator()).willReturn(false);
    	
    	given(ontologyService.getOntologyById(access.getOntology().getId())).willReturn(access.getOntology());
    	given(ontologyService.getOntologyUserAccessByOntologyIdAndUserId(access.getOntology().getId(), access.getUser().getUserId())).willReturn(access);
    	
    	mockMvc.perform(post("/ontologies/authorization")
						.param("accesstype", access.getOntologyUserAccessType().getName())
						.param("ontology", access.getOntology().getId())
						.param("user", access.getUser().getUserId()))
				.andExpect(status().isForbidden());
    }
    
    @Test
    public void when__correctParametersAreSentToDelete__OntologyAccessIsDeleted () throws Exception {
    	
    	OntologyUserAccess access = ontologyUserAccessCreator("ontologyId", "somebody", "user", "ALL", "accessId");
    	
    	String sessionUserId = "somebody";
    	
    	given(utils.getUserId()).willReturn(sessionUserId);
    	given(utils.isAdministrator()).willReturn(false);
    	
    	given(ontologyService.getOntologyUserAccessById(access.getId())).willReturn(access);
    	
    	mockMvc.perform(post("/ontologies/authorization/delete")
						.param("id", access.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.status", is("ok")));
    }
    
    @Test
    public void when__sessionUserIsNotAdminOrOwner__ontologyAccessDeleteIsForbidden() throws Exception {

    	OntologyUserAccess access = ontologyUserAccessCreator("ontologyId", "somebody", "user", "ALL", "accessId");
    	
    	String sessionUserId = "unknown";
    	
    	given(utils.getUserId()).willReturn(sessionUserId);
    	given(utils.isAdministrator()).willReturn(false);
    	
    	given(ontologyService.getOntologyUserAccessById(access.getId())).willReturn(access);
    	
    	mockMvc.perform(post("/ontologies/authorization/delete")
						.param("id", access.getId()))
				.andExpect(status().isForbidden());
    }
    

}
