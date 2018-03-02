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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyUserAccess;
import com.indracompany.sofia2.config.model.OntologyUserAccessType;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.controlpanel.utils.AppWebUtils;

@RunWith(MockitoJUnitRunner.class)
public class OntologyControllerTest {
 
	@Mock
    private OntologyService ontologyService;
	@Mock
	private AppWebUtils utils;
	@Mock
	private UserService userService;
	
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
    
    private Ontology ontologyCreator (String ontologyId, String userId) {
    	User userOntologyOwner = new User();
    	userOntologyOwner.setUserId(userId);
    	
    	Ontology ontology = new Ontology();  
    	ontology.setId(ontologyId);
    	ontology.setUser(userOntologyOwner);
    	
    	return ontology;
    }
    
    private OntologyUserAccess ontologyUserAccessCreator(String ontologyId, String userOntologyOwnerId,
    		String userIdToGiveAccessId, String accessTypeName, String accessId) {
    	
    	User userAuthorized = new User();
    	userAuthorized.setUserId(userIdToGiveAccessId);
    	
    	OntologyUserAccessType type = new OntologyUserAccessType();
    	type.setName(accessTypeName);
    	
    	OntologyUserAccess access = new OntologyUserAccess();
    	access.setId(accessId);
    	access.setOntology(ontologyCreator(ontologyId, userOntologyOwnerId));
    	access.setUser(userAuthorized);
    	access.setOntologyUserAccessType(type);
    	
    	return access;
    }
    
    @Test
    public void when__correctParamentersAreSentToShow__OntologyShowsViewIsServed() throws Exception {
    	
    	Ontology ontology = ontologyCreator("ontologyId", "userOntology");
    	
    	User administrator = new User();
    	administrator.setUserId("administrador");
    	User user = new User();
    	user.setUserId("user");
    	User developer = new User();
    	developer.setUserId("developer");
    	List<User> users = new ArrayList<User>();
    	users.add(administrator);
    	users.add(user);
    	users.add(developer);
    	
    	List<OntologyUserAccess> accesses = new ArrayList<OntologyUserAccess>(2);
    	OntologyUserAccess access1 = ontologyUserAccessCreator("ontologyId", "userOntology", "user1", "ALL", "accessId1");
    	OntologyUserAccess access2 = ontologyUserAccessCreator("ontologyId", "userOntology", "user2", "ALL", "accessId2");
    	accesses.add(access1);
    	accesses.add(access2);
    	
    	given(ontologyService.getOntologyById(ontology.getId())).willReturn(ontology);
    	given(ontologyService.getOntologyUserAccesses(ontology.getId())).willReturn(accesses);
    	given(userService.getAllUsers()).willReturn(users);
    	
    	String sessionUserId = "userOntology";
    	
    	given(utils.getUserId()).willReturn(sessionUserId);
    	given(utils.isAdministrator()).willReturn(false);
    	
    	mockMvc.perform(get("/ontologies/show/"+ontology.getId()))
				.andExpect(status().isOk())
    			.andExpect(view().name("/ontologies/show"))
    			.andExpect(model().attribute("ontology", ontology))
    			//authorizations is serialized using OntologyUserAccessDTO, to check the content of 
    			//this attribute, it would be necessary to implement a custom Matcher<OntologyUserAccess>
    			.andExpect(model().attributeExists("authorizations")); 
    			
    }
    
    @Test
    public void when__correctParametersAreSentToCreate__OntologyAccessIsCreated() throws Exception {
    	
    	OntologyUserAccess access = ontologyUserAccessCreator("ontologyId", "userOntology", "user", "ALL", "accessId");
    	String sessionUserId = "userOntology";
    	
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
    	OntologyUserAccess access = ontologyUserAccessCreator("ontologyId", "userOntology", "user", "ALL", "accessId");
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
    	
    	OntologyUserAccess access = ontologyUserAccessCreator("ontologyId", "userOntology", "user", "ALL", "accessId");
    	
    	String sessionUserId = "userOntology";
    	
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

    	OntologyUserAccess access = ontologyUserAccessCreator("ontologyId", "userOntology", "user", "ALL", "accessId");
    	
    	String sessionUserId = "unknown";
    	
    	given(utils.getUserId()).willReturn(sessionUserId);
    	given(utils.isAdministrator()).willReturn(false);
    	
    	given(ontologyService.getOntologyUserAccessById(access.getId())).willReturn(access);
    	
    	mockMvc.perform(post("/ontologies/authorization/delete")
						.param("id", access.getId()))
				.andExpect(status().isForbidden());
    }
    
    @Test
    public void when__correctParametersAreSentToUpdate__OntologyAccessIsUpdated() throws Exception {
    	OntologyUserAccess accessOld = ontologyUserAccessCreator("ontologyId", "userOntology", "user", "ALL", "accessId");
    	OntologyUserAccess accessNew = ontologyUserAccessCreator("ontologyId", "userOntology", "user", "QUERY", "accessId");
    	
    	String sessionUserId = "userOntology";
    	
    	given(utils.getUserId()).willReturn(sessionUserId);
    	given(utils.isAdministrator()).willReturn(false);
    	
    	given(ontologyService.getOntologyUserAccessById(accessOld.getId())).willAnswer(new Answer<OntologyUserAccess>() {

    		private int count = 0;
			@Override
			public OntologyUserAccess answer(InvocationOnMock invocation) throws Throwable {
				switch (count) {
				case 0:
					count++;
					return accessOld;
				default:
					return accessNew;
				}
			}
		});
    	
    	mockMvc.perform(post("/ontologies/authorization/update")
    					.param("id", accessOld.getId())
    					.param("accesstype", accessOld.getOntologyUserAccessType().getName()))
    			.andExpect(status().isOk())
    			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
    			.andExpect(jsonPath("$.id", is(accessNew.getId())))
				.andExpect(jsonPath("$.userId", is(accessNew.getUser().getUserId())))
				.andExpect(jsonPath("$.typeName", is(accessNew.getOntologyUserAccessType().getName())));
    }
    
    @Test
    public void when__sessionUserIsNotAdminOrOwner__ontologyAccessUpdateIsForbidden() throws Exception {
    	OntologyUserAccess access = ontologyUserAccessCreator("ontologyId", "userOntology", "user", "ALL", "accessId");
    	
    	String sessionUserId = "unknown";
    	
    	given(utils.getUserId()).willReturn(sessionUserId);
    	given(utils.isAdministrator()).willReturn(false);
    	
    	given(ontologyService.getOntologyUserAccessById(access.getId())).willReturn(access);
    	
    	mockMvc.perform(post("/ontologies/authorization/update")
						.param("id", access.getId())
						.param("accesstype", access.getOntologyUserAccessType().getName()))
				.andExpect(status().isForbidden());
    }
    
    @Test
    public void when__parametersAreSentToListAuthorizations__OntologyAuthorizationsAreReturned() throws Exception {
    	Ontology ontology = ontologyCreator("ontologyId", "userOntology");
    	
    	List<OntologyUserAccess> accesses = new ArrayList<OntologyUserAccess>(2);
    	OntologyUserAccess access1 = ontologyUserAccessCreator("ontologyId", "userOntology", "user1", "ALL", "accessId1");
    	OntologyUserAccess access2 = ontologyUserAccessCreator("ontologyId", "userOntology", "user2", "ALL", "accessId2");
    	accesses.add(access1);
    	accesses.add(access2);
    	
    	given(ontologyService.getOntologyById(ontology.getId())).willReturn(ontology);
    	given(ontologyService.getOntologyUserAccesses(ontology.getId())).willReturn(accesses);
    	
    	String sessionUserId = "userOntology";
    	
    	given(utils.getUserId()).willReturn(sessionUserId);
    	given(utils.isAdministrator()).willReturn(false);
    	
    	mockMvc.perform(get("/ontologies/authorization")
				.param("id", ontology.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(jsonPath("$[0].id", is(accesses.get(0).getId())))
		.andExpect(jsonPath("$[0].userId", is(accesses.get(0).getUser().getUserId())))
		.andExpect(jsonPath("$[0].typeName", is(accesses.get(0).getOntologyUserAccessType().getName())))
    	.andExpect(jsonPath("$[0].ontologyId", is(accesses.get(0).getOntology().getId())))
    	.andExpect(jsonPath("$[1].id", is(accesses.get(1).getId())))
		.andExpect(jsonPath("$[1].userId", is(accesses.get(1).getUser().getUserId())))
		.andExpect(jsonPath("$[1].typeName", is(accesses.get(1).getOntologyUserAccessType().getName())))
    	.andExpect(jsonPath("$[1].ontologyId", is(accesses.get(1).getOntology().getId())));    	
    }

    @Test
    public void when__sessionUserIsNotAdminOrOwner__OntologyAuthorizationsAreNotReturned() throws Exception {
    	Ontology ontology = ontologyCreator("ontologyId", "userOntology");

    	given(ontologyService.getOntologyById(ontology.getId())).willReturn(ontology);
    	
    	String sessionUserId = "unknown";
    	
    	given(utils.getUserId()).willReturn(sessionUserId);
    	given(utils.isAdministrator()).willReturn(false);
    	
    	mockMvc.perform(get("/ontologies/authorization")
				.param("id", ontology.getId()))
		.andExpect(status().isForbidden());   	
    }
}
