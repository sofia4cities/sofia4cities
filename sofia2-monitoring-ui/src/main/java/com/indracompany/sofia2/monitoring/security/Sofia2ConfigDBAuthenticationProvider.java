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
package com.indracompany.sofia2.monitoring.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.UserCDB;
import com.indracompany.sofia2.config.repository.UserCDBRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Sofia2ConfigDBAuthenticationProvider implements AuthenticationProvider {


	@Autowired 
    private UserCDBRepository userRepository;
	
	 @Autowired
	private CounterService counterService;
	 
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String name = authentication.getName();
        Object credentials = authentication.getCredentials();
        log.info("credentials class: " + credentials.getClass());
        if (!(credentials instanceof String)) {
            return null;
        }
        String password = credentials.toString();
	
        UserCDB user = userRepository.findByUserIdAndPassword(name,password);

        if (user==null) {
			log.info("authenticate: User or password incorrect: " + name);
            throw new BadCredentialsException("Authentication failed for " + name);
        }
        if (!user.getRoleTypeId().getName().equalsIgnoreCase("ROLE_OPERATIONS")) {
			log.info("authenticate: Role of user "+user.getUserId()+" is not ROLE_OPERATIONS: ");
            throw new BadCredentialsException("Authentication failed for user "+user.getUserId()+"User has not ROLE_OPERATIONS: ");        	
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRoleTypeId().getName()));
        Authentication auth = new
                UsernamePasswordAuthenticationToken(name, password, grantedAuthorities);
        
        counterService.increment("_sofia2.monitoring.metrics.logins");
        return auth;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}