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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class UserInfo implements UserDetails, Serializable {

	private static final long serialVersionUID = 1L;

	@Getter @Setter private String id;
	@Getter @Setter private String username;

	@Getter @Setter private String email;
	@Getter @Setter private String password;
	@Getter @Setter  Date creationDate;
	@Setter private boolean enabled;
	@Getter @Setter private Date deleteDate;
	@Getter @Setter private UserRole role;

	@Getter @Setter private String signInProvider;
	@Getter @Setter private String firstName;
	@Getter @Setter  String lastName;
	@Getter @Setter private String fullName;

	@Getter @Setter private List<String> groups;


	//TODO:REVISAR QUE ES CORRECTO
	public boolean isRole(UserRole role) {
		if (this.role==null || role==null) return false;
		if (this.role.equals(role)) return true;
		return false;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//TODO:Implementar: ver cómo hacerlo, ROLES en BDC?
		throw new RuntimeException("NotImplementedException: Implementar: ver cómo hacerlo, ROLES en BDC?");
		/*
		List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>();	
		if (role!=null){
			AUTHORITIES.add(new SimpleGrantedAuthority(rol.getNombre()));
			if (rol.getRolparent()!=null && rol.getId()!=rol.getRolparent().getId()){
				AUTHORITIES.add(new SimpleGrantedAuthority(rol.getRolparent().getNombre()));
			}
		}
		return AUTHORITIES;	
		 */
	}

	@Override
	public boolean isAccountNonExpired() {
		if (deleteDate==null || Calendar.getInstance().after(deleteDate)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean isAccountNonLocked() {
		return enabled;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		if (deleteDate==null || Calendar.getInstance().after(deleteDate)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}


}
