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

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
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