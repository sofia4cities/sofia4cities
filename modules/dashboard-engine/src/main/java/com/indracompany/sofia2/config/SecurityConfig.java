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
package com.indracompany.sofia2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.indracompany.sofia2.filter.CustomFilter;
import com.indracompany.sofia2.security.CustomBasicAuthenticationEntryPoint;
import com.indracompany.sofia2.security.CustomUserDetailsService;
import com.indracompany.sofia2.security.ri.Sofia2ConfigDBAuthenticationProvider;

/**
 * @EnableGlobalAuthentication annotates:
 * @EnableWebSecurity
 * @EnableWebMvcSecurity
 * @EnableGlobalMethodSecurity Passing in 'prePostEnabled = true' allows:
 *                             <p>
 *                             Pre/Post annotations such
 *                             as: @PreAuthorize("hasRole('ROLE_USER')")
 */

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private CustomBasicAuthenticationEntryPoint authenticationEntryPoint;

	/**
	 * Authentication beans
	 */

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * @Bean public DaoAuthenticationProvider authenticationProvider() { final
	 * DaoAuthenticationProvider bean = new CustomDaoAuthenticationProvider();
	 * bean.setUserDetailsService(customUserDetailsService);
	 * bean.setPasswordEncoder(encoder()); return bean; }
	 */

	@Bean
	public Sofia2ConfigDBAuthenticationProvider authenticationProviderSofia2() {
		final Sofia2ConfigDBAuthenticationProvider bean = new Sofia2ConfigDBAuthenticationProvider();
		return bean;
	}

	/**
	 * Order of precedence is very important.
	 * <p>
	 * Matching occurs from top to bottom - so, the topmost match succeeds first.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().authorizeRequests()
				// .antMatchers(HttpMethod.OPTIONS,"/loginRest").permitAll()
				/* .antMatchers("/secured/chat") */
				.antMatchers("/**").authenticated().and().httpBasic().authenticationEntryPoint(authenticationEntryPoint)
				.and().authenticationProvider(authenticationProviderSofia2());

		/** Disabled for local testing */
		http.csrf().disable();

		/**
		 * This is solely required to support H2 console viewing in Spring MVC with
		 * Spring Security
		 */
		http.headers().frameOptions().disable();

		http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);

	}

	/*
	 * @Override protected void configure(final AuthenticationManagerBuilder auth)
	 * throws Exception { auth.authenticationProvider(authenticationProvider()); }
	 */

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
	}

}
