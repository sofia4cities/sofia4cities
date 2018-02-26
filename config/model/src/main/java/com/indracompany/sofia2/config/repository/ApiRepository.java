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
package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.User;

public interface ApiRepository extends JpaRepository<Api, String> {

	List<Api> findByIdentificationIgnoreCase(String identification);

	List<Api> findByDescription(String description);

	List<Api> findByIdentification(String identification);

	List<Api> findByDescriptionContaining(String description);

	List<Api> findByIdentificationContaining(String identification);

	List<Api> findByUser(User user);

	List<Api> findByIdentificationLikeAndDescriptionLike(String identification, String description);

	List<Api> findByUserAndIdentificationLikeAndDescriptionLike(User user, String identification, String description);

	List<Api> findByIdentificationContainingAndDescriptionContaining(String identification, String description);

	List<Api> findByUserAndIdentificationContainingAndDescriptionContaining(User user, String identification,
			String description);

	List<Api> findByUserAndIdentificationContaining(User user, String identification);

	List<Api> findByUserAndDescriptionContaining(User user, String description);

	List<Api> findByIdentificationAndNumversionAndApiType(String identification, Integer apiVersion, String apiType);

	List<Api> findByIdentificationAndNumversion(String identification, Integer apiVersion);

	List<Api> findByIdentificationAndApiType(String identification, String apiType);
	
	Api findById(String id);

	List<Api> findByUserAndIsPublicTrue(User userId);
	
	@Query("SELECT o FROM Api AS o WHERE (o.user.userId LIKE %:userId% OR o.identification LIKE %:apiId% OR o.state LIKE %:state%)")
	List<Api> findApisByIdentificationOrStateOrUser(@Param("apiId") String apiId, @Param("state")String state, @Param("userId") String userId);
	
	@Query("SELECT o FROM Api AS o WHERE (o.user.userId LIKE %:userId% AND (o.identification LIKE %:apiId% OR o.state LIKE %:state%)) AND o.isPublic IS true")
	List<Api> findApisByIdentificationOrStateAndUserAndIsPublicTrue(@Param("apiId") String apiId, @Param("state") String state, @Param("userId") String userId);
	
	@Query("SELECT a FROM Api as a WHERE a.isPublic = false AND (a.state = 'PUBLISHED' or a.state = 'DEVELOPMENT') ORDER BY a.identification asc")
	List<Api> findApisNotPublicAndPublishedOrDevelopment();
	
	@Query("SELECT a FROM Api as a WHERE a.user.userId = :userId AND a.isPublic = false AND (a.state = 'PUBLISHED' or a.state = 'DEVELOPMENT') ORDER BY a.identification asc")
	List<Api> findApisByUserNotPublicAndPublishedOrDevelopment(@Param("userId") String userId);
	

}