package com.indracompany.sofia2.flowengine.api.rest.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(String msg) {
		super(msg);
	}

	public ResourceNotFoundException(Exception e) {
		super(e);
	}
}
