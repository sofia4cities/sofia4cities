/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.interfaces;

import lombok.Getter;
import lombok.Setter;

public class Table4Ontology {

	public enum AccessMode {
		
		SELECT,
		UPDATE,
		DELETE,
		INSERT,
		CREATE,
		DELETEINDEX,
		GETINDEXES,
		INVALIDATE,
		ALTER
	}
	
	@Getter @Setter private String tableName;
	@Getter @Setter private AccessMode accessMode;
	
	public Table4Ontology() {
		super();
	}

	public Table4Ontology(String tableName, AccessMode accessMode) {
		super();
		this.tableName = tableName;
		this.accessMode = accessMode;
	}

}
