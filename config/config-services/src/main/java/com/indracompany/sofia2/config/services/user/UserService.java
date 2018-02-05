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

import java.util.List;

import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;

public interface UserService {
	
	public Token getToken(String token) ;
	public UserToken getUserToken(Token token);
	public User getUser(UserToken token);
	public User getUserByToken(String token);
	public User getUser(String userId);
	public List<Role> getAllRoles();
	public UserToken getUserToken(User userId);
	public List<User> getAllUsers();
	public List<User> getAllUsersByCriteria(String userId, String fullName, String email, String roleType,Boolean active);
	public void createUser(User user);
	public boolean userExists(User user);
	public void updateUser(User user);
	public Role getUserRole(String role);
	public void deleteUser(String userId);

}
