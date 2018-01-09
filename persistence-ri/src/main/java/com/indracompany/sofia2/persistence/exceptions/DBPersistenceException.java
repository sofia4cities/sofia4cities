/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.exceptions;

public class DBPersistenceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DBPersistenceException(String message) {
		super(message);
	}

	public DBPersistenceException(String message, Throwable e) {
		super(message, e);
	}

	public DBPersistenceException(Throwable e) {
		super(e);
	}

}