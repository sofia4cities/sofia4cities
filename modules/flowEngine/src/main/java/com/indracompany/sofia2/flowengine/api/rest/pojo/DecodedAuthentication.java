package com.indracompany.sofia2.flowengine.api.rest.pojo;

import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class DecodedAuthentication {

	@Getter
	@Setter
	@JsonProperty("id_usuario")
	private String userId;

	@Getter
	@Setter
	private String password;

	public DecodedAuthentication(String authentication) {
		String auth = new String(Base64.getDecoder().decode(authentication));
		this.userId = auth.split(":")[0];
		this.password = auth.split(":")[1];
	}
}
