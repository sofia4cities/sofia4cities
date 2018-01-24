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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.RoleType;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;
import com.indracompany.sofia2.config.repository.RoleTypeRepository;
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
		RoleType role=null;
		//convert role String to RoleType Object
		if(roleType!=null){
			role= new RoleType();
			role.setName(roleType);
		}
		if(active!=null)
		{
			users=this.userRepository.findUsersByUserIdOrFullNameOrEmailOrRoleTypeIdAndActive(userId, fullName, email,role, active);
			
		}else{
			
			users=this.userRepository.findUsersByUserIdOrFullNameOrEmailOrRoleTypeId(userId, fullName, email, role);
		}
		
		return users;
		
		
	}
}
