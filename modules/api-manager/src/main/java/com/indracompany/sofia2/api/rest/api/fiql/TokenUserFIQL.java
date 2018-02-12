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

package com.indracompany.sofia2.api.rest.api.fiql;

import com.indracompany.sofia2.api.rest.api.dto.TokenUserDTO;
import com.indracompany.sofia2.config.model.UserToken;

public final class TokenUserFIQL {
	
	

	public static TokenUserDTO toTokenUsuarioDTO(UserToken token) throws Exception {
		
		if (token!=null) {
			TokenUserDTO tokenUsuarioDTO = new TokenUserDTO();
			tokenUsuarioDTO.setToken(token.getToken());
			tokenUsuarioDTO.setUserIdentification(token.getUser().getUserId());
			return tokenUsuarioDTO;
		}
		else {
			TokenUserDTO tokenUsuarioDTO = new TokenUserDTO();
			tokenUsuarioDTO.setToken("NO_VALID_TOKEN_FOUND");
			tokenUsuarioDTO.setUserIdentification(token.getUser().getUserId());
			return tokenUsuarioDTO;
		}
		
		
	}
}
