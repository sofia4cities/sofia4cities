package com.indracompany.sofia2.config.service.digitaltwin;

import lombok.Getter;
import lombok.Setter;

public class EventsDigitalTwinTypeDTO {
	
	public EventsDigitalTwinTypeDTO(String id, String type, String name, Boolean status, String description) {
		super();
		this.id = id;
		this.type = type;
		this.name = name;
		this.status = status;
		this.description = description;
	}

	@Getter
	@Setter
	private String id;

	
	@Getter
	@Setter
	private String type;
	
	@Getter
	@Setter
	private String name;
	
	@Getter
	@Setter
	private Boolean status;
	
	@Getter
	@Setter
	private String description;

}
