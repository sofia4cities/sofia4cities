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

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {

	List<Device> findByClientPlatform(ClientPlatform clientPlatform);

	List<Device> findByClientPlatformAndIdentification(ClientPlatform clientPlatform, String identification);

	@Query("SELECT d FROM Device as d WHERE d.clientPlatform.id=:clientId AND d.identification=:identification")
	List<Device> findByClientPlatformAndIdentification(@Param("clientId") String clientPlatformId,
			@Param("identification") String identification);

	@Modifying
	@Query("UPDATE Device d SET d.connected = :connected, d.disabled = :disabled WHERE d.updatedAt < :date")
	int updateDeviceStatusByUpdatedAt(@Param("connected") boolean connected, @Param("disabled") boolean disabled,
			@Param("date") Date date);

	Device findById(String id);

}
