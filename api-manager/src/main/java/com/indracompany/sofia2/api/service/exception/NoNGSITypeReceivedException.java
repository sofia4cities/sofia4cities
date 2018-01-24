package com.indracompany.sofia2.api.service.exception;

public class NoNGSITypeReceivedException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public NoNGSITypeReceivedException(Exception e){
		super(e);
	}
	
	public NoNGSITypeReceivedException(String msg){
		super(msg);
	}
	
	public NoNGSITypeReceivedException(String msg, Throwable e){
		super(msg, e);
	}

}
