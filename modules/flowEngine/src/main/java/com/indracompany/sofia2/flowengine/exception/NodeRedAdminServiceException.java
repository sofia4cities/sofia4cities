package com.indracompany.sofia2.flowengine.exception;

public class NodeRedAdminServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NodeRedAdminServiceException() {
		super();
	}

	public NodeRedAdminServiceException(String msg) {
		super(msg);
	}

	public NodeRedAdminServiceException(Exception e) {
		super(e);
	}
}
