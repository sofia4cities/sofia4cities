/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.api.service.exception;

public class ApiLimitException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApiLimitException(Exception e){
		super(e);
	}
	
	public ApiLimitException(String msg){
		super(msg);
	}
	
	public ApiLimitException(String msg, Throwable e){
		super(msg, e);
	}

}
