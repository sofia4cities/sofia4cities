package com.indracompany.sofia2.persistence.exceptions;

public class NotSupportedStatementException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public NotSupportedStatementException(Exception e){
		super(e);
	}
	
	public NotSupportedStatementException(String msg){
		super(msg);
	}
	
	public NotSupportedStatementException(String msg, Throwable e){
		super(msg, e);
	}

}
