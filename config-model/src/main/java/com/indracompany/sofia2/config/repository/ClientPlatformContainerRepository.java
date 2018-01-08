/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformContainer;
import com.indracompany.sofia2.config.model.ClientPlatformContainerType;


public interface ClientPlatformContainerRepository extends JpaRepository<ClientPlatformContainer, String> {
	
	List<ClientPlatformContainer> findByUserIdAndIdentificationLikeAndClientConnectionLike(String userId,String identification,String clientConnection);
	List<ClientPlatformContainer> findByIdentificationLikeAndClientConnectionLike(String identification,String clientConnection);
	List<ClientPlatformContainer> findById(String id);
	List<ClientPlatformContainer> findByState(String state);
	long countByClientPlatformId(ClientPlatform clientPlatformId);
	List<ClientPlatformContainer> findByClientPlatformContainerType(ClientPlatformContainerType clientPlatformContainerTypeId);
	//omitido metodo de consulta para clase Lenguajeruntime
}
