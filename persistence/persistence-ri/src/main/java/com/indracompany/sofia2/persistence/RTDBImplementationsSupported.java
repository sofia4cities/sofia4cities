/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence;

public enum RTDBImplementationsSupported {
	MONGODB, ORACLE, POSTGRESQL, KUDU;

	public String toString4ControlPanel() {
		switch (this) {
		case KUDU:
			return "relationalKudu";
		case ORACLE:
			return "multibdtr";
		case POSTGRESQL:
			return "multibdtr";
		default:
			return "documentalMongo";
		}
	}

	public String toString() {
		switch (this) {
		case KUDU:
			return "Kudu";
		case ORACLE:
			return "Oracle";
		case POSTGRESQL:
			return "Postgresql";
		default:
			return "MongoDB";
		}
	}
}