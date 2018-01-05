package com.indracompany.sofia2.common.exception;

//TODO: Pasar a commons
public abstract class BaseException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public BaseException(String message) {
		super(message);
	}
}
