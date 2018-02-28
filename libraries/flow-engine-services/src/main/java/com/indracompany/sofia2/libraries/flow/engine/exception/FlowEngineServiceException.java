package com.indracompany.sofia2.libraries.flow.engine.exception;

public class FlowEngineServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FlowEngineServiceException() {
		super();
	}

	public FlowEngineServiceException(String msg) {
		super(msg);
	}

	public FlowEngineServiceException(Exception e) {
		super(e);
	}

	public FlowEngineServiceException(String msg, Exception e) {
		super(msg, e);
	}

}
