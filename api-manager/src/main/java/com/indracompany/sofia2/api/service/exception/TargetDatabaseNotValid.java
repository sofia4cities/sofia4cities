package com.indracompany.sofia2.api.service.exception;

public class TargetDatabaseNotValid extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TargetDatabaseNotValid(Exception e){
		super(e);
	}
	
	public TargetDatabaseNotValid(String msg){
		super(msg);
	}
	
	public TargetDatabaseNotValid(String msg, Throwable e){
		super(msg, e);
	}

}