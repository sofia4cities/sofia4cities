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
package com.indracompany.sofia2.iotbroker.plugable.impl.security.reference;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.github.javafaker.Faker;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.DataModelRepository;
import com.indracompany.sofia2.config.repository.RoleRepository;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.token.TokenService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.iotbroker.common.exception.AuthenticationException;
import com.indracompany.sofia2.iotbroker.common.exception.AuthorizationException;
import com.indracompany.sofia2.iotbroker.plugable.interfaces.security.IoTSession;
import com.indracompany.sofia2.ssap.enums.SSAPMessageTypes;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReferenceSecurityTest {
	@Autowired
	ReferenceSecurityImpl security;

	@Autowired
	UserService userService;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	ClientPlatformService clientPlatformService;
	@Autowired
	OntologyService ontologyService;
	@Autowired
	DataModelRepository dataModelRepository;
	@Autowired
	TokenService tokenService;

	ClientPlatform subjectClientPlatform;
	User subjectUser;
	Ontology subjectOntology;

	Faker faker = new Faker();

	@Before
	public void setup() throws JsonGenerationException, IOException {
		final User user = new User();
		user.setActive(true);
		user.setEmail(faker.internet().emailAddress());
		user.setFullName(faker.name().fullName());
		user.setPassword("changeIt!");
		user.setRole( roleRepository.findById(Role.Type.ROLE_DEVELOPER.name()) );
		final String userId = UUID.randomUUID().toString();
		user.setUserId(userId);
		userService.createUser(user);
		subjectUser = userService.getUser(userId);

		final Ontology ontology = new Ontology();
		ontology.setActive(true);
		ontology.setDescription(faker.lorem().fixedString(10));
		final String ontologyIdentification = UUID.randomUUID().toString();
		ontology.setIdentification(ontologyIdentification);
		ontology.setDataModel(dataModelRepository.findAll().get(0));
		ontology.setJsonSchema("{}");
		ontology.setMetainf("meta");
		ontology.setPublic(false);
		ontology.setRtdbClean(false);
		ontology.setRtdbToHdb(false);
		ontology.setUser(subjectUser);
		ontologyService.createOntology(ontology);
		subjectOntology = ontologyService.getOntologyByIdentification(ontology.getIdentification());

		final ClientPlatform clientPlatform = new ClientPlatform();
		final String clientPlatformIdentification = UUID.randomUUID().toString();
		clientPlatform.setIdentification(clientPlatformIdentification);
		clientPlatform.setUser(subjectUser);
		clientPlatformService.createClientAndToken(Arrays.asList(subjectOntology), clientPlatform);
		subjectClientPlatform = clientPlatformService.getByIdentification(clientPlatformIdentification);

		tokenService.generateTokenForClient(subjectClientPlatform);

	}

	@Test
	public void tearDown() {
		//TODO: Delete created items (depends on JPA configuration)
	}

	@Test
	public void test_security_basic() throws AuthenticationException, AuthorizationException {
		final Token t = tokenService.getToken(subjectClientPlatform);

		final Optional<IoTSession> session = security.authenticate(t.getToken(),
				subjectClientPlatform.getIdentification(), UUID.randomUUID().toString());

		Assert.assertTrue(session.isPresent());
		Assert.assertTrue(!StringUtils.isEmpty(session.get().getSessionKey()));
		Assert.assertTrue(security.checkSessionKeyActive(session.get().getSessionKey()));
		Assert.assertTrue(security.closeSession(session.get().getSessionKey()));
		Assert.assertFalse(security.checkSessionKeyActive(session.get().getSessionKey()));
	}

	@Test
	public void test_fail_on_invalid_token() throws AuthenticationException {
		final Optional<IoTSession> session = security.authenticate("INVALID_TOKEN",
				subjectClientPlatform.getIdentification(), UUID.randomUUID().toString());

		Assert.assertFalse(session.isPresent());
	}

	@Test
	public void test_fails_on_check_unexisting_sessionkey() throws AuthorizationException {
		Assert.assertFalse(security.checkSessionKeyActive("NOT_EXISTENT_SESSIONKEY"));
	}

	@Test
	public void test_ontology_auth_ok() throws AuthenticationException, AuthorizationException {
		final Token t = tokenService.getToken(subjectClientPlatform);

		final Optional<IoTSession> session = security.authenticate(t.getToken(),
				subjectClientPlatform.getIdentification(), UUID.randomUUID().toString());

		Assert.assertTrue(security.checkAuthorization(SSAPMessageTypes.INSERT, subjectOntology.getIdentification(), session.get().getSessionKey()));
	}

	@Test
	public void test_ontology_auth_not_assigned() throws AuthenticationException, AuthorizationException {
		final Token t = tokenService.getToken(subjectClientPlatform);

		final Optional<IoTSession> session = security.authenticate(t.getToken(),
				subjectClientPlatform.getIdentification(), UUID.randomUUID().toString());

		Assert.assertFalse(security.checkAuthorization(SSAPMessageTypes.INSERT, "NOT_ASSIGNED_ONTOLOGY", session.get().getSessionKey()));
	}

}
