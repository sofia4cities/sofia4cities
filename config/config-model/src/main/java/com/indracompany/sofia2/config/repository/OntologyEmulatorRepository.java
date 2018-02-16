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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyEmulator;
import com.indracompany.sofia2.config.model.User;

public interface OntologyEmulatorRepository extends JpaRepository<OntologyEmulator, String> {

	List<OntologyEmulator> findByIdentification(String identification);

	List<OntologyEmulator> findByIdentificationAndUser(String identification, User user);

	List<OntologyEmulator> findByUser(User user);

	List<OntologyEmulator> findById(String id);

	OntologyEmulator findByOntology(Ontology ontology);

	void deleteByOntology(Ontology ontology);
}
