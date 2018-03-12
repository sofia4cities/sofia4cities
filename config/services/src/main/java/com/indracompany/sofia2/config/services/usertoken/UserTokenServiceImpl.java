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
package com.indracompany.sofia2.config.services.usertoken;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.UserTokenRepository;

@Service

public class UserTokenServiceImpl implements UserTokenService {

	@Autowired
	private UserTokenRepository userTokenRepository;

	@Override
	public UserToken generateToken(User user) throws Exception{
		UserToken userToken = new UserToken();
		if (user.getUserId() != null) {
			userToken.setUser(user);
			userToken.setToken(UUID.randomUUID().toString().replaceAll("-", ""));
			if (this.userTokenRepository.findByToken(userToken.getToken()) == null) {
				userToken = this.userTokenRepository.save(userToken);
			} else {
				throw new Exception("Token with value " + userToken.getToken() + " already exists");
			}
		}
		return userToken;
	}

	@Override
	public UserToken getToken(User user) {
		return this.userTokenRepository.findByUser(user).get(0);
	}

	@Override
	public UserToken getTokenByToken(String token) {
		return this.userTokenRepository.findByToken(token);
	}

	@Override
	public void deactivateToken(UserToken userToken, boolean active) {
		this.userTokenRepository.save(userToken);

	}

	@Override
	public UserToken getTokenByID(String id) {
		return this.userTokenRepository.findById(id);
	}

	@Override
	public List<UserToken> getTokens(User user) {
		return this.userTokenRepository.findByUser(user);
	}

	@Override
	public void removeToken(User user, String token) {
		UserToken userToken = this.userTokenRepository.findByUserAndToken(user, token);
		if (userToken != null) {
			this.userTokenRepository.delete(userToken);
		}
	}

}
