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
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.sibcore;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.indra.sofia2.support.entity.gestion.dominio.master.Usuario;
import com.indra.sofia2.support.entity.gestion.dominio.master.UsuarioService;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Component
public class UsuarioServiceTestImpl implements UsuarioService {

	@Override
	public void persist(Usuario user) {
		com.indra.sofia2.support.entity.gestion.dominio.Usuario finalUser = converToExternalUser(user);
		finalUser.persist();
		user.setId(finalUser.getId());
	}

	@Override
	public void remove(Usuario user) {
		com.indra.sofia2.support.entity.gestion.dominio.Usuario finalUser = com.indra.sofia2.support.entity.gestion.dominio.UsuarioRepository.findUsuario(user.getId());
		finalUser.remove();
	}

	@Override
	public void merge(Usuario user) {
		com.indra.sofia2.support.entity.gestion.dominio.Usuario finalUser = com.indra.sofia2.support.entity.gestion.dominio.UsuarioRepository.findUsuario(user.getId());
		finalUser.setActivo(user.isActivo());
		finalUser.setEmail(user.getEmail());
		finalUser.setFechaalta(user.getFechaalta());
		finalUser.setFechabaja(user.getFechabaja());
		finalUser.setIdentificacion(user.getIdentificacion());
		finalUser.setNombrecompleto(user.getNombrecompleto());
		finalUser.setPassword(user.getPassword());
		finalUser.setRolId(user.getRol());
		finalUser.merge();
	}

	@Override
	public long countUser() {
		return com.indra.sofia2.support.entity.gestion.dominio.UsuarioRepository.countUsuarios();
	}

	private com.indra.sofia2.support.entity.gestion.dominio.Usuario converToExternalUser(Usuario user){
		com.indra.sofia2.support.entity.gestion.dominio.Usuario returnUser = new com.indra.sofia2.support.entity.gestion.dominio.Usuario();
		returnUser.setActivo(user.isActivo());
		returnUser.setEmail(user.getEmail());
		returnUser.setFechaalta(user.getFechaalta());
		returnUser.setFechabaja(user.getFechabaja());
		returnUser.setIdentificacion(user.getIdentificacion());
		returnUser.setId(user.getId());
		returnUser.setIdentificacion(user.getIdentificacion());
		returnUser.setNombrecompleto(user.getNombrecompleto());
		returnUser.setPassword(user.getPassword());
		returnUser.setRolId(user.getRol());
		return returnUser;
	}
	
	private Usuario converToPlatformUser(com.indra.sofia2.support.entity.gestion.dominio.Usuario user){
		Usuario returnUser = new Usuario();
		returnUser.setActivo(user.isActivo());
		returnUser.setEmail(user.getEmail());
		returnUser.setFechaalta(user.getFechaalta());
		returnUser.setFechabaja(user.getFechabaja());
		returnUser.setIdentificacion(user.getIdentificacion());
		returnUser.setId(user.getId());
		returnUser.setIdentificacion(user.getIdentificacion());
		returnUser.setNombrecompleto(user.getNombrecompleto());
		returnUser.setPassword(user.getPassword());
		returnUser.setRol(user.getRolId());
		return returnUser;
	}
	
	@Override
	public List<Usuario> findAllUser() {
		List<com.indra.sofia2.support.entity.gestion.dominio.Usuario> finalUserList = com.indra.sofia2.support.entity.gestion.dominio.UsuarioRepository.findAllUsuarios();
		List<Usuario> userList = new ArrayList<Usuario>();
		for (com.indra.sofia2.support.entity.gestion.dominio.Usuario user : finalUserList){
			userList.add(converToPlatformUser(user));
		}
		return userList;
				
	}

	@Override
	public Usuario findUser(String idUsuario) {
		return converToPlatformUser(com.indra.sofia2.support.entity.gestion.dominio.UsuarioRepository.findUsuario(idUsuario));
	}

	@Override
	public List<Usuario> findUserByCriteria(Usuario usuario) {
		StringBuffer query = new StringBuffer("SELECT o FROM Usuario AS o");
		List<Object> parametros = new ArrayList<Object>();
		boolean where = true;
		boolean and = false;
		if (usuario.getId()!=null && !usuario.getId().equals("")){
			if (where){
				query.append(" WHERE");
			}	
			if (and){
				query.append(" AND");
			}
			query.append(" o.id = ?");
			parametros.add(usuario.getId());
			where=false;
			and=true;
		}
		if (usuario.getEmail()!=null && !usuario.getEmail().equals("")){
			if (where){
				query.append(" WHERE");
			}
			if (and){
				query.append(" AND");
			}
			query.append(" o.email = ?");
			parametros.add(usuario.getEmail());
			where=false;
			and=true;
		}
		if (usuario.getFechaalta()!=null){
			if (where){
				query.append(" WHERE");
			}
			if (and){
				query.append(" AND");
			}
			query.append(" o.fechaalta = ?");
			parametros.add(usuario.getFechaalta());
			where=false;
			and=true;
		}
		if (usuario.getFechabaja()!=null){
			if (where){
				query.append(" WHERE");
			}
			if (and){
				query.append(" AND");
			}
			query.append(" o.fechabaja = ?");
			parametros.add(usuario.getFechabaja());
			where=false;
			and=true;
		}
		if (usuario.getIdentificacion()!=null && !usuario.getIdentificacion().equals("")){
			if (where){
				query.append(" WHERE");
			}
			if (and){
				query.append(" AND");
			}
			query.append(" o.identificacion = ?");
			parametros.add(usuario.getIdentificacion());
			where=false;
			and=true;
		}
		if (usuario.getNombrecompleto()!=null && !usuario.getNombrecompleto().equals("")){
			if (where){
				query.append(" WHERE");
			}
			if (and){
				query.append(" AND");
			}
			query.append(" o.nombrecompleto = ?");
			parametros.add(usuario.getNombrecompleto());
			where=false;
			and=true;
		}
		if (usuario.getPassword()!=null && !usuario.getPassword().equals("")){
			if (where){
				query.append(" WHERE");
			}
			if (and){
				query.append(" AND");
			}
			query.append(" o.password = ?");
			parametros.add(usuario.getPassword());
			where=false;
			and=true;
		}
		if (usuario.getRol()!=null){
			if (where){
				query.append(" WHERE");
			}
			if (and){
				query.append(" AND");
			}
			query.append(" o.rolId = ?");
			parametros.add(usuario.getRol());
			where=false;
			and=true;
		}
		List<com.indra.sofia2.support.entity.gestion.dominio.Usuario> finalUserList = com.indra.sofia2.support.entity.gestion.dominio.UsuarioRepository.findUsuarios(query.toString(), parametros);
		List<Usuario> userList = new ArrayList<Usuario>();
		for (com.indra.sofia2.support.entity.gestion.dominio.Usuario user : finalUserList){
			userList.add(converToPlatformUser(user));
		}
		return userList;	
	}

	@Override
	public List<Usuario> findUserByIdentificacion(String identificacion)
			throws EmptyResultDataAccessException {
		List<com.indra.sofia2.support.entity.gestion.dominio.Usuario> finalUserList = com.indra.sofia2.support.entity.gestion.dominio.UsuarioRepository.findUsuariosByIdentificacionEquals(identificacion).getResultList();
		List<Usuario> userList = new ArrayList<Usuario>();
		for (com.indra.sofia2.support.entity.gestion.dominio.Usuario user : finalUserList){
			userList.add(converToPlatformUser(user));
		}
		return userList;	
	}
	
	@Override
	public List<Usuario> findUsers(String qlString, List<Object> parametros){
		List<com.indra.sofia2.support.entity.gestion.dominio.Usuario> finalUserList = com.indra.sofia2.support.entity.gestion.dominio.UsuarioRepository.findUsuarios(qlString, parametros);
		List<Usuario> userList = new ArrayList<Usuario>();
		for (com.indra.sofia2.support.entity.gestion.dominio.Usuario user : finalUserList){
			userList.add(converToPlatformUser(user));
		}
		return userList;	
	}

	@Override
	public Usuario findLoginUser(String identificador, String credential,
			String sourceInfo) throws EmptyResultDataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Usuario findUserByEmail(String email) throws NotImplementedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateRestToken(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public List<Usuario> findIdentificacionUserNoAdmin() throws EmptyResultDataAccessException {
		throw new NotImplementedException();
	}
}
