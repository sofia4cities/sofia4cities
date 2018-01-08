/*******************************************************************************

 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Token;

public interface TokenRepository extends JpaRepository<Token, String>{
	
	List<Token> findByClientPlatformId(ClientPlatform clientPlatformId);
	List<Token> findByToken(String token);
	
	

}
