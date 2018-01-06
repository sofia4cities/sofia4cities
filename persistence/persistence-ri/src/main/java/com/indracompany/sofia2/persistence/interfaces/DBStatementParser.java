package com.indracompany.sofia2.persistence.interfaces;

import java.util.List;

import com.indracompany.sofia2.persistence.enums.AccessMode;

public interface DBStatementParser {
	public boolean isValidStatement(String stmt, AccessMode mode);
	public List<String> getCollectionList(String stmt, AccessMode mode);
}
