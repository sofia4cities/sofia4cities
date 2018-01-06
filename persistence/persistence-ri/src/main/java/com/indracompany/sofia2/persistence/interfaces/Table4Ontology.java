/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.interfaces;

import com.indracompany.sofia2.persistence.common.AccessMode;

import lombok.Getter;
import lombok.Setter;

public class Table4Ontology {

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
