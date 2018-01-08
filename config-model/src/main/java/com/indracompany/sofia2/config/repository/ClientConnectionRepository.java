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

public interface ClientConnectionRepository extends JpaRepository<ClientConnection, String>{

	List<ClientConnection> findByClientPlatformId(ClientPlatform clientPlatform);
	List<ClientConnection> findByClientPlatformIdAndStaticIpTrue(ClientPlatform clientPlatform);
	List<ClientConnection> findByClientPlatformIdAndStaticIpFalse(ClientPlatform clientPlatform);
	List<ClientConnection> findByClientPlatformIdAndIdentification(ClientPlatform clientPlatform,String indentification);
	
	@Query("SELECT o FROM ClientConnection o WHERE o.clientPlatformId.userId= :#{#userId}")
	List<ClientConnection> findByUserId(@Param("userId") String userId);
	
	ClientConnection findById(String id);
	List<ClientConnection> findByIdentification(String identification);
	long countByIdentification(String identification);
	long countByClientPlatformId(ClientPlatform clientPlatform);
	
	
}
