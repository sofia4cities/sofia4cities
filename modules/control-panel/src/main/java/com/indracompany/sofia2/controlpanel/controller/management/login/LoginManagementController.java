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
package com.indracompany.sofia2.controlpanel.controller.management.login;

import static com.indracompany.sofia2.controlpanel.controller.management.login.LoginPostUrl.OP_LOGIN;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.indracompany.sofia2.controlpanel.controller.management.ApiOpsRestServices;
import com.indracompany.sofia2.controlpanel.controller.management.login.model.RequestLogin;
import com.indracompany.sofia2.security.jwt.ri.Sofia2CustomTokenService;
import com.indracompany.sofia2.security.jwt.ri.Sofia2ResponseToken;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Api(value="Post Login")
@RestController
@Slf4j
public class LoginManagementController extends ApiOpsRestServices {
	
	@Autowired
	private TokenEndpoint tokenEndpoint;

	@Autowired
	private ClientDetailsService clientDetailsService;
	
	@Autowired
	Sofia2CustomTokenService sofia2CustomTokenService;
	
	@Value("${security.jwt.client-id}")
	private String clientId;

	private String grantTypeLogin="password";

	@ApiOperation(value = "Post Login Oauth2")
	@PostMapping(OP_LOGIN)
	public ResponseEntity<?> postLoginOauth2(@Valid @RequestBody RequestLogin request) throws Exception{

		try {
			log.info(OP_LOGIN + " Request: "+request);
			
			ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(clientId);

			Map<String, String> parameters=new HashMap<String, String>();
			parameters.put("grant_type", grantTypeLogin);
			parameters.put("username", request.getUsername());
			parameters.put("password", request.getPassword());
			
			Principal principal = new UsernamePasswordAuthenticationToken(authenticatedClient.getClientId(),authenticatedClient.getClientSecret(),authenticatedClient.getAuthorities());
			ResponseEntity<OAuth2AccessToken> token = tokenEndpoint.postAccessToken(principal, parameters);
			
			OAuth2AccessToken accessToken = token.getBody();
			
			OAuth2Authentication authentication = sofia2CustomTokenService.loadAuthentication(accessToken.getValue());
			OAuth2AccessToken enhanced = enhance(accessToken,authentication);
			
			return getResponse(enhanced);
			
		} catch (Exception e) {
			log.error(OP_LOGIN + " Error: " + e.getMessage(),e);
			log.error(OP_LOGIN + " Error: " + e.getStackTrace());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
	
	@ApiOperation(value = "GET Login Oauth2")
	@RequestMapping(value="/login/username/{username}/password/{password}", method=RequestMethod.GET)
	public ResponseEntity<?> getLoginOauth2(@ApiParam(value = "username", required = true) @PathVariable("username") String username,
															@ApiParam(value = "password", required=true) @PathVariable(name="password") String password) throws Exception{

		try {
			log.info(OP_LOGIN + " Request: "+username);
			
			RequestLogin request = new RequestLogin();
			request.setPassword(password);
			request.setUsername(username);
			
			return postLoginOauth2(request);
			
		} catch (Exception e) {
			log.error(OP_LOGIN + " Error: " + e.getMessage(),e);
			log.error(OP_LOGIN + " Error: " + e.getStackTrace());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/login/refresh")
	public ResponseEntity<OAuth2AccessToken> renewToken(@RequestBody String id) {
		try {
			
			log.info("Entering Renew Token with id = "+id);
			
			OAuth2Authentication authentication = sofia2CustomTokenService.loadAuthentication(id);
			OAuth2AccessToken token = sofia2CustomTokenService.getAccessToken((OAuth2Authentication) authentication);

			OAuth2RefreshToken refreshToken = token.getRefreshToken();

			String clientId = ((OAuth2Authentication) authentication).getOAuth2Request().getClientId();
			Collection<String> scope = ((OAuth2Authentication) authentication).getOAuth2Request().getScope();
			Map<String, String> parameters = ((OAuth2Authentication) authentication).getOAuth2Request()
					.getRequestParameters();

			TokenRequest tokenRequest = new TokenRequest(parameters, clientId, scope, grantTypeLogin);

			OAuth2AccessToken tokenRefreshed = sofia2CustomTokenService.refreshAccessToken(refreshToken.getValue(),
					tokenRequest);

			OAuth2AccessToken enhanced = enhance(tokenRefreshed,authentication);
			
			return getResponse(enhanced);

		} catch (Exception e) {
			Sofia2ResponseToken r = new Sofia2ResponseToken();
			r.setToken("-1");
			log.info("Leaving Info Token with with Error response = "+e.getLocalizedMessage());
			return getResponse(null);
		}

	}
	
	
	    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
	        final Map<String, Object> additionalInfo = new HashMap<>();
	        additionalInfo.put("name", authentication.getName());
	        
	        Collection<GrantedAuthority>  authorities = authentication.getAuthorities();
	        List<String> collect = authorities.stream()
	                .map(GrantedAuthority:: getAuthority)
	                .collect(Collectors.toList());
	        
	        additionalInfo.put("authorities", collect);
	        additionalInfo.put("principal",  authentication.getUserAuthentication().getPrincipal());
	        additionalInfo.put("parameters",  authentication.getOAuth2Request().getRequestParameters());
	        additionalInfo.put("clientId",  authentication.getOAuth2Request().getClientId());
	        additionalInfo.put("grantType",  authentication.getOAuth2Request().getGrantType());
	       
	        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
	        return accessToken;
	    }

	@RequestMapping(method = RequestMethod.POST, value = "/login/info")
	public ResponseEntity<OAuth2AccessToken> info(@RequestBody String tokenId) {
		try {
			
			log.info("Entering Info Token with id = "+tokenId);
			
			OAuth2Authentication authentication = sofia2CustomTokenService.loadAuthentication(tokenId);
			OAuth2AccessToken token = sofia2CustomTokenService.getAccessToken((OAuth2Authentication) authentication);
			
			OAuth2AccessToken enhanced = enhance(token,authentication);

			return getResponse(enhanced);
		} catch (Exception e) {
			log.info("Leaving Info Token with with Error response = "+e.getLocalizedMessage());
			return getResponse(null);
		}

	}

	private ResponseEntity<OAuth2AccessToken> getResponse(OAuth2AccessToken accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		return new ResponseEntity<OAuth2AccessToken>(accessToken, headers, HttpStatus.OK);
	}
	
	private ResponseEntity<DefaultOAuth2AccessToken> getResponseDefault(DefaultOAuth2AccessToken accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		return new ResponseEntity<DefaultOAuth2AccessToken>(accessToken, headers, HttpStatus.OK);
	}
	
	
}
