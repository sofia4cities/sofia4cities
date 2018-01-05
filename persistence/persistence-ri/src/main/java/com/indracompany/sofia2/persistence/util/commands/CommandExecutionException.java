/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.util.commands;

public class CommandExecutionException extends Exception {

	private static final long serialVersionUID = 1L;

	public CommandExecutionException(Exception e){
		super(e);
	}
	
	public CommandExecutionException(String message){
		super(message);
	}
}
