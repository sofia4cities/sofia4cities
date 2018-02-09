package com.indracompany.sofia2.flowengine;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {

		httpSecurity
				// by default uses a Bean by the name of corsConfigurationSource
				.cors().and()
				// we don't need CSRF because our token is invulnerable
				.csrf().disable()

				.authorizeRequests()

				// allow anonymous resource requests

				.anyRequest().permitAll();
		// Custom JWT based security filter

		// disable page caching
		httpSecurity.headers().cacheControl();

		httpSecurity.headers().frameOptions().disable();
	}
}
