package com.indracompany.sofia2.flowengine.api.rest.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InsertRequest {
	@Getter
	@Setter
	private String ontology;
	@Getter
	@Setter
	private String data;
	@Getter
	@Setter
	private String authentication;

}
