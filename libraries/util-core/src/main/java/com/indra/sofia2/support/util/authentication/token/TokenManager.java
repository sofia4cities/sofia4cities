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
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.authentication.token;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indra.sofia2.grid.client.cache.CacheService;
import com.indra.sofia2.log.LogService;
import com.indra.sofia2.support.bbdd.sib.exceptions.BDCConnectionException;
import com.indra.sofia2.support.bbdd.test.DatabaseTester;
import com.indra.sofia2.support.entity.gestion.dominio.Kp;
import com.indra.sofia2.support.entity.gestion.dominio.Token;
import com.indra.sofia2.support.entity.utils.CalendarAdapter;
import com.indra.sofia2.support.util.authentication.exceptions.NotValidKpForTokenException;
import com.indra.sofia2.support.util.authentication.exceptions.TokenDeactivatedException;
import com.indra.sofia2.support.util.authentication.exceptions.TokenNotFoundException;
import com.indra.sofia2.support.util.authentication.token.exceptions.KPNotFoundException;

/**
 * Bean to generate, check, activate deactivate and remove a token from BDC
 * 
 * @author jfgpimpollo
 *
 */
@Component("tokenAuthenticationManager")
public class TokenManager {
	private static final String KP_DOES_NOT_EXIST = "KP does not exist";
	private static final String DUPLICATE_KP_IDENTIFIER = "There are more than one KP with this identifier";

	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private DatabaseTester databaseTester;
	
	/**
	 * Generates new Unique Tokens and asociates them to the KP 
	 * @return
	 */
	public List<String> generateNewTokens(String kpName, int numberOfTokens) throws KPNotFoundException, Exception{
		LogService.getLogI18n(this.getClass()).debug("TokenManager.debug.generateNewTokens", kpName, numberOfTokens);
		List<String> tokens=new ArrayList<String>();
		
		for(int i=0;i<numberOfTokens;){
			//Genera un nuevo token
			String candidateToken=UUID.randomUUID().toString().replaceAll("-", "");

			//Comprueba que el token no existe en la base de datos
			Token existingToken=cacheService.getToken(candidateToken);
			if(existingToken==null){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot generate new Tokens in BDC. Configuration Database is not available");
				}
				
				//El token no existe en la BDC por lo que es valido
				
				i++;//En la siguiente iteración puede pasar al siguiente token

				Kp kpId=cacheService.getKp(kpName);
				if(kpId==null){
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.generateNewTokens.notfound", kpName);
					if(!databaseTester.testBDC()){
						throw new BDCConnectionException("Cannot get KP from BDC. Configuration Database is not available");
					}else{
						throw new KPNotFoundException("Cannot find KP: "+kpName+" in BDC");
					}
				}

				//Crea el nuevo token en la BDC asociandolo al KP
				Token newToken=new Token();
				newToken.setToken(candidateToken);
				newToken.setKpId(kpId);
				newToken.setActivo(0);
				
				try{
					newToken.persist();
				}catch(Exception e){
					if(!databaseTester.testBDC()){
						throw new BDCConnectionException("Cannot create Token in BDC. Configuration Database is not available");
					}else{
						LogService.getLogI18n(this.getClass()).error("TokenManager.error.generateNewTokens.persist", e, newToken);
						throw e;
					}
				}
				
				cacheService.updateToken(newToken);
				
				//Añade el token a la lista de tokens generados
				tokens.add(candidateToken);
			}
		}
		LogService.getLogI18n(this.getClass()).debug("TokenManager.debug.generateNewTokens.result", tokens);
		return tokens;
	}
	
	/**
	 * Activates an existing Token
	 * @param token
	 * @return
	 */
	public boolean activateToken(String token) throws TokenNotFoundException, Exception {
		try{
			LogService.getLogI18n(this.getClass()).debug("TokenManager.debug.activateToken", token);
			
			//Recupera el Token de BDC
			Token selectedToken = cacheService.getToken(token);
			if(selectedToken==null){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot get Token from BDC. Configuration Database is not available");
				}else{
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.notfound", token);
					throw new TokenNotFoundException("Cannot find Token: "+token+" in BDC");
				}
			}
			//Lo pone a activo
			selectedToken.setActivo(1);
			
			try{
				selectedToken.merge();
			}catch(Exception e){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot activate Token in BDC. Configuration Database is not available");
				}else{
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.persist", e, selectedToken);
					throw e;
				}
			}
			
			cacheService.updateToken(selectedToken);

			return true;
		}catch(NoResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.notfound", token);
			throw new TokenNotFoundException(KP_DOES_NOT_EXIST);
		}catch(NonUniqueResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.unike", token);
			throw new TokenNotFoundException(DUPLICATE_KP_IDENTIFIER);
		}
	}
	
	/**
	 * Deactivates an existing Token
	 * @param token
	 * @return
	 */
	public boolean deactivateToken(String token) throws TokenNotFoundException, Exception{
		try{
			LogService.getLogI18n(this.getClass()).debug("TokenManager.debug.deactivateToken", token);
			//Recupera el token de BDC
			Token selectedToken=cacheService.getToken(token);
			
			if(selectedToken==null){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot get Token from BDC. Configuration Database is not available");
				}else{
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.deactivateToken.notfound", token);
					throw new TokenNotFoundException("Cannot find Token: "+token+" in BDC");
				}
			}
			
			//lo desactiva
			selectedToken.setActivo(0);
			
			try{
				selectedToken.merge();
			}catch(Exception e){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot deactivate Token in BDC. Configuration Database is not available");
				}else{
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.deactivateToken.persist", e, selectedToken);
					throw e;
				}
			}
			
			cacheService.updateToken(selectedToken);
			
			return true;
		}catch(NoResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.notfound", token);
			throw new TokenNotFoundException(KP_DOES_NOT_EXIST);
		}catch(NonUniqueResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.unike", token);
			throw new TokenNotFoundException(DUPLICATE_KP_IDENTIFIER);
		}	
	}
	
	/**
	 * Removes a Token from BDC
	 * @param token
	 */
	public boolean removeToken(String token) throws TokenNotFoundException, Exception{
		try{
			LogService.getLogI18n(this.getClass()).debug("TokenManager.debug.removeToken", token);
			//Recupera el token de BDC
			Token selectedToken=cacheService.getToken(token);
			
			if(selectedToken==null){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot get Token from BDC. Configuration Database is not available");
				}else{
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.removeToken.notfound", token);
					throw new TokenNotFoundException("Cannot find Token: "+token+" in BDC");
				}
			}
			
			try{
				//lo borra
				selectedToken.remove();
			}catch(Exception e){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot remove Token in BDC. Configuration Database is not available");
				}else{
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.removeToken.persist", e, selectedToken);
					throw e;
				}
			}
			
			cacheService.removeToken(selectedToken);
			
			return true;
		}catch(NoResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.notfound", token);
			throw new TokenNotFoundException(KP_DOES_NOT_EXIST);
		}catch(NonUniqueResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.unike", token);
			throw new TokenNotFoundException(DUPLICATE_KP_IDENTIFIER);
		}
	}
	
	/**
	 * Returns a Bean with a token attributes in the BDC
	 * @param token
	 * @return
	 * @throws TokenNotFoundException
	 */
	public TokenVO getToken(String token) throws TokenNotFoundException{
		try{
			LogService.getLogI18n(this.getClass()).debug("TokenManager.debug.getToken", token);
			//Recupera el token de cache
			Token selectedToken=cacheService.getToken(token);
			if(selectedToken==null){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot get Token from BDC. Configuration Database is not available");
				}else{
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.removeToken.notfound", token);
					throw new TokenNotFoundException("Cannot find Token: "+token+" in BDC");
				}
			}
			
			//Crea el bean con los datos recuperados de BDC
			TokenVO tokenVo=new TokenVO();
			tokenVo.setId(selectedToken.getId());
			tokenVo.setKpId(selectedToken.getKpId().getId());
			tokenVo.setToken(selectedToken.getToken());
			tokenVo.setActivo(selectedToken.getActivo()==1);
			
			return tokenVo;
			
		}catch(NoResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.notfound", token);
			throw new TokenNotFoundException(KP_DOES_NOT_EXIST);
		}catch(NonUniqueResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.unike", token);
			throw new TokenNotFoundException(DUPLICATE_KP_IDENTIFIER);
		}
	}
	
	/**
	 * Checks if a token exists and is associated to a KP
	 * @param token
	 * @param kpName
	 * @throws NotValidKpForTokenException
	 * @throws TokenNotFoundException
	 * @throws TokenDeactivatedException 
	 */
	public void validateToken(String token, String kpName) throws NotValidKpForTokenException, TokenNotFoundException, TokenDeactivatedException {
		try{
			LogService.getLogI18n(this.getClass()).debug("TokenManager.debug.validateToken", token, kpName);
			//Recupera el token de BDC
			Token selectedToken=cacheService.getToken(token);
			if(selectedToken==null){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot get Token from BDC. Configuration Database is not available");
				}else{
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.removeToken.notfound", token);
					throw new TokenNotFoundException("Cannot find Token: "+token+" in BDC");
				}
			}
			
			//Valida si el token esta asociado al KP y si está activo
			if(selectedToken.getKpId().getIdentificacion().equals(kpName) ){
				if (selectedToken.getActivo()==1) {
					return;
				} else {
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.validateToken.noactivo", token);
					throw new TokenDeactivatedException("The token: "+token+" is deactivated en BDC"); 
				}
			}else{
				LogService.getLogI18n(this.getClass()).error("TokenManager.error.validateToken.noasociado", token, kpName);
				throw new NotValidKpForTokenException("The token: "+token+" is not a valid token for KP: "+kpName);
			}
		}catch(NoResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.notfound", token);
			throw new TokenNotFoundException(KP_DOES_NOT_EXIST);
		}catch(NonUniqueResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.unike", token);
			throw new TokenNotFoundException(DUPLICATE_KP_IDENTIFIER);
		}
	}
	
	/**
	 * Sets current timestamp as last connection time to a token
	 * @param token
	 * @param kpName
	 * @throws TokenNotFoundException
	 * @throws TokenNotFoundException
	 */
	public void updateLastConnectionTime(String token, String kpName)throws TokenNotFoundException {
		//Recupera el token de BDC
		try{
			LogService.getLogI18n(this.getClass()).debug("TokenManager.debug.updateLastConnectionTime", token, kpName);
			Token selectedToken=cacheService.getToken(token);
			
			if(selectedToken==null){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot get Token from BDC. Configuration Database is not available");
				}else{
					LogService.getLogI18n(this.getClass()).error("TokenManager.error.removeToken.notfound", token);
					throw new TokenNotFoundException("Cannot find Token: "+token+" in BDC");
				}
			}

			//actualiza la fecha de ultima conexion
			selectedToken.setUltimaconexion(CalendarAdapter.getUtcDate().toGregorianCalendar());
			
			try{
				selectedToken.merge();
			}catch(Exception e){
				LogService.getLogI18n(this.getClass()).error("TokenManager.error.updateLastConnectionTime.merge", selectedToken);
			}
			
			cacheService.updateToken(selectedToken);
			
		}catch(NoResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.notfound", token);
			throw new TokenNotFoundException(KP_DOES_NOT_EXIST);
		}catch(NonUniqueResultException e){
			LogService.getLogI18n(this.getClass()).error("TokenManager.error.activateToken.unike", token);
			throw new TokenNotFoundException(DUPLICATE_KP_IDENTIFIER);
		}
	}
	
	/**
	 * Generates new Unique User Token 
	 * @return
	 */
	public String generateTokenUsuario() {
		LogService.getLogI18n(this.getClass()).debug("TokenManager.debug.generateTokenUsuario");
		String candidateToken="";
		Boolean tokenOk = false;
		while (!tokenOk) {
			//Genera un nuevo token
			candidateToken=UUID.randomUUID().toString().replaceAll("-", "");
			
			//Comprueba que el token no existe en la base de datos
			Token existingToken=cacheService.getToken(candidateToken);
			
			if(existingToken==null){
				if(!databaseTester.testBDC()){
					throw new BDCConnectionException("Cannot generate new Tokens in BDC. Configuration Database is not available");
				}
				
				//El token no existe en la BDC por lo que es valido
				tokenOk = true;
			}
		}
		LogService.getLogI18n(this.getClass()).debug("TokenManager.debug.generateTokenUsuario.result",candidateToken);
		return candidateToken;
	}
	
}
