package com.indracompany.sofia2.flowengine.exception;

public class NotSynchronizedToCdbException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotSynchronizedToCdbException() {
		super();
	}

	public NotSynchronizedToCdbException(String msg) {
		super(msg);
	}

	public NotSynchronizedToCdbException(Exception e) {
		super(e);
	}
}
