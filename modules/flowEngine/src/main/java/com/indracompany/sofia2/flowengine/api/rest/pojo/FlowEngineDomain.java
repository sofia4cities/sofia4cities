package com.indracompany.sofia2.flowengine.api.rest.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class FlowEngineDomain {

	@Getter
	@Setter
	private String domain;

	@Getter
	@Setter
	private int port;

	@Getter
	@Setter
	private String home;

	@Getter
	@Setter
	private int servicePort;

}
