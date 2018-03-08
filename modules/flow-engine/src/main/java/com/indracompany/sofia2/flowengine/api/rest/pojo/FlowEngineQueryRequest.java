package com.indracompany.sofia2.flowengine.api.rest.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowEngineQueryRequest {
	@Getter
	@Setter
	private String ontology;
	@Getter
	@Setter
	private String queryType;
	@Getter
	@Setter
	private String query;
	@Getter
	@Setter
	private String targetDB;
	@Getter
	@Setter
	private String authentication;
}
