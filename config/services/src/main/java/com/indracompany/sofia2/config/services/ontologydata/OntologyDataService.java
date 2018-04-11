/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.config.services.ontologydata;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.router.service.app.model.OperationModel;

public interface OntologyDataService {

	public void checkOntologySchemaCompliance(final String data, final Ontology ontology) throws DataSchemaValidationException;
	
	public void checkOntologySchemaCompliance(final String data, final String ontologyName) throws DataSchemaValidationException;

	public String addContextData(final OperationModel operationModel) throws JsonProcessingException, IOException;
}
