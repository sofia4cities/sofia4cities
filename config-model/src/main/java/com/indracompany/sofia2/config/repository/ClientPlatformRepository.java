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
