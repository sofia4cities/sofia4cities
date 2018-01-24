package com.indracompany.sofia2.service.token;

import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;

public interface TokenService {
	
	public Token getTokenUserId(User userId);

}
