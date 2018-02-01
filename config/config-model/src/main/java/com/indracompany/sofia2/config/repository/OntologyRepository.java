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
import org.springframework.data.jpa.repository.Query;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;

public interface OntologyRepository extends JpaRepository<Ontology,String> {

	List<Ontology> findByIdentificationIgnoreCase(String identification);
	List<Ontology> findByDescription(String description);
	Ontology findByIdentification(String identification);
	List<Ontology> findAllByOrderByIdentificationAsc();
	List<Ontology> findByDescriptionContaining(String description);
	List<Ontology> findByIdentificationContaining(String identification);
	List<Ontology> findByUserId(User userId);
	List<Ontology> findByUserIdAndActiveTrue(User userId);
	List<Ontology> findByIdentificationLikeAndDescriptionLike(String identification, String description);
	List<Ontology> findByUserIdAndIdentificationLikeAndDescriptionLike(User userId,String identification,String description);
	List<Ontology> findByIdentificationContainingAndDescriptionContaining(String identification, String description);
	List<Ontology> findByUserIdAndIdentificationContainingAndDescriptionContaining(User userId,String identification,String description);
	List<Ontology> findByUserIdAndIdentificationContaining(User userId,String identification);
	List<Ontology> findByUserIdAndDescriptionContaining(User userId,String description);
	List<Ontology> findByActiveTrueAndIsPublicTrue();
	List<Ontology> findByActiveTrue();
	Ontology findById(String id);
	List<Ontology> findByUserIdAndIsPublicTrue(User userId);
	long countByActiveTrueAndIsPublicTrue();
	long countByIdentificationLikeOrDescriptionLikeOrMetainfLike(String identification, String description, String metainf);
	long countByActiveTrueAndIsPublicTrueAndMetainfIsNull();
	@Query("SELECT o FROM Ontology AS o WHERE o.userId=:userId OR o.id IN (SELECT uo.ontologyId.id FROM OntologyUserAccess AS uo WHERE uo.userId=:userId) AND o.active=true")
	List<Ontology> findByUserIdAndOntologyUserAccessAndAllPermissions(User userId);
}
