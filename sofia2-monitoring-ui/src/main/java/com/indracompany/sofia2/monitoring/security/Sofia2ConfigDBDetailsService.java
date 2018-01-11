package com.indracompany.sofia2.monitoring.security;

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