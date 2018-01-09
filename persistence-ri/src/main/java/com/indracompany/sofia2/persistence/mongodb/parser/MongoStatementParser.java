package com.indracompany.sofia2.persistence.mongodb.parser;

import java.util.List;

import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.common.AccessMode;
import com.indracompany.sofia2.persistence.common.statement.TableResult;
import com.indracompany.sofia2.persistence.exceptions.NotSupportedStatementException;
import com.indracompany.sofia2.persistence.interfaces.DBStatementParser;
import com.indracompany.sofia2.persistence.mongodb.query.MongoQueryNativeUtil;
import com.indracompany.sofia2.ssap.SSAPQueryType;

@Component
public class MongoStatementParser implements DBStatementParser {

	private final static String INSERT_MONGO_LITERAL="insert";
	private final static String UPDATE_MONGO_LITERAL="update";
	private final static String DELETE_MONGO_LITERAL="remove";
	private final static String SELECT_MONGO_LITERAL="find";
	private final static String AGGREGATE_MONGO_LITERAL="aggregate";
	private final static String COUNT_MONGO_LITERAL="count";
	private final static String DISTINC_MONGO_LITERAL="distinct";
	private final static String ENSUREINDEX_MONGO_LITERAL="ensureindex";
	private final static String DROPINDEX_MONGO_LITERAL="dropindex";
	private final static String GETINDEX_MONGO_LITERAL="getindexes";
	
	@Override
	public boolean isValidStatement(String stmt, AccessMode mode) {
		throw new RuntimeException("Method not implementd");
	}

	@Override
	public List<String> getCollectionList(String stmt, AccessMode mode) {
		throw new RuntimeException("Method not implementd");
	}
	
	public static TableResult getTableNamesFromMongoStatement(String mongoStmt) throws NotSupportedStatementException {
		String operation;
		TableResult result=new TableResult();
		if(mongoStmt.toLowerCase().contains(SELECT_MONGO_LITERAL)){
			operation=SELECT_MONGO_LITERAL;
			result.setAccessMode(AccessMode.SELECT);
			
		}else if(mongoStmt.toLowerCase().contains(INSERT_MONGO_LITERAL)){
			operation=INSERT_MONGO_LITERAL;
			result.setAccessMode(AccessMode.INSERT);
			
		}else if(mongoStmt.toLowerCase().contains(UPDATE_MONGO_LITERAL)){
			operation=UPDATE_MONGO_LITERAL;
			result.setAccessMode(AccessMode.UPDATE);
			
		}else if(mongoStmt.toLowerCase().contains(DELETE_MONGO_LITERAL)){
			operation=DELETE_MONGO_LITERAL;
			result.setAccessMode(AccessMode.DELETE);
			
		}else if(mongoStmt.toLowerCase().contains(AGGREGATE_MONGO_LITERAL)){
			operation=AGGREGATE_MONGO_LITERAL;
			result.setAccessMode(AccessMode.SELECT);
			
		}else if(mongoStmt.toLowerCase().contains(COUNT_MONGO_LITERAL)){
			operation=COUNT_MONGO_LITERAL;
			result.setAccessMode(AccessMode.SELECT);
		}else if(mongoStmt.toLowerCase().contains(DISTINC_MONGO_LITERAL)){
			operation=DISTINC_MONGO_LITERAL;
			result.setAccessMode(AccessMode.SELECT);
			
		}else if(mongoStmt.toLowerCase().contains(ENSUREINDEX_MONGO_LITERAL)){
			operation=ENSUREINDEX_MONGO_LITERAL;
			result.setAccessMode(AccessMode.CREATE);
		}else if(mongoStmt.toLowerCase().contains(DROPINDEX_MONGO_LITERAL)){
			operation=DROPINDEX_MONGO_LITERAL;
			result.setAccessMode(AccessMode.DELETEINDEX);
		}else if(mongoStmt.toLowerCase().contains(GETINDEX_MONGO_LITERAL)){
			operation = GETINDEX_MONGO_LITERAL;
			result.setAccessMode(AccessMode.GETINDEXES);
		}else throw new NotSupportedStatementException("The statement: "+mongoStmt+" can not be processed to get ontologies acceded");
		
		String colName=MongoQueryNativeUtil.getCollNameFromAction(mongoStmt, operation);
		result.setTableName(colName);
		
		return result;
	}

	@Override
	public List<SSAPQueryType> getSSAPQueryTypeSupported() {
		return java.util.Arrays.asList(SSAPQueryType.NATIVE);
	}

}
