/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.api.service.exception;

public class ForbiddenException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ForbiddenException(Exception e){
		super(e);
	}
	
	public ForbiddenException(String msg){
		super(msg);
	}
	
	public ForbiddenException(String msg, Throwable e){
		super(msg, e);
	}

}
