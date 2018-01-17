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

import org.springframework.data.jpa.repository.JpaRepository;
import com.indracompany.sofia2.config.model.ClientPlatform;
import java.util.List;

public interface ClientPlatformRepository extends JpaRepository<ClientPlatform, String>{

	ClientPlatform findById(String id);
	List<ClientPlatform> findByIdentificationAndDescription(String identification, String description);
	List<ClientPlatform> findByUserIdAndIdentificationAndDescription(String userId, String identification, String description);
	long countByIdentification(String identification);
	List<ClientPlatform> countByIdentificationLike(String identification);
	long countByUserId(String userId);
	List<ClientPlatform> findByIdentification(String identification);
	List<ClientPlatform> findByIdentificationLike(String identification);
	List<ClientPlatform> findByUserId(String userId);


}
