/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.indra.sofia2.support.parsersqlnative.Constant;
import com.indra.sofia2.support.parsersqlnative.Delete;
import com.indra.sofia2.support.parsersqlnative.Expression;
import com.indra.sofia2.support.parsersqlnative.FromItem;
import com.indra.sofia2.support.parsersqlnative.Insert;
import com.indra.sofia2.support.parsersqlnative.ParseException;
import com.indra.sofia2.support.parsersqlnative.Query;
import com.indra.sofia2.support.parsersqlnative.SelectItem;
import com.indra.sofia2.support.parsersqlnative.Update;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;




@SuppressWarnings("unchecked")
public class ApiStatementsParser {
	
	private final static String INSERT_SQL_LITERAL="insert";
	private final static String UPDATE_SQL_LITERAL="update";
	private final static String DELETE_SQL_LITERAL="delete";
	private final static String SELECT_SQL_LITERAL="select";
	private final static String ALTER_SQL_LITERAL= "alter";
	private final static String INVALIDATE_SQL_LITERAL="invalidate";
	
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
	
	private final static String EXCEPTION_QUERY_NOT_VALID="Query is not a valid SQL SELECT statement: ";
	private final static String EXCEPTION_QUERY_EMPTY_NULL="Query cannot be empty or null";
	private final static String EXCEPTION_NOT_VALID_OPERATOR="Not valid operator for query";
	private final static String EXCEPTION_PROCESSING_CONDITION="Error processing where condition";
	private final static String EXCEPTION_NOT_FIELD_REQUIRED="The expected field to extract its value is not in the WHERE clause";
	private final static String EXCEPTION_QUERY_WHERE_NULL="Query does not have a valid WHERE clause";
	
	/**
	 * Devuelve una lista con las tablas a las que accede una sentencia
	 * @param sqlStatement
	 * @return
	 * @throws ParseException
	 */
	public static List<TableResult> getTableNamesFromSQLStatement(String sql) throws  Exception {
		
		List<TableResult> tables=new ArrayList<TableResult>();
		if(sql!=null && !sql.trim().equals("")){
			//limpia la query de espacios vacios
			String sqlStatement=sql.trim();
			
			//Determina el tipo de tabla de la que se trata
			if(sqlStatement.toLowerCase().startsWith(INSERT_SQL_LITERAL)){//Se trata de un insert
				TableResult table = getTableFromInsert(sqlStatement);
				tables.add(table);
				
			}else if(sqlStatement.toLowerCase().startsWith(UPDATE_SQL_LITERAL)){
				tables.addAll(getTablesFromUpdate(sqlStatement));
				
			}else if(sqlStatement.toLowerCase().startsWith(DELETE_SQL_LITERAL)){
				tables.addAll(getTablesFromDelete(sqlStatement));
				
			}else if (sqlStatement.toLowerCase().startsWith(ALTER_SQL_LITERAL)){
				tables.addAll(getTablesFromAlter(sqlStatement));
			}else if(sqlStatement.toLowerCase().startsWith(SELECT_SQL_LITERAL)){
				tables.addAll(getTablesFromSelect(sqlStatement));
			
			}else throw new Exception("The statement: "+sql+" can not be processed to get tables acceded");
		}
		
		return tables;
		
	}
	
	/**
	 * Devuelve una lista con las ontologias a las que accede una sentencia
	 * @param mongoStmt
	 * @return
	 * @throws Exception
	 */
	public static TableResult getTableNamesFromMongoStatement(String mongoStmt)  throws Exception{
		String operation;
		TableResult result=new TableResult();
		/*if(mongoStmt.toLowerCase().contains(SELECT_MONGO_LITERAL)){
			operation=SELECT_MONGO_LITERAL;
			result.setAccessMode(TableAccessMode.SELECT);
			
		}else if(mongoStmt.toLowerCase().contains(INSERT_MONGO_LITERAL)){
			operation=INSERT_MONGO_LITERAL;
			result.setAccessMode(TableAccessMode.INSERT);
			
		}else if(mongoStmt.toLowerCase().contains(UPDATE_MONGO_LITERAL)){
			operation=UPDATE_MONGO_LITERAL;
			result.setAccessMode(TableAccessMode.UPDATE);
			
		}else if(mongoStmt.toLowerCase().contains(DELETE_MONGO_LITERAL)){
			operation=DELETE_MONGO_LITERAL;
			result.setAccessMode(TableAccessMode.DELETE);
			
		}else if(mongoStmt.toLowerCase().contains(AGGREGATE_MONGO_LITERAL)){
			operation=AGGREGATE_MONGO_LITERAL;
			result.setAccessMode(TableAccessMode.SELECT);
			
		}else if(mongoStmt.toLowerCase().contains(COUNT_MONGO_LITERAL)){
			operation=COUNT_MONGO_LITERAL;
			result.setAccessMode(TableAccessMode.SELECT);
		}else if(mongoStmt.toLowerCase().contains(DISTINC_MONGO_LITERAL)){
			operation=DISTINC_MONGO_LITERAL;
			result.setAccessMode(TableAccessMode.SELECT);
			
		}else if(mongoStmt.toLowerCase().contains(ENSUREINDEX_MONGO_LITERAL)){
			operation=ENSUREINDEX_MONGO_LITERAL;
			result.setAccessMode(TableAccessMode.CREATE);
		}else if(mongoStmt.toLowerCase().contains(DROPINDEX_MONGO_LITERAL)){
			operation=DROPINDEX_MONGO_LITERAL;
			result.setAccessMode(TableAccessMode.DELETEINDEX);
		}else if(mongoStmt.toLowerCase().contains(GETINDEX_MONGO_LITERAL)){
			operation = GETINDEX_MONGO_LITERAL;
			result.setAccessMode(TableAccessMode.GETINDEXES);
		}else throw new Exception("The statement: "+mongoStmt+" can not be processed to get ontologies acceded");
		
		String colName=MongoQueryNativeUtil.getCollNameFromAction(mongoStmt, operation);
		result.setTableName(colName);*/
		
		return result;
	}
	
	
	public static List<TableResult> getTableNamesFromNativeStatement(String statement, PersistenceImplementationType persistenceType)  throws Exception, ParseException{
		if(persistenceType==PersistenceImplementationType.MONGODB){
			ArrayList<TableResult> lResult=new ArrayList<TableResult>();
			lResult.add(ApiStatementsParser.getTableNamesFromMongoStatement(statement));
			return lResult;
		}else if(persistenceType==PersistenceImplementationType.ORACLE || persistenceType==PersistenceImplementationType.KUDU){
			return ApiStatementsParser.getTableNamesFromSQLStatement(statement);
		}else{
			throw new Exception("Not Supported Persistence type");
		}
	}
	
	
	/**
	 * Devuelve el tipo de acceso para una operación
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static TableAccessMode getOperationTypeFromSQLStatement(String sql) throws Exception{
		
		if(sql!=null && !sql.trim().equals("")){
			//limpia la query de espacios vacios
			String sqlStatement=sql.trim().toLowerCase();
			
			//Determina el tipo de tabla de la que se trata
			if(sqlStatement.startsWith(INSERT_SQL_LITERAL)){//Se trata de un insert
				return TableAccessMode.INSERT;
				
			}else if(sqlStatement.startsWith(UPDATE_SQL_LITERAL)){
				return TableAccessMode.UPDATE;
				
			}else if(sqlStatement.startsWith(DELETE_SQL_LITERAL)){
				return TableAccessMode.DELETE;
				
			}else if(sqlStatement.startsWith(SELECT_SQL_LITERAL)){
				return TableAccessMode.SELECT;
				
			}else if (sqlStatement.startsWith(INVALIDATE_SQL_LITERAL)){
				return TableAccessMode.INVALIDATE;
			} else if (sqlStatement.startsWith(ALTER_SQL_LITERAL)) {
				return TableAccessMode.ALTER;
			}else throw new Exception("The statement: "+sql+" can not be processed to get tables accesed");

		}else throw new Exception("Please insert a supported sql INSERT, UPDATE, DELETE, SELECT statement");
	}
	
	
	/**
	 *  Devuelve el tipo de acceso para una operación
	 * @param mongoStmt
	 * @return
	 * @throws Exception
	 */
	public static TableAccessMode getOperationTypeFromMongoStatement(String mongoStmt)  throws Exception{
		if(mongoStmt.toLowerCase().contains(SELECT_MONGO_LITERAL)){
			return TableAccessMode.SELECT;
			
		}else if(mongoStmt.toLowerCase().contains(INSERT_MONGO_LITERAL)){
			return TableAccessMode.INSERT;
			
		}else if(mongoStmt.toLowerCase().contains(UPDATE_MONGO_LITERAL)){
			return TableAccessMode.UPDATE;
			
		}else if(mongoStmt.toLowerCase().contains(DELETE_MONGO_LITERAL)){
			return TableAccessMode.DELETE;
			
		}else throw new Exception("The statement: "+mongoStmt+" can not be processed to get ontologies acceded");
	}
	
	
	
	/**
	 * Convierte el tipo de acceso al correspondiente tipo de operación de SSAP
	 * @param mode
	 * @return
	 */
	public static SSAPMessageTypes fromTableAccessModeToSSAPMessageType(TableAccessMode mode){
		switch(mode){
			case SELECT: return SSAPMessageTypes.QUERY;
			case INSERT: return SSAPMessageTypes.INSERT;
			case UPDATE: return SSAPMessageTypes.UPDATE;
			case DELETE: return SSAPMessageTypes.DELETE;
			case CREATE: return SSAPMessageTypes.CREATE;
			case DELETEINDEX: return SSAPMessageTypes.DELETEINDEX;
			case ALTER: return SSAPMessageTypes.DELETE;
			case GETINDEXES: return SSAPMessageTypes.GETINDEXES;
			default: return null;
		}
	}
	
	
	//Correc: Renombrada variable local
	private static TableResult getTableFromInsert(String sqlStatement) throws ParseException{
		String sqlStatementAux = sqlStatement;
		if(!sqlStatementAux.trim().endsWith(";")){
			sqlStatementAux=sqlStatementAux.trim()+";";
		}
		
		//Convierte la sentencia sql en un objeto de tipo Statement
		Parser sSql = new Parser(sqlStatementAux);
		Insert insertStatement = (Insert) sSql.processStatement();
		
		return new TableResult(insertStatement.getTable(), TableAccessMode.INSERT);
		
	}
	
	//Correc: Renombrada variable local
	private static List<TableResult> getTablesFromUpdate(String sqlStatement) throws ParseException{
		String sqlStatementAux = sqlStatement;
		if(!sqlStatementAux.trim().endsWith(";")){
			sqlStatementAux=sqlStatementAux.trim()+";";
		}
		
		//Convierte la sentencia sql en un objeto de tipo Statement
		Parser sSql = new Parser (sqlStatementAux);
		Update updateStatement = (Update) sSql.processStatement();
		
		List<TableResult> tables=new ArrayList<TableResult>();
		tables.add(new TableResult(updateStatement.getTable(), TableAccessMode.UPDATE));
		
		
		//Extrae recursivamente todas las tablas que haya en la clausula WHERE
		Expression whereSql=(Expression) updateStatement.getWhere();
		
		if(whereSql!=null){
			tables.addAll(getTablesFromExpression(whereSql));
		}
		
		return tables;
	}
	
	//Correc: Renombrada variable local
	private static List<TableResult> getTablesFromDelete(String sqlStatement) throws ParseException{
		String sqlStatementAux = sqlStatement;
		if(!sqlStatementAux.trim().endsWith(";")){
			sqlStatementAux=sqlStatementAux.trim()+";";
		}
		
		//Convierte la sentencia sql en un objeto de tipo Statement
		Parser sSql = new Parser (sqlStatementAux);
		Delete deleteStatement = (Delete) sSql.processStatement();
		
		List<TableResult> tables=new ArrayList<TableResult>();
		tables.add(new TableResult(deleteStatement.getTable(), TableAccessMode.DELETE));
		
		
		Expression whereSql=(Expression) deleteStatement.getWhere();
		
		if(whereSql!=null){
			tables.addAll(getTablesFromExpression(whereSql));
		}
		
		return tables;
	}
	
	//Correc: Renombrada variable local
	private static List<TableResult> getTablesFromSelect(String sqlStatement) throws ParseException{
		String sqlStatementAux = sqlStatement;
		if(!sqlStatementAux.trim().endsWith(";")){
			sqlStatementAux=sqlStatementAux.trim()+";";
		}
		
		List<TableResult> tables=new ArrayList<TableResult>();
		//Convierte la sentencia sql en un objeto de tipo Statement
		Parser sSql = new Parser (sqlStatementAux);
		Query selectStatement = (Query) sSql.processStatement();
		
		List<FromItem> fromTables = selectStatement.getFrom();
		
		for(FromItem table:fromTables){
			tables.add(new TableResult(table.getTable(), TableAccessMode.SELECT));
		}
		
		//Extrae recursivamente todas las tablas que haya en la clausula WHERE
		Expression whereSql=(Expression) selectStatement.getWhere();
		
		if(whereSql!=null){
			tables.addAll(getTablesFromExpression(whereSql));
		}
		
		return tables;
	}
	
	private static List<TableResult> getTablesFromAlter(String sqlStatement) throws ParseException{
		String sqlStatementAux = sqlStatement;
		if(!sqlStatementAux.trim().endsWith(";")){
			sqlStatementAux=sqlStatementAux.trim()+";";
		}
		
		List<TableResult> tables=new ArrayList<TableResult>();
		
		sqlStatementAux = sqlStatementAux.replace("alter table","");
		sqlStatementAux = sqlStatementAux.substring(0,sqlStatementAux.indexOf("drop"));
		sqlStatementAux = sqlStatementAux.trim();
		
		tables.add(new TableResult(sqlStatementAux, TableAccessMode.ALTER));
		
		return tables;
	}
	
	
	
	private static List<TableResult> getTablesFromExpression(Expression exp) throws ParseException{
		List<TableResult> tables=new ArrayList<TableResult>();
		
		if(exp!=null){
			 for(Object obj:exp.getOperands()){
				 if(obj instanceof Query){
					 tables.addAll(getTablesFromSelect(((Query)obj).toString()));
				 }else if(obj instanceof Expression){
					 tables.addAll(getTablesFromExpression((Expression)obj));
				 }
				 
			 }
		 }
		return tables;
	}

	
	//Correc: Renombrada variable local
	public static List<String> getColumnsFromSelect(String sqlStatement) throws ParseException{
		String sqlStatementAux = sqlStatement;
		if(!sqlStatementAux.trim().endsWith(";")){
			sqlStatementAux=sqlStatementAux.trim()+";";
		}
		
		List<String> columns=new ArrayList<String>();
		//Convierte la sentencia sql en un objeto de tipo Statement
		Parser sSql = new Parser (sqlStatementAux);
		Query selectStatement = (Query) sSql.processStatement();
		
		Vector<SelectItem> selectItems=selectStatement.getSelect();
		
		
		for(SelectItem item:selectItems){
			//columns.add(item.getColumn().toLowerCase());
			if(item.getAlias()==null){
				columns.add(item.toString().toLowerCase());
			}else{
				columns.add(item.toString().toLowerCase().replace(item.getAlias().toLowerCase(), "").trim());
			}
		}
		
		
		//Extrae recursivamente todas las columnas que haya en la clausula WHERE
		Expression whereSql=(Expression) selectStatement.getWhere();
		
		if(whereSql!=null){
			columns.addAll(getColumnsFromExpression(whereSql));
		}
		
		return columns;
	}
	
	
	private static List<String> getColumnsFromExpression(Expression exp) throws ParseException{
		List<String> columns=new ArrayList<String>();
		if(exp!=null){
			
			 for(Object obj:exp.getOperands()){
				 if(obj instanceof Query){
					 columns.addAll(getColumnsFromSelect(((Query)obj).toString()));
				 }else if(obj instanceof Expression){
					 columns.addAll(getColumnsFromExpression((Expression)obj));
				 }else if(obj instanceof Constant && ((Constant)obj).getType()==Constant.COLUMNNAME){
					columns.add(((Constant)obj).getValue().toLowerCase());
				 }
			 }
		 }
		return columns;
	}
	
	//Correc: Renombrada variable local
	public static List<String> getTablesAndAliasFromSelect(String sqlStatement) throws ParseException{
		String sqlStatementAux =sqlStatement;
		if(!sqlStatementAux.trim().endsWith(";")){
			sqlStatementAux=sqlStatementAux.trim()+";";
		}
		
		List<String> tables=new ArrayList<String>();
		//Convierte la sentencia sql en un objeto de tipo Statement
		Parser sSql = new Parser (sqlStatementAux);
		Query selectStatement = (Query) sSql.processStatement();
		
		List<FromItem> fromTables = selectStatement.getFrom();
		
		for(FromItem table:fromTables){
			tables.add(table.getTable().toLowerCase());
			if(table.getAlias()!=null){
				tables.add(table.getAlias().toLowerCase());
			}
		}
		
		//Extrae recursivamente todas las tablas que haya en la clausula WHERE
		Expression whereSql=(Expression) selectStatement.getWhere();
		
		if(whereSql!=null){
			tables.addAll(getTablesAndAliasFromExpression(whereSql));
		}
		
		return tables;
	}
	
	private static List<String> getTablesAndAliasFromExpression(Expression exp) throws ParseException{
		List<String> tables=new ArrayList<String>();
		
		if(exp!=null){
			 for(Object obj:exp.getOperands()){
				 if(obj instanceof Query){
					 tables.addAll(getTablesAndAliasFromSelect(((Query)obj).toString()));
				 }else if(obj instanceof Expression){
					 tables.addAll(getTablesAndAliasFromExpression((Expression)obj));
				 }
				 
			 }
		 }
		return tables;
	}
	
	//Correc: Renombrada variable local
	public static List<String> getColumnsFromInsert(String sqlStatement) throws ParseException{
		String sqlStatementAux = sqlStatement;
		if(!sqlStatementAux.trim().endsWith(";")){
			sqlStatementAux=sqlStatementAux.trim()+";";
		}
		
		//Convierte la sentencia sql en un objeto de tipo Statement
		Parser sSql = new Parser(sqlStatementAux);
		Insert insertStatement = (Insert) sSql.processStatement();
		
		return insertStatement.getColumns();
	}
	
	
	@SuppressWarnings("rawtypes")
	public static String getSimpleFieldWhereValue(String sql, String field) throws ParseException{
		if(sql!=null && !sql.trim().equals("")){
			//limpia la query de espacios vacios
			String sqlStatement=sql.trim();
			if(!sqlStatement.endsWith(";")){
				sqlStatement+=";";
			}
			
			//Determina el tipo de tabla de la que se trata
			if(sqlStatement.toLowerCase().startsWith(SELECT_SQL_LITERAL)){//Se trata de un SELECT
				Parser sSql = new Parser(sqlStatement);
				Query selectStatement = (Query) sSql.processStatement();
				Expression whereSql=(Expression)selectStatement.getWhere();
				if(whereSql!=null){
					if(whereSql.getOperator().equals("=")){
						try{
							Vector vOperands=whereSql.getOperands();
							if(((Constant)vOperands.get(0)).getValue().equalsIgnoreCase(field)){
								return ((Constant)vOperands.get(1)).getValue();
							}else{
								throw new ParseException(EXCEPTION_NOT_FIELD_REQUIRED);
							}
						}catch(ParseException e){
							throw e;
						}
						catch(Exception e){
							throw new ParseException(EXCEPTION_PROCESSING_CONDITION);
						}
					}else{
						throw new ParseException(EXCEPTION_NOT_VALID_OPERATOR);
					}
				}else{
					throw new ParseException(EXCEPTION_QUERY_WHERE_NULL);
				}
			}else{
				throw new ParseException(EXCEPTION_QUERY_NOT_VALID+sql);
			}
		}else{
			throw new ParseException(EXCEPTION_QUERY_EMPTY_NULL);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isSelectAllFields(String sql) throws ParseException {
		if(sql!=null && !sql.trim().equals("")){
			//limpia la query de espacios vacios
			String sqlStatement=sql.trim();
			if(!sqlStatement.endsWith(";")){
				sqlStatement+=";";
			}
			
			//Determina el tipo de tabla de la que se trata
			if(sqlStatement.toLowerCase().startsWith(SELECT_SQL_LITERAL)){//Se trata de un SELECT
				Parser sSql = new Parser(sqlStatement);
				Query selectStatement = (Query) sSql.processStatement();
				Vector vSelect=selectStatement.getSelect();
				if(vSelect.size()==1){
					SelectItem item=(SelectItem)vSelect.get(0);
					if(item.getColumn().equals("*")){
						return true;
					}else return false;
					
				}else return false;
				
			}else{
				throw new ParseException(EXCEPTION_QUERY_NOT_VALID+sql);
			}
		}else{
			throw new ParseException(EXCEPTION_QUERY_EMPTY_NULL);
		}	
	}
	
	
	public static boolean notWhereClauseStatement(String sql) throws ParseException  {
		if(sql!=null && !sql.trim().equals("")){
			//limpia la query de espacios vacios
			String sqlStatement=sql.trim();
			if(!sqlStatement.endsWith(";")){
				sqlStatement+=";";
			}
			
			//Determina el tipo de tabla de la que se trata
			if(sqlStatement.toLowerCase().startsWith(SELECT_SQL_LITERAL)){//Se trata de un SELECT
				Parser sSql = new Parser(sqlStatement);
				Query selectStatement = (Query) sSql.processStatement();
				Expression whereSql=(Expression)selectStatement.getWhere();
				if(null==whereSql){
					return true;
				}else{
					return false;
				}
			}else{
				throw new ParseException(EXCEPTION_QUERY_NOT_VALID+sql);
			}
		}else{
			throw new ParseException(EXCEPTION_QUERY_EMPTY_NULL);
		}
	}

}
