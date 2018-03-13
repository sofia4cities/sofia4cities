package com.indracompany.sofia2.config.service.digitaltwin;

import lombok.Getter;
import lombok.Setter;

public class PropertyDigitalTwinTypeDTO {
	
	public PropertyDigitalTwinTypeDTO(String id, String type, String name, String unit, String direction,
			String description) {
		super();
		this.id = id;
		this.type = type;
		this.name = name;
		this.unit = unit;
		this.direction = direction;
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
	private String unit;
	
	@Getter
	@Setter
	private String direction;
	
	@Getter
	@Setter
	private String description;

}
