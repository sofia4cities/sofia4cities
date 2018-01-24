/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.api.service.exception;

public class BadRequestException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadRequestException(Exception e){
		super(e);
	}
	
	public BadRequestException(String msg){
		super(msg);
	}
	
	public BadRequestException(String msg, Throwable e){
		super(msg, e);
	}

}
