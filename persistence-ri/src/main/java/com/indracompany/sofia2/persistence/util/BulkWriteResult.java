/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.util;

import lombok.Getter;
import lombok.Setter;

public class BulkWriteResult {
	@Setter @Getter private boolean ok;
	@Setter @Getter private String id;
	@Setter @Getter private String errorMessage;
	
}
