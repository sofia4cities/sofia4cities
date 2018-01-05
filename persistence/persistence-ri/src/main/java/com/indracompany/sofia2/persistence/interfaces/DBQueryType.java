/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.interfaces;

public enum DBQueryType {
	
	/**
	 * Query Type
	 */
	RTDB_SQL,
	RTDB_NATIVE, 
	TIME_SERIES, 
	HDB_SQL,
	HDB_NATIVE,
	CONFIGDB_SQL;

	public String value() {
		return name();
	}

	public static DBQueryType fromValue(String v) {
		return valueOf(v);
	}
}