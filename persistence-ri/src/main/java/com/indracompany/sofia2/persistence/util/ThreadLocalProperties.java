/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.util;

public class ThreadLocalProperties {
	
	//public static ThreadLocal<Boolean> jsonMode;
	public static ThreadLocal<Boolean> jsonMode = new ThreadLocal<Boolean>() {
	    @Override 
	    protected Boolean initialValue() {
	        return false;
	    }
	}; 
	
//	static {
//		jsonMode = new ThreadLocal<Boolean>();
//		jsonMode.set(false);
//	}
}
