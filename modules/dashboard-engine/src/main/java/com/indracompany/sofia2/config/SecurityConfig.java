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

import com.indracompany.sofia2.security.CustomAccessDeniedHandler;
import com.indracompany.sofia2.security.CustomBasicAuthenticationEntryPoint;
import com.indracompany.sofia2.security.CustomDaoAuthenticationProvider;
import com.indracompany.sofia2.security.CustomLoginSuccessHandler;
import com.indracompany.sofia2.security.CustomLogoutSuccessHandler;
import com.indracompany.sofia2.security.CustomUserDetailsService;

import filter.CustomFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * @EnableGlobalAuthentication annotates:
 * @EnableWebSecurity
 * @EnableWebMvcSecurity
 * @EnableGlobalMethodSecurity Passing in 'prePostEnabled = true' allows:
 * <p>
 * Pre/Post annotations such as:
 * @PreAuthorize("hasRole('ROLE_USER')")
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
     * Login, Logout, Success, and Access Denied beans/handlers
     */

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new CustomLoginSuccessHandler();
    }

    /**
     * Authentication beans
     */

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider bean = new CustomDaoAuthenticationProvider();
        bean.setUserDetailsService(customUserDetailsService);
        bean.setPasswordEncoder(encoder());
        return bean;
    }

    /**
     * Order of precedence is very important.
     * <p>
     * Matching occurs from top to bottom - so, the topmost match succeeds first.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        		.cors()
        		.and()
                .authorizeRequests()
                //.antMatchers(HttpMethod.OPTIONS,"/loginRest").permitAll()
                /*.antMatchers("/secured/chat")*/
                .antMatchers("/**")
                .authenticated()
                .and()
                /*.formLogin()
                .loginPage("/login").permitAll()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/authenticate")
                .successHandler(loginSuccessHandler())
                .failureUrl("/denied").permitAll()
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler())
                .and()*/
                /**
                 * Applies to User Roles - not to login failures or unauthenticated access attempts.
                 */
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .authenticationProvider(authenticationProvider());

        /** Disabled for local testing */
        http
                .csrf().disable();

        /** This is solely required to support H2 console viewing in Spring MVC with Spring Security */
        http
                .headers()
                .frameOptions()
                .disable();
        
        http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
        
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

}
