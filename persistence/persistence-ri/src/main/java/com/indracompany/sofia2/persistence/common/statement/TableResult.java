package com.indracompany.sofia2.persistence.common.statement;

import com.indracompany.sofia2.persistence.common.AccessMode;

public class TableResult {
	
	private String tableName;
	private AccessMode accessMode;
	
	public TableResult() {
		super();
	}

	public TableResult(String tableName, AccessMode accessMode) {
		super();
		this.tableName = tableName;
		this.accessMode = accessMode;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public AccessMode getAccessMode() {
		return accessMode;
	}
	
	public void setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
	}
	
	
	

}