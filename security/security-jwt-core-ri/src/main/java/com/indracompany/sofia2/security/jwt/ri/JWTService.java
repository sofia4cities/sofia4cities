/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.security.jwt.ri;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

	@Resource(name = "tokenServices")
	Sofia2CustomTokenService tokenServices;

	
	public String extractToken(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		if (authorization != null && authorization.contains("Bearer")) {
			String tokenId = authorization.substring("Bearer".length() + 1);
			
			OAuth2Authentication  authentication = tokenServices.loadAuthentication(tokenId);
			Authentication au = authentication.getUserAuthentication();
			Object principal = authentication.getPrincipal();
			
			return authentication.getUserAuthentication().getName();
		}
		else return null;
	}
	
	public String extractToken(String tokenId) {
		OAuth2Authentication  authentication = tokenServices.loadAuthentication(tokenId);
		Authentication au = authentication.getUserAuthentication();
		Object principal = authentication.getPrincipal();
		
		return (String)principal;
	}
	
	public Object extractTokenPrincipal(String tokenId) {
		OAuth2Authentication  authentication = tokenServices.loadAuthentication(tokenId);
		Authentication au = authentication.getUserAuthentication();
		Object principal = authentication.getPrincipal();
		
		return principal;
	}

}
