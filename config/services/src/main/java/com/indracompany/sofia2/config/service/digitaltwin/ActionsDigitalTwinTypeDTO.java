package com.indracompany.sofia2.config.service.digitaltwin;

import lombok.Getter;
import lombok.Setter;

public class ActionsDigitalTwinTypeDTO {
	

	public ActionsDigitalTwinTypeDTO(String id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	@Getter
	@Setter
	private String id;

	@Getter
	@Setter
	private String name;
	
	@Getter
	@Setter
	private String description;

}
