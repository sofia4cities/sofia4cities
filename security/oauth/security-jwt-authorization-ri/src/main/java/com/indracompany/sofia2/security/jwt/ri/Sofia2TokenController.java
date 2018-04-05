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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class Sofia2TokenController {

	@Value("${security.jwt.client-id}")
	private String clientId;

	@Resource(name = "tokenStore")
	TokenStore tokenStore;

	@Autowired
	Sofia2CustomTokenService sofia2CustomTokenService;

	@RequestMapping(method = RequestMethod.GET, value = "/sofia2-oauth/token-values")
	@ResponseBody
	public List<String> getTokens() {
		List<String> tokenValues = new ArrayList<String>();
		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(clientId);
		if (tokens != null) {
			for (OAuth2AccessToken token : tokens) {
				tokenValues.add(token.getValue());
			}
		}
		return tokenValues;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/sofia2-oauth/tokens")
	@ResponseBody
	public Collection<OAuth2AccessToken> getTokenLists() {
		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(clientId);
		return tokens;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/sofia2-oauth/tokens/revokeRefreshToken/{tokenId:.*}")
	@ResponseBody
	public String revokeRefreshToken(@PathVariable String tokenId) {
		if (tokenStore instanceof JdbcTokenStore) {
			((JdbcTokenStore) tokenStore).removeRefreshToken(tokenId);
		}
		return tokenId;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/sofia2-oauth/renewToken")
	@ResponseBody
	public Sofia2ResponseToken renewToken(@RequestBody String id) {
		try {
			
			log.info("Entering Renew Token with id = "+id);
			
			OAuth2Authentication authentication = sofia2CustomTokenService.loadAuthentication(id);
			OAuth2AccessToken token = sofia2CustomTokenService.getAccessToken((OAuth2Authentication) authentication);

			OAuth2RefreshToken refreshToken = token.getRefreshToken();

			String clientId = ((OAuth2Authentication) authentication).getOAuth2Request().getClientId();
			Collection<String> scope = ((OAuth2Authentication) authentication).getOAuth2Request().getScope();
			Map<String, String> parameters = ((OAuth2Authentication) authentication).getOAuth2Request()
					.getRequestParameters();

			TokenRequest tokenRequest = new TokenRequest(parameters, clientId, scope, "password");

			OAuth2AccessToken tokenRefreshed = sofia2CustomTokenService.refreshAccessToken(refreshToken.getValue(),
					tokenRequest);

			Date expiration = tokenRefreshed.getExpiration();

			Sofia2ResponseToken r = new Sofia2ResponseToken();
			r.setOauthInfo(tokenRefreshed);
			r.setExpirationTimestamp(expiration);

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

			r.setExpirationFormatted(dateFormat.format(expiration));
			r.setToken(tokenRefreshed.getValue());
			
			log.info("Leaving Renew Token with with response = "+r);
			
			return r;

		} catch (Exception e) {
			Sofia2ResponseToken r = new Sofia2ResponseToken();
			r.setToken("-1");
			log.info("Leaving Info Token with with Error response = "+e.getLocalizedMessage());
			return r;
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = "/sofia2-oauth/tokenInfo")
	@ResponseBody
	public Sofia2ResponseToken info(@RequestBody String tokenId) {
		try {
			
			log.info("Entering Info Token with id = "+tokenId);
			
			OAuth2Authentication authentication = sofia2CustomTokenService.loadAuthentication(tokenId);

			String ip = ((OAuth2Authentication) authentication).getOAuth2Request().getRefreshTokenRequest()
					.getRequestParameters().get("ip");

			OAuth2AccessToken token = sofia2CustomTokenService.getAccessToken((OAuth2Authentication) authentication);

			Sofia2ResponseToken r = new Sofia2ResponseToken();
			r.setOauthInfo(token);
			r.setExpirationTimestamp(token.getExpiration());

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

			r.setExpirationFormatted(dateFormat.format(token.getExpiration()));
			r.setToken(token.getValue());
			r.setIp(ip);
			
			log.info("Leaving Info Token with response = "+r);

			return r;
		} catch (Exception e) {
			Sofia2ResponseToken r = new Sofia2ResponseToken();
			r.setToken("-1");
			log.info("Leaving Info Token with with Error response = "+e.getLocalizedMessage());
			return r;
		}

	}

}