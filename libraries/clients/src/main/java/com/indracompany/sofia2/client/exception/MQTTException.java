package com.indracompany.sofia2.client.exception;

public class MQTTException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public MQTTException(String message) {
		super(message);
	}

	public MQTTException(String message, Throwable e) {
		super(message, e);
	}
}
