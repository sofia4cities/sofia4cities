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
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformContainer;
import com.indracompany.sofia2.config.model.ClientPlatformContainerType;


public interface ClientPlatformContainerRepository extends JpaRepository<ClientPlatformContainer, String> {

	@Query("SELECT o FROM ClientPlatformContainer o WHERE o.clientPlatformId.userId = ?1 AND o.clientPlatformId.identification LIKE ?2 AND o.clientConnection LIKE ?3")
	List<ClientPlatformContainer> findByUserIdAndIdentificationLikeAndClientConnectionLike(String userId,String identification,String clientConnection);
	@Query("SELECT o FROM ClientPlatformContainer o WHERE o.clientPlatformId.identification LIKE ?1 AND o.clientConnection LIKE ?2")
	List<ClientPlatformContainer> findByIdentificationLikeAndClientConnectionLike(String identification,String clientConnection);
	List<ClientPlatformContainer> findById(String id);
	List<ClientPlatformContainer> findByState(String state);
	long countByClientPlatformId(ClientPlatform clientPlatformId);
	List<ClientPlatformContainer> findByClientPlatformContainerTypeId(ClientPlatformContainerType clientPlatformContainerTypeId);
	//omitido metodo de consulta para clase Lenguajeruntime
}
