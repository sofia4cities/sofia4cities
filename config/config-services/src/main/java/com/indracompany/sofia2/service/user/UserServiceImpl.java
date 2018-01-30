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
package com.indracompany.sofia2.service.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.RoleType;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.RoleTypeRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.repository.UserTokenRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleTypeRepository roleTypeRepository;
	@Autowired
	UserTokenRepository userTokenRepository;
	@Autowired
	TokenRepository tokenRepository;
	
	public List<Token> getToken(String token) {
		return tokenRepository.findByToken(token);
	}
	
	public UserToken getUserToken(Token token) {
		return userTokenRepository.findByToken(token);
	}
	
	public User getUser(UserToken token) {
		return token.getUserId();
	}
	
	public User getUserByToken(String token) {
		List<Token> listToken = getToken(token);
		Token theToken = listToken.get(0);
		String  userId  = userTokenRepository.findUserIdByTokenValue(theToken.getToken());
		User user = getUser(userId);
		return user;
	}


	public User getUser(String userId) {
		return userRepository.findByUserId(userId);
	}
	public List<RoleType> getAllRoles() {
		return roleTypeRepository.findAll();
	}
	public UserToken getUserToken(User userId)
	{
		return this.userTokenRepository.findByUserId(userId);
	}
	public List<User> getAllUsers()
	{
		return this.userRepository.findAll();
	}
	public List<User> getAllUsersByCriteria(String userId, String fullName, String email, String roleType,Boolean active)
	{
		List<User> users= new ArrayList<User>();

		if(active!=null)
		{
			users=this.userRepository.findByUserIdOrFullNameOrEmailOrRoleTypeAndActive(userId, fullName, email,roleType, active);
			
		}else{
			
			users=this.userRepository.findByUserIdOrFullNameOrEmailOrRoleType(userId, fullName, email, roleType);
		}
		
		return users;
		
		
	}
	
	public void createUser(User user){		
		if(!this.userExists(user)){
			user.setRoleTypeId(this.roleTypeRepository.findByName(user.getRoleTypeId().getName()));
			this.userRepository.save(user);
		}
	}
	public boolean userExists(User user)
	{
		if(this.userRepository.findByUserId(user.getUserId())!=null) return true;
		else return false;
	}
	public void updateUser(User user)
	{
		if(this.userExists(user))
		{
			User userDb=this.userRepository.findByUserId(user.getUserId());
			userDb.setPassword(user.getPassword());
			userDb.setEmail(user.getEmail());
			userDb.setRoleTypeId(this.roleTypeRepository.findByName(user.getRoleTypeId().getName()));
			userDb.setActive(user.isActive());
			userDb.setUpdatedAt(new Date());
			userDb.setFullName(user.getFullName());
			this.userRepository.save(userDb);
		}
	}
}
