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
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.RoleRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.repository.UserTokenRepository;
import com.indracompany.sofia2.config.services.exceptions.UserServiceException;
import com.indracompany.sofia2.config.services.utils.ServiceUtils;

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
	
	
	@Autowired OntologyRepository ontologyRepository;
	
	
	
	@Override
	public boolean isUserAdministrator(User user) {
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.name()))
			return true;
		if (user.getRole().getRoleParent() != null
				&& user.getRole().getRoleParent().getId().equals(Role.Type.ROLE_ADMINISTRATOR.name()))
			return true;
		return false;
	}

	@Override
	public boolean isUserDeveloper(User user) {
		if (user.getRole().getId().equals(Role.Type.ROLE_DEVELOPER.name()))
			return true;
		if (user.getRole().getRoleParent() != null
				&& user.getRole().getRoleParent().getId().equals(Role.Type.ROLE_DEVELOPER.name()))
			return true;
		return false;
	}

	@Override
	public Token getToken(String token) {
		return tokenRepository.findByToken(token);
	}

	@Override
	public UserToken getUserToken(String user, String token) {
		return userTokenRepository.findByUserAndToken(user, token);
	}

	@Override
	public User getUser(UserToken token) {
		return token.getUser();
	}

	@Override
	public User getUserByToken(String token) {
		final UserToken usertoken = userTokenRepository.findByToken(token);
		final User user = usertoken.getUser();
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
	public List<UserToken> getUserToken(User userId) {
		return this.userTokenRepository.findByUser(userId);
	}

	@Override
	public List<User> getAllUsers() {
		return this.userRepository.findAll();
	}

	@Override
	public List<User> getAllUsersByCriteria(String userId, String fullName, String email, String roleType,
			Boolean active) {
		List<User> users = new ArrayList<>();

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
		
		if (user.getPassword().length() < 7) {
			throw new UserServiceException("Password has to be at least 7 characters");
		}
		
		if (!this.userExists(user)) {
			log.debug("User no exist, creating...");
			user.setRole(this.roleTypeRepository.findByName(user.getRole().getName()));
			this.userRepository.save(user);
			
			String collectionAuditName = ServiceUtils.getAuditCollectionName (user.getUserId());

			if (ontologyRepository.findByIdentification(collectionAuditName) == null) {
				Ontology ontology = new Ontology();
				ontology.setJsonSchema("{}");
				ontology.setIdentification(collectionAuditName);
				ontology.setDescription("Ontology Audit for user " + user.getUserId());
				ontology.setActive(true);
				ontology.setRtdbClean(true);
				ontology.setRtdbToHdb(true);
				ontology.setPublic(false);
				ontology.setUser(user);
				
				ontologyRepository.save(ontology);
			}
						
		} else {
			throw new UserServiceException("User already exists in Database");
		}
	}
	
	@Override
	public void registerRoleDeveloper(User user) {
		
		user.setRole(getRole(Role.Type.ROLE_DEVELOPER));
		user.setActive(true);
		log.debug("Creating user with Role Developer default");

		this.createUser(user);

	}

	@Override
	public void registerRoleUser(User user) {

		user.setActive(true);
		user.setRole(getRole(Role.Type.ROLE_USER));
		log.debug("Creating user with Role User default");

		this.createUser(user);

	}

	@Override
	public boolean userExists(User user) {
		if (this.userRepository.findByUserId(user.getUserId()) != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void updatePassword(User user) {
		if (this.userExists(user)) {
			final User userDb = this.userRepository.findByUserId(user.getUserId());
			userDb.setPassword(user.getPassword());
		}
	}

	@Override
	public void updateUser(User user) {
		if (this.userExists(user)) {
			final User userDb = this.userRepository.findByUserId(user.getUserId());
			userDb.setEmail(user.getEmail());

			if (user.getRole() != null)
				userDb.setRole(this.roleTypeRepository.findByName(user.getRole().getName()));

			// Update dateDeleted for in/active user
			if (!userDb.isActive() && user.isActive()) {
				userDb.setDateDeleted(null);
			}
			if (userDb.isActive() && !user.isActive()) {
				userDb.setDateDeleted(new Date());
			}

			userDb.setActive(user.isActive());
			if (user.getDateDeleted() != null) {
				userDb.setDateDeleted(user.getDateDeleted());
			}
			userDb.setFullName(user.getFullName());
			this.userRepository.save(userDb);
		} else {
			throw new UserServiceException("Cannot update user that does not exist");
		}
	}

	@Override
	public Role getUserRole(String role) {
		return this.roleTypeRepository.findByName(role);
	}

	@Override
	public void deleteUser(String userId) {
		final User user = this.userRepository.findByUserId(userId);
		if (user != null) {
			user.setDateDeleted(new Date());
			user.setActive(false);
			this.userRepository.save(user);
		} else {
			throw new UserServiceException("Cannot delete user that does not exist");
		}
	}
	
	Role getRole(Role.Type roleType) {
		final Role r = new Role();
		r.setName(roleType.name());
		r.setIdEnum(roleType);
		return r;
	}

	@Override
	public List<ClientPlatform> getClientsForUser(User user) {
		List<ClientPlatform> clients = new ArrayList<ClientPlatform>();
		clients = this.clientPlatformRepository.findByUser(user);
		return clients;
	}

	@Override
	public UserToken getUserToken(String token) {
		return userTokenRepository.findByToken(token);
	}

	@Override
	public boolean emailExists(User user) {

		if ((this.userRepository.findByEmail(user.getEmail())).size() != 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public User getUserByIdentification(String identification) {
		return userRepository.findByUserId(identification);
	}
	
}
