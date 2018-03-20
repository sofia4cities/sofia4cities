package com.indracompany.sofia2.config.services.oauth;

import javax.servlet.http.HttpServletRequest;

public interface JWTService {
	public String extractToken(HttpServletRequest request);
	public String extractToken(String tokenId);	
	public Object extractTokenPrincipal(String tokenId);

}
