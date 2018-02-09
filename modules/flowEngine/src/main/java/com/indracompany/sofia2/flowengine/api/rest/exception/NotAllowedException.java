package com.indracompany.sofia2.flowengine.api.rest.exception;

public class NotAllowedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotAllowedException() {
		super();
	}

	public NotAllowedException(String msg) {
		super(msg);
	}

	public NotAllowedException(Exception e) {
		super(e);
	}
}
