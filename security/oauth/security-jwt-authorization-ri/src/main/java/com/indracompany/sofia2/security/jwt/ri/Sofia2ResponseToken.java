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

import java.io.Serializable;
import java.util.Date;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

public class Sofia2ResponseToken implements Serializable {
	private String token;
	private Date expirationTimestamp;
	private String expirationFormatted;
	
	
	private OAuth2AccessToken oauthInfo;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public OAuth2AccessToken getOauthInfo() {
		return oauthInfo;
	}

	public void setOauthInfo(OAuth2AccessToken oauthInfo) {
		this.oauthInfo = oauthInfo;
	}

	public Date getExpirationTimestamp() {
		return expirationTimestamp;
	}

	public void setExpirationTimestamp(Date expirationTimestamp) {
		this.expirationTimestamp = expirationTimestamp;
	}

	public String getExpirationFormatted() {
		return expirationFormatted;
	}

	public void setExpirationFormatted(String expirationFormatted) {
		this.expirationFormatted = expirationFormatted;
	}
	
	
	@Override
	public String toString() {
		return "Sofia2ResponseToken [token=" + token + ", expirationTimestamp=" + expirationTimestamp
				+ ", expirationFormatted=" + expirationFormatted + ", oauthInfo=" + oauthInfo + "]";
	}

	
	

	

	
}
