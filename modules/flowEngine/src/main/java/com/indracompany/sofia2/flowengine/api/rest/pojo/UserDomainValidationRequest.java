package com.indracompany.sofia2.flowengine.api.rest.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDomainValidationRequest {

	@Getter
	@Setter
	@JsonProperty("dominio")
	private String domainId;

	@Getter
	@Setter
	@JsonProperty("id_usuario")
	private String userId;

	@Getter
	@Setter
	private String password;
}
