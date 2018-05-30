package com.indracompany.sofia2.controlpanel.controller.user;

import java.util.List;

import lombok.Data;

@Data
public class OntologyRemoveRevokeDto {

	private List<String> ontologies;
	private String userId;

}
