package com.indracompany.sofia2.persistence.interfaces;

import com.indracompany.sofia2.persistence.FormatDelimitedQueryResult;
import com.indracompany.sofia2.ssap.SSAPQueryResultFormat;

public interface BasicOpsQuasarDBRepository {
	
	public static final String ACCEPT_TEXT_CSV="text/csv";
	public static final String ACCEPT_APPLICATION_JSON="application/json";
	
	
//	public FormatDelimitedQueryResult executeQuery(String query, int offset, SSAPQueryResultFormat resultType, UserCDB user, String formatter) throws Exception;
//	public FormatDelimitedQueryResult executeQueryWithoutLimit(String query, int offset, SSAPQueryResultFormat resultType, UserCDB user, String formatter) throws Exception;

}
