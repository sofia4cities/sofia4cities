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

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

public class Sofia2CustomTokenService  {
	
	
	private DefaultTokenServices tokenServices;

	public Sofia2CustomTokenService( DefaultTokenServices tokenServices)
	{
		this.tokenServices=tokenServices;
	}
	
	public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException,
	InvalidTokenException {
		return this.tokenServices.loadAuthentication(accessTokenValue);
	}
	
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		return this.tokenServices.getAccessToken(authentication);
	}
	
	public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest)
			throws AuthenticationException {
		return this.tokenServices.refreshAccessToken(refreshTokenValue, tokenRequest);
	}
	



	
}