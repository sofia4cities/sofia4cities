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
import org.springframework.data.repository.query.Param;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.User;

public interface OntologyRepository extends JpaRepository<Ontology, String> {

	List<Ontology> findByIdentificationIgnoreCase(String identification);

	List<Ontology> findByDescription(String description);

	Ontology findByIdentification(String identification);

	List<Ontology> findAllByOrderByIdentificationAsc();

	List<Ontology> findByDescriptionContaining(String description);

	List<Ontology> findByIdentificationContaining(String identification);

	List<Ontology> findByUser(User user);

	List<Ontology> findByUserAndActiveTrue(User user);

	List<Ontology> findByIdentificationLikeAndDescriptionLike(String identification, String description);

	List<Ontology> findByUserAndIdentificationLikeAndDescriptionLike(User user, String identification,
			String description);

	List<Ontology> findByIdentificationContainingAndDescriptionContaining(String identification, String description);

	List<Ontology> findByUserAndIdentificationContainingAndDescriptionContaining(User user, String identification,
			String description);

	List<Ontology> findByUserAndIdentificationContaining(User user, String identification);

	List<Ontology> findByUserAndDescriptionContaining(User user, String description);

	List<Ontology> findByActiveTrueAndIsPublicTrue();

	List<Ontology> findByActiveTrue();

	Ontology findById(String id);

	List<Ontology> findByUserAndIsPublicTrue(User user);

	long countByActiveTrueAndIsPublicTrue();

	long countByIdentificationLikeOrDescriptionLikeOrMetainfLike(String identification, String description,
			String metainf);

	long countByActiveTrueAndIsPublicTrueAndMetainfIsNull();

	@Query("SELECT o FROM Ontology AS o WHERE (o.user=:user OR o.isPublic=TRUE OR o.id IN (SELECT uo.ontology.id FROM OntologyUserAccess AS uo WHERE uo.user=:user)) AND o.active=true")
	List<Ontology> findByUserAndOntologyUserAccessAndAllPermissions(@Param("user") User user);

	@Query("SELECT o " + "FROM Ontology AS o " + 
		   "WHERE (o.isPublic=TRUE OR " +
		          "o.user=:user OR " + 
			      "o.id IN (SELECT uo.ontology.id " + 
		                   "FROM OntologyUserAccess AS uo " + 
			               "WHERE uo.user=:user)) AND " + 
		          "(o.identification like %:identification% AND o.description like %:description%)")
	List<Ontology> findByUserAndPermissionsANDIdentificationContainingAndDescriptionContaining(@Param("user") User user,
			@Param("identification") String identification, @Param("description") String description);

	@Query("SELECT o FROM Ontology AS o WHERE o.isPublic=TRUE OR o.user=:user OR o.id IN (SELECT uo.ontology.id FROM OntologyUserAccess AS uo WHERE uo.user=:user AND (uo.ontologyUserAccessType='ALL' OR uo.ontologyUserAccessType='QUERY')) AND o.active=true")
	List<Ontology> findByUserAndOntologyUserAccessAndPermissionsQuery(@Param("user") User user);

	@Query("SELECT o FROM Ontology AS o WHERE o.user=:user OR o.id IN (SELECT uo.ontology.id FROM OntologyUserAccess AS uo WHERE uo.user=:user AND (uo.ontologyUserAccessType='ALL' OR uo.ontologyUserAccessType='INSERT')) AND o.active=true")
	List<Ontology> findByUserAndOntologyUserAccessAndPermissionsInsert(@Param("user") User user);

	void deleteById(String id);

}
