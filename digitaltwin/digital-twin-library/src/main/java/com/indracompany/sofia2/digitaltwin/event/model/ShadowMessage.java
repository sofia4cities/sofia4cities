package com.indracompany.sofia2.digitaltwin.event.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class ShadowMessage {

	@Getter
	@Setter
	String id;
	
	@Getter
	@Setter
	Map<String, Object> status;
	
	
	
}
