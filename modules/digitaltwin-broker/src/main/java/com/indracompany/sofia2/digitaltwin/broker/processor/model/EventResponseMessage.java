package com.indracompany.sofia2.digitaltwin.broker.processor.model;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

public class EventResponseMessage {
	
	public EventResponseMessage(String message, HttpStatus status) {
		super();
		this.message = message;
		this.code = status;
	}

	@Getter
	@Setter
	private String message;
	
	@Getter
	@Setter
	private HttpStatus code;

}
