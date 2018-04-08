package com.indracompany.sofia2.digitaltwin.event.model;

import lombok.Getter;
import lombok.Setter;

public class RegisterMessage {
	
	@Getter
	@Setter
	private String id;
	
	@Getter
	@Setter
	private String endpoint;

}
