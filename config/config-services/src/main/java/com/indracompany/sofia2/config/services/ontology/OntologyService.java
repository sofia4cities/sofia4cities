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
package com.indracompany.sofia2.config.services.ontology;

import java.util.List;

import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.DataModel.MainType;
import com.indracompany.sofia2.config.model.Ontology;

public interface OntologyService {

	List<Ontology> getAllOntologies();

	List<Ontology> getOntologiesByUserId(String userId);

	List<Ontology> getOntologiesWithDescriptionAndIdentification(String userId, String identification,
			String description);

	List<String> getAllIdentifications();

	Ontology getOntologyById(String id);

	Ontology getOntologyByIdentification(String identification);

	Ontology saveOntology(Ontology ontology);

	List<DataModel> getAllDataModels();
	
	List<MainType> getAllDataModelTypes();
}
