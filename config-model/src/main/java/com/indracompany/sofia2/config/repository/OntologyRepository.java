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

public interface OntologyRepository extends JpaRepository<Ontology,String> {

	List<Ontology> findByIdentificationIgnoreCase(String identification);
	List<Ontology> findByIdentification(String identification);
	List<Ontology> findByUserId(String userId);
	List<Ontology> findByUserIdAndActiveTrue(String userId);
	List<Ontology> findByIdentificationLikeAndDescriptionLike(String identification, String description);
	List<Ontology> findByUserIdAndIdentificationLikeAndDescriptionLike(String userId,String identification,String description);
	List<Ontology> findByActiveTrueAndIsPublicTrue();
	List<Ontology> findByActiveTrue();
	List<Ontology> findById(String id);
	List<Ontology> findByUserIdAndIsPublicTrue(String userId);
	long countByActiveTrueAndIsPublicTrue();
	long countByIdentificationLikeOrDescriptionLikeOrMetainfLike(String identification, String description, String metainf);
	long countByActiveTrueAndIsPublicTrueAndMetainfIsNull();
}
