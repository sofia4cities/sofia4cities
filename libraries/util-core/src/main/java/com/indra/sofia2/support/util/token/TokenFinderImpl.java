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
 * 2013 - 2015  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.token;

import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.indra.sofia2.support.entity.gestion.dominio.Kp;
import com.indra.sofia2.support.entity.gestion.dominio.KpRepository;
import com.indra.sofia2.support.entity.gestion.dominio.Token;
import com.indra.sofia2.support.entity.gestion.dominio.TokenRepository;

@Component("tokenFinderImpl")
public class TokenFinderImpl implements TokenFinder{
	
	private static final Log logger = LogFactory.getLog(TokenFinderImpl.class);
	
	public String getToken(String kpName) throws PersistenceException {
		try {
			logger.info("Retrieving token of KP "+ kpName +" from CDB...");
			Kp kp = KpRepository.findKpsByIdentificacionEquals(kpName).getSingleResult();
			Token t = TokenRepository.findTokensByKpId(kp).getSingleResult();
			return t.getToken();
		} catch (Exception e) {
			throw new PersistenceException("Unable to retrieve token of KP "+ kpName +" from the CDB.", e);
		}
		
	}

}
