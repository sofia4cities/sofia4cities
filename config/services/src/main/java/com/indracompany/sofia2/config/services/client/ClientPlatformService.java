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
package com.indracompany.sofia2.config.services.client;

import java.util.List;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.ClientPlatformOntology.AccessType;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.services.client.dto.DeviceCreateDTO;

public interface ClientPlatformService {

	Token createClientAndToken(List<Ontology> ontologies, ClientPlatform clientPlatform);

	ClientPlatform getByIdentification(String identification);

	public List<ClientPlatform> getAllClientPlatforms();

	public List<ClientPlatform> getclientPlatformsByUser(User user);

	List<ClientPlatform> getAllClientPlatformByCriteria(String userId, String identification, String[] ontologies);

	List<AccessType> getClientPlatformOntologyAccessLevel();

	void createClientPlatform(ClientPlatform clientPlatform);

	void updateDevice(DeviceCreateDTO clientPlatform);
}
