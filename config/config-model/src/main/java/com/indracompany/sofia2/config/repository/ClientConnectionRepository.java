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

import com.indracompany.sofia2.config.model.ClientConnection;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.User;

public interface ClientConnectionRepository extends JpaRepository<ClientConnection, String> {

	List<ClientConnection> findByClientPlatformId(ClientPlatform clientPlatform);

	List<ClientConnection> findByClientPlatformIdAndStaticIpTrue(ClientPlatform clientPlatform);

	List<ClientConnection> findByClientPlatformIdAndStaticIpFalse(ClientPlatform clientPlatform);

	List<ClientConnection> findByClientPlatformIdAndIdentification(ClientPlatform clientPlatform,
			String indentification);

	@Query("SELECT o FROM ClientConnection o WHERE o.clientPlatformId.userId= :#{#userId}")
	List<ClientConnection> findByUserId(@Param("userId") User userId);

	ClientConnection findById(String id);

	List<ClientConnection> findByIdentification(String identification);

	long countByIdentification(String identification);

	long countByClientPlatformId(ClientPlatform clientPlatform);

}
