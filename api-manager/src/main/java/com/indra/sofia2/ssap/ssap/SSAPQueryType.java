package com.indra.sofia2.ssap.ssap;

public enum SSAPQueryType {
	/*
	 * Tipo de motor de persistencia sobre la que se ralizara la operacion
	 */
	NATIVE, SQLLIKE, SIB_DEFINED, TIME_SERIE, // predefined queries
	CEP, BDH, BDC, SQL;

	public String value() {
		return name();
	}

	public static SSAPQueryType fromValue(String v) {
		return valueOf(v);
	}
}