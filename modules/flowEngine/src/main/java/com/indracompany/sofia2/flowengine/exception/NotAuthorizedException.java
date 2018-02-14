package com.indracompany.sofia2.flowengine.exception;

public class NotAuthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotAuthorizedException() {
		super();
	}

	public NotAuthorizedException(String msg) {
		super(msg);
	}

	public NotAuthorizedException(Exception e) {
		super(e);
	}
}
