
package com.indracompany.sofia2.api.rest.api.dto;

public class TokenUserDTO {
	private String userIdentification;
	private String token;
	public String getUserIdentification() {
		return userIdentification;
	}
	public void setUserIdentification(String userIdentification) {
		this.userIdentification = userIdentification;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}


	
}
