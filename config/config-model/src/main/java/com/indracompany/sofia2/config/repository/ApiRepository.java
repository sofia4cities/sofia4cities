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

import com.indracompany.sofia2.config.model.Api;
import com.indracompany.sofia2.config.model.User;

public interface ApiRepository extends JpaRepository<Api,String> {

	List<Api> findByIdentificationIgnoreCase(String identification);
	List<Api> findByDescription(String description);
	List<Api> findByIdentification(String identification);
	List<Api> findByDescriptionContaining(String description);
	List<Api> findByIdentificationContaining(String identification);
	List<Api> findByUserId(String userId);
	List<Api> findByIdentificationLikeAndDescriptionLike(String identification, String description);
	List<Api> findByUserIdAndIdentificationLikeAndDescriptionLike(User userId,String identification,String description);
	List<Api> findByIdentificationContainingAndDescriptionContaining(String identification, String description);
	List<Api> findByUserIdAndIdentificationContainingAndDescriptionContaining(User userId,String identification,String description);
	List<Api> findByUserIdAndIdentificationContaining(User userId,String identification);
	List<Api> findByUserIdAndDescriptionContaining(User userId,String description);
	
	
	List<Api> findByIdentificationAndNumversionAndApiType(String identification,Integer apiVersion, String apiType);
	List<Api> findByIdentificationAndNumversion(String identification,Integer apiVersion);

	
	Api findById(String id);
	List<Api> findByUserIdAndIsPublicTrue(User userId);

}