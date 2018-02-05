package com.indracompany.sofia2.config.services.exceptions;

public class ConfigServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigServiceException(String message) {
		super(message);
	}

	public ConfigServiceException(Throwable th) {
		super(th);
	}

	public ConfigServiceException(String message, Throwable th) {
		super(message, th);
	}

}
