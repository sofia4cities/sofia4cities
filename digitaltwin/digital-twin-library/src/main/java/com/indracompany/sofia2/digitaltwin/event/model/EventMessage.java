package com.indracompany.sofia2.digitaltwin.event.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class EventMessage {

	@Getter
	@Setter
	private String id;
	
	@Getter
	@Setter
	private String event;
	
	@Getter
	@Setter
	Map<String, Object> status;
}
