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
package com.indracompany.sofia2.config.services.twitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.TwitterListening;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.client.ClientPlatformService;
import com.indracompany.sofia2.config.services.configuration.ConfigurationService;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.token.TokenService;
import com.indracompany.sofia2.config.services.user.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TwitterServiceIntegrationTest {

	@Autowired
	TwitterListeningService twitterListeningService;
	@Autowired
	TokenService tokenService;
	@Autowired
	ClientPlatformService clientPlatformService;
	@Autowired
	UserService userService;
	@Autowired
	OntologyService ontologyService;
	@Autowired
	ConfigurationService configurationService;

	@Test
	public void given_ThereIsNotATwitterListening_When_ItIsCreated_Then_ItIsCorrectlyAddedToThePersistenceLayer() {
		TwitterListening twitterListening;
		Ontology ontology;
		Token token;
		ClientPlatform clientPlatform;
		if (twitterListeningService.getListenByIdentificator("Listening Test") == null) {
			twitterListening = new TwitterListening();
			twitterListening.setId("1");
			twitterListening.setConfiguration(configurationService
					.getConfiguration(Configuration.Type.TwitterConfiguration, "default", "lmgracia"));

			User user = userService.getUser("administrator");
			if (this.ontologyService.getOntologyByIdentification("OntologyTwitter", user.getUserId()) == null) {
				ontology = twitterListeningService.createTwitterOntology("OntologyTwitter");
				ontology.setUser(user);
				ontologyService.createOntology(ontology);
				ontology = ontologyService.getOntologyByIdentification(ontology.getIdentification(), user.getUserId());
			} else
				ontology = this.ontologyService.getOntologyByIdentification("OntologyTwitter", user.getUserId());

			List<Ontology> ontologies = new ArrayList<Ontology>();
			ontologies.add(ontology);

			if (clientPlatformService.getByIdentification("CPTwitter") == null) {
				clientPlatform = new ClientPlatform();
				clientPlatform.setUser(userService.getUser("administrator"));
				clientPlatform.setIdentification("CPTwitter");
				token = clientPlatformService.createClientAndToken(ontologies, clientPlatform);
			} else {
				clientPlatform = clientPlatformService.getByIdentification("CPTwitter");
				token = tokenService.getToken(clientPlatform);
			}

			twitterListening.setToken(token);
			twitterListening.setOntology(ontology);
			twitterListening.setUser(ontology.getUser());
			twitterListening.setDateFrom(new Date());
			twitterListening.setDateTo(new Date(System.currentTimeMillis() + 10000000));
			twitterListening.setIdentificator("Listening Test");
			twitterListening.setTopics("Helsinki,Madrid");
			twitterListening = twitterListeningService.createListening(twitterListening, user.getUserId());
		}
		twitterListening = twitterListeningService.getListenByIdentificator("Listening Test");
		Assert.assertTrue(twitterListening.getId() != null);

	}
	//
	// @Test
	// public void addScheduledSearchJob() {
	// TwitterListening twitterListening =
	// twitterService.getListenByIdentificator("Listening Test");
	// if(twitterListening == null)
	// {
	// this.testCreateListening();
	// twitterListening = twitterService.getListenByIdentificator("Listening Test");
	// }
	//
	// Assert.assertTrue(twitterService.scheduleTwitterListening(twitterListening));
	//
	//
	// }
}
