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

import java.util.List;

import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.TwitterListening;

public interface TwitterListeningService {

	List<TwitterListening> getAllListenings();

	List<TwitterListening> getAllListeningsByUser(String userId);

	TwitterListening getListenById(String id);

	TwitterListening getListenByIdentificator(String identificator);

	List<Configuration> getAllConfigurations();

	List<Configuration> getConfigurationsByUserId(String userId);

	List<String> getClientsFromOntology(String ontologyId);

	List<String> getTokensFromClient(String clientPlatformId);

	void updateListen(TwitterListening twitterListener);

	boolean existOntology(String identification);

	boolean existClientPlatform(String identification);

	Ontology createTwitterOntology(String ontologyId);

	TwitterListening createListening(TwitterListening TwitterListening);

}
