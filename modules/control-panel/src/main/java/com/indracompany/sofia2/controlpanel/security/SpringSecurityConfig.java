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
package com.indracompany.sofia2.controlpanel.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Slf4j
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
    public FilterRegistrationBean corsFilterOauth() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

	@Autowired
	private AccessDeniedHandler accessDeniedHandler;
	@Autowired
	private AuthenticationProvider authenticationProvider;
	
	@Autowired
	private LogoutSuccessHandler logoutSuccessHandler;

	@Autowired
	private Securityhandler successHandler;
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(new URlPriorFilter(), BasicAuthenticationFilter.class);
		http.csrf().disable().authorizeRequests()
				.antMatchers("/", 
						"/home", 
						"/favicon.ico").permitAll()
				.antMatchers("/api/applications", 
						"/api/applications/").permitAll()
				.antMatchers("/users/register").permitAll()
				.antMatchers("/health/", 
						"/info", 
						"/metrics", 
						"/trace", 
						"/api", 
						"/dashboards/**", 
						"/gadgets/**", 
						"/datasources/**",	
						"/v2/api-docs/",
		        		"/v2/api-docs/**",
		        		"/swagger-resources/",
		        		"/swagger-resources/**",
		        		"/swagger-ui.html").permitAll()
				.antMatchers("/oauth/").permitAll()
				.antMatchers("/api-ops","/api-ops/**").permitAll()
				.antMatchers("/management","/management/**").permitAll()
				.antMatchers("/admin").hasAnyRole("ROLE_ADMINISTRATOR").antMatchers("/admin/**")
				.hasAnyRole("ROLE_ADMINISTRATOR").anyRequest().authenticated().and().formLogin().loginPage("/login")
				.successHandler(successHandler).permitAll().and().logout().logoutSuccessHandler(logoutSuccessHandler).permitAll().and().sessionManagement()
				.invalidSessionUrl("/login").maximumSessions(10).expiredUrl("/login").maxSessionsPreventsLogin(false)
				.sessionRegistry(sessionRegistry()).and().sessionFixation().none().and().exceptionHandling()
				.accessDeniedHandler(accessDeniedHandler);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
		builder.authenticationProvider(authenticationProvider);
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**","/webjars/**");
	}

}
