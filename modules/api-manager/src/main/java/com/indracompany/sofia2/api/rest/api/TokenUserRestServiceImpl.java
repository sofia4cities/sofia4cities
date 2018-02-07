
package com.indracompany.sofia2.api.rest.api;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.api.rest.api.dto.TokenUserDTO;
import com.indracompany.sofia2.api.rest.api.fiql.TokenUserFIQL;
import com.indracompany.sofia2.api.service.api.ApiServiceRest;

@Component("tokenUserRestService")
public class TokenUserRestServiceImpl implements TokenUserRestService {


	@Autowired
	private ApiServiceRest apiService;

	@Override
	public Response getTokenUser(String identificacion, String token) throws Exception {
		return Response.ok(TokenUserFIQL.toTokenUsuarioDTO(apiService.findTokenUserByIdentification(identificacion, token))).build();
	}

	@Override
	public Response addTokenUser(String identificacion, String token) throws Exception {
		TokenUserDTO tokenUsuarioDTO =TokenUserFIQL.toTokenUsuarioDTO(apiService.generateTokenUsuario(identificacion, token));
		return Response.ok( tokenUsuarioDTO.getToken()).type(MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response generateToken(String identificacion, String token) throws Exception {
		TokenUserDTO tokenUsuarioDTO =TokenUserFIQL.toTokenUsuarioDTO(apiService.generateTokenUsuario(identificacion, token));
		return Response.ok( tokenUsuarioDTO.getToken()).type(MediaType.APPLICATION_JSON).build();
	}

}
