
package com.indracompany.sofia2.api.rest.api.fiql;

import com.indracompany.sofia2.api.rest.api.dto.TokenUserDTO;
import com.indracompany.sofia2.config.model.UserToken;

public final class TokenUserFIQL {
	
	

	public static TokenUserDTO toTokenUsuarioDTO(UserToken token) throws Exception {
		TokenUserDTO tokenUsuarioDTO = new TokenUserDTO();
		tokenUsuarioDTO.setToken(token.getToken().getToken());
		tokenUsuarioDTO.setUserIdentification(token.getUser().getUserId());
		return tokenUsuarioDTO;
	}
}
