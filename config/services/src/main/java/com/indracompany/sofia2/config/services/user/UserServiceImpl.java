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
package com.indracompany.sofia2.config.services.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.RoleRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.repository.UserTokenRepository;
import com.indracompany.sofia2.config.services.exceptions.UserServiceException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleTypeRepository;
	@Autowired
	private UserTokenRepository userTokenRepository;
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private ClientPlatformRepository clientPlatformRepository;

	@Override
	public Token getToken(String token) {
		return tokenRepository.findByToken(token);
	}

	@Override
	public UserToken getUserToken(String token) {
		return userTokenRepository.findByToken(token);
	}

	@Override
	public User getUser(UserToken token) {
		return token.getUser();
	}

	@Override
	public User getUserByToken(String token) {
		UserToken usertoken = userTokenRepository.findByToken(token);
		User user = usertoken.getUser();
		return user;
	}

	@Override
	public User getUser(String userId) {
		return userRepository.findByUserId(userId);
	}

	@Override
	public List<Role> getAllRoles() {
		return roleTypeRepository.findAll();
	}

	@Override
	public UserToken getUserToken(User userId) {
		return this.userTokenRepository.findByUser(userId);
	}

	@Override
	public List<User> getAllUsers() {
		return this.userRepository.findAll();
	}

	@Override
	public List<User> getAllUsersByCriteria(String userId, String fullName, String email, String roleType,
			Boolean active) {
		List<User> users = new ArrayList<User>();

		if (active != null) {
			users = this.userRepository.findByUserIdOrFullNameOrEmailOrRoleTypeAndActive(userId, fullName, email,
					roleType, active);
		} else {
			users = this.userRepository.findByUserIdOrFullNameOrEmailOrRoleType(userId, fullName, email, roleType);
		}

		return users;

	}

	@Override
	public void createUser(User user) {
		if (!this.userExists(user)) {
			log.debug("User no exist, creating...");
			user.setRole(this.roleTypeRepository.findByName(user.getRole().getName()));
			this.userRepository.save(user);
		} else
			throw new UserServiceException("User already exists in Database");
	}

	@Override
	public boolean userExists(User user) {
		if (this.userRepository.findByUserId(user.getUserId()) != null)
			return true;
		else
			return false;
	}

	@Override
	public void updateUser(User user) {
		if (this.userExists(user)) {
			User userDb = this.userRepository.findByUserId(user.getUserId());
			userDb.setPassword(user.getPassword());
			userDb.setEmail(user.getEmail());
			userDb.setRole(this.roleTypeRepository.findByName(user.getRole().getName()));

			// Update dateDeleted for in/active user
			if (!userDb.isActive() && user.isActive())
				userDb.setDateDeleted(null);
			if (userDb.isActive() && !user.isActive())
				userDb.setDateDeleted(new Date());

			userDb.setActive(user.isActive());
			if (user.getDateDeleted() != null)
				userDb.setDateDeleted(user.getDateDeleted());
			userDb.setFullName(user.getFullName());
			this.userRepository.save(userDb);
		} else
			throw new UserServiceException("Cannot update user that does not exist");
	}

	@Override
	public Role getUserRole(String role) {
		return this.roleTypeRepository.findByName(role);
	}

	@Override
	public void deleteUser(String userId) {
		User user = this.userRepository.findByUserId(userId);
		if (user != null) {
			user.setDateDeleted(new Date());
			user.setActive(false);
			this.userRepository.save(user);
		} else
			throw new UserServiceException("Cannot delete user that does not exist");
	}

	Role getRoleDeveloper() {
		Role r = new Role();
		r.setName(Role.Type.ROLE_DEVELOPER.name());
		r.setIdEnum(Role.Type.ROLE_DEVELOPER);
		return r;
	}

	@Override
	public void registerUser(User user) {
		// FIXME
		if (user.getPassword().length() < 7)
			throw new UserServiceException("Password has to be at least 7 characters");
		if (this.userExists(user))
			throw new UserServiceException(
					"User ID:" + user.getUserId() + " exists in the system. Please select another User ID.");

		user.setRole(getRoleDeveloper());
		log.debug("Creating user with Role Developer default");

		this.userRepository.save(user);

	}

	@Override
	public List<ClientPlatform> getClientsForUser(User user) {
		List<ClientPlatform> clients = new ArrayList<ClientPlatform>();
		clients = this.clientPlatformRepository.findByUser(user);
		return clients;
	}
}
