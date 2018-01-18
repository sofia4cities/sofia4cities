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
package com.indracompany.sofia2.controlpanel.security.plugin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.UserCDB;
import com.indracompany.sofia2.config.repository.UserCDBRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Sofia2ConfigDBDetailsService implements UserDetailsService {

	@Autowired 
    private UserCDBRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserCDB user = userRepository.findByUserId(username);		
		
		if (user==null) {
			log.info("LoadUserByUserName: User not found by name: " + username);
            throw new UsernameNotFoundException("User not found by name: " + username);
		}		

		return toUserDetails(user);
	}
	
	private UserDetails toUserDetails(UserCDB userObject) {
        return User.withUsername(userObject.getUserId())
                   .password(userObject.getPassword())
                   .roles(userObject.getRole().getName()).build();
    }

}
