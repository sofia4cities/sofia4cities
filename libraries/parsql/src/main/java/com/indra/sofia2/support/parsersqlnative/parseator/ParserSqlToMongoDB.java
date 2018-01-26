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
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.parsersqlnative.parseator;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.jfree.util.Log;

import com.indra.sofia2.support.parsersqlnative.Constant;
import com.indra.sofia2.support.parsersqlnative.Delete;
import com.indra.sofia2.support.parsersqlnative.Exp;
import com.indra.sofia2.support.parsersqlnative.Expression;
import com.indra.sofia2.support.parsersqlnative.FromItem;
import com.indra.sofia2.support.parsersqlnative.GroupBy;
import com.indra.sofia2.support.parsersqlnative.Insert;
import com.indra.sofia2.support.parsersqlnative.ParseException;
import com.indra.sofia2.support.parsersqlnative.Query;
import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.Update;
import com.indra.sofia2.support.parsersqlnative.util.QueryType;

@SuppressWarnings("rawtypes")
public class ParserSqlToMongoDB implements ParserSql{

	private static final String DB_MONGODB ="db.";
	private static final String DB_MONGODB_FIND = "find";
	private static final String DB_MONGODB_LIMIT = "limit";
	private static final String DB_MONGODB_SKIP = "skip";
	private static final String DB_MONGODB_ORDERBY = "sort";
	private static final String DB_MONGODB_COUNT = "count";
	private static final String DB_MONGODB_DISTINCT = "distinct";
	private static final String DB_MONGODB_GROUPBY = "aggregate";
	private static final String DB_MONGODB_INSERT = "insert";
	private static final String DB_MONGODB_UPDATE = "update";
	private static final String DB_MONGODB_DELETE = "remove";

	private Vector selectSql;
	private Vector fromSql;
	private Exp whereSql;
	private GroupBy groupBySql;
	private Vector orderBySql;
	private int limitSql;
	private int skipSql;
	private boolean distinct = false;
	private boolean order = false;
	private boolean specialSelect = false;

	/**
	 * Method which transform sql statement into mongodb statement
	 * @throws ParseException, SQLException
	 */
	public String postProcess(Statement statement) throws SQLException, ParseException {

		String queryMongoDB="";

		if(statement instanceof Query) { 
			queryMongoDB = transformQuery((Query)statement);

		} else if(statement instanceof Insert) { 
			queryMongoDB = transformInsert((Insert)statement);

		} else if(statement instanceof Update) {
			queryMongoDB = transformUpdate((Update)statement);

		} else if(statement instanceof Delete) {
			queryMongoDB = transformDelete((Delete)statement);
		}

		queryMongoDB = queryMongoDB.replace("\"{", "{");
		queryMongoDB = queryMongoDB.replace("}\"", "}");

		queryMongoDB = queryMongoDB.replace("'ISODate", "ISODate");
		queryMongoDB = queryMongoDB.replace(")'", ")");
		return queryMongoDB;
	}

	private String transformQuery(Query statement) throws SQLException, ParseException {

		String queryMongoDB = DB_MONGODB;

		selectSql = statement.getSelect(); // SELECT part of the query
		fromSql = statement.getFrom();  // FROM part of the query
		whereSql = statement.getWhere(); //Where
		groupBySql = statement.getGroupBy(); //groupBy
		orderBySql = statement.getOrderBy(); // orderBy
		limitSql = statement.getLimit(); //limit
		skipSql = statement.getSkip(); //skip
		distinct = statement.isDistinct(); // select distinct
		specialSelect = isSpecialSelect();

		if(fromSql.size() > 1) {
			throw new SQLException("Joins are not supported");
		}

		FromItem ontology = (FromItem)fromSql.elementAt(0); //collection name
		queryMongoDB = queryMongoDB.concat(ontology.getTable()).concat("."); // db.[collection].

		if(selectSql != null){ 
			if( !selectSql.get(0).toString().toLowerCase().contains(DB_MONGODB_COUNT) && !distinct && groupBySql == null){
				//select a,b,c or select a, sum(pop) totalpop
				boolean special = false;
				for(int i=0; i<selectSql.size(); i++){
					if( isSpecial(selectSql.get(i).toString())){
						special = true;
						break;
					}
				}
				if(special){
					queryMongoDB = queryMongoDB.concat(DB_MONGODB_GROUPBY).concat("([{$group:{");

					String selectVal="";
					String selectOper="";

					for(int i=0; i< selectSql.size(); i++){

						if( ! isSpecial(selectSql.get(i).toString())){
							selectVal = selectVal.concat("\""+ selectSql.get(i).toString()).concat("\":\"$").concat(selectSql.get(i).toString()).concat("\",");

						}else{	//total:{$sum:"$price"}
							selectOper = transformOper(selectSql.get(i).toString(),QueryType.SELECT).concat(",");
						}
					}
					if ( selectVal.length() > 1 ){
						selectVal = selectVal.substring(0, selectVal.lastIndexOf(","));
						queryMongoDB = queryMongoDB.concat("_id:{").concat(selectVal).concat("}");
					}else {
						selectVal = " _id: null";
						queryMongoDB = queryMongoDB.concat(selectVal);
					}

					if( selectOper.length() > 1 ){
						selectOper = selectOper.substring(0, selectOper.lastIndexOf(","));
						if(selectVal.length()>1){
							queryMongoDB = queryMongoDB.concat(",");
						}
						queryMongoDB = queryMongoDB.concat(selectOper);
					}				
					queryMongoDB = queryMongoDB.concat("}}");

					queryMongoDB = queryMongoDB.concat("])");
				}
				else{
					queryMongoDB = queryMongoDB.concat(DB_MONGODB_FIND).concat("("). //db.[collection].find(
							concat(transformSelect()).concat(")");
				}
			}else if (selectSql.get(0).toString().toLowerCase().contains(DB_MONGODB_COUNT)){ // select count

				String [] elements = selectSql.get(0).toString().split(" ");
				
				if(selectSql.get(0).toString().toLowerCase().contains(DB_MONGODB_DISTINCT)){ // select count distinct
					// if select count distinct XXX from
				
					// "Select count(distinct campo)"  o  "Select count(distinct campo) as namecampoas"
					
					//Entonces String [] elements :
				    // 	[0]--> count(distinct
				    // 	[1]--> campo con o sin )
				    // 	[2]--> namecampoas de as
					
					String campo = elements[1].replace(")","").trim();
					
					if(elements.length == 2){
						queryMongoDB = queryMongoDB.concat(DB_MONGODB_GROUPBY).concat("([{$match:{\"").concat(campo).concat("\":{$exists:true}}},{$group:{\"_id\":\"$").concat(campo).concat("\"}},{ $group:{\"_id\":\"").concat(campo).concat("\",count: { $sum: 1 }}}])");
					}
					
					if(elements.length == 3){
						String namecampoas = elements[2].trim();
						queryMongoDB = queryMongoDB.concat(DB_MONGODB_GROUPBY).concat("([{$match:{\"").concat(campo).concat("\":{$exists:true}}},{$group:{\"_id\":\"$").concat(campo).concat("\"}},{ $group:{\"_id\":\"").concat(namecampoas).concat("\",count: { $sum: 1 }}}])");
					}
					
				}else{

				if(elements.length == 1){ 
					//if select count (*) from
					queryMongoDB = queryMongoDB.concat(DB_MONGODB_COUNT).concat("(")
							.concat(transformCount(QueryType.SELECT)).concat(")");
				}else{
					//if select count (*) as x from
						String campo = elements[0].replace("count(", "").replace(")","").trim(); //count(campo)
						String namecampoas=elements[1].trim();
						queryMongoDB = queryMongoDB.concat(DB_MONGODB_GROUPBY).concat("([{$match:{\"").concat(campo).concat("\":{$exists:true}}},{$group:{\"_id\":null,").concat(namecampoas).concat(":{$sum:1}}}])");
					}
				}



			}else if (distinct){ //select distinct
				queryMongoDB = queryMongoDB.concat(DB_MONGODB_DISTINCT).concat("(").concat("'").concat(selectSql.get(0).toString()).
						concat("'");
				if(whereSql != null){
					queryMongoDB = queryMongoDB.concat(",").concat("{").concat(transformWhere(QueryType.SELECT)).concat("}");
				}
				queryMongoDB = queryMongoDB.concat(")");

			}else if(groupBySql != null){ //select ... where...group by..

				queryMongoDB = queryMongoDB.concat(DB_MONGODB_GROUPBY).concat("([");

				if(groupBySql.getHaving() != null){

					queryMongoDB = queryMongoDB.concat("{").concat("$match").concat(":").concat("{")
							.concat(transformHaving(groupBySql.getHaving(), QueryType.SELECT)).concat("}").concat("}").concat(",").
							concat("{").concat("$group:{").concat("\"_id\"").concat(":"). 
							concat(transformGroup(groupBySql.getGroupBy()));

					if(isSpecialSelectCount()){ //count =>  { $group: { _id: "$cust_id", count: { $sum: 1 } } }
						queryMongoDB = queryMongoDB.concat(",count:{$sum:1}");
					}				
					queryMongoDB = queryMongoDB.concat("}}");

				}

				if(selectSql!=null){

					if(groupBySql.getHaving() != null && !isSpecialSelectCount()){
						queryMongoDB = queryMongoDB.concat(",");
					}

					if(specialSelect){
						queryMongoDB = queryMongoDB.concat("{$group:{");

						String selectVal="";
						String selectOper="";
						boolean nocontenido = false;
						String groupby = groupBySql.getGroupBy().toString();
						groupby = groupby.replace("[", "");
						groupby = groupby.replace("]", "");
						for(int i=0; i< selectSql.size(); i++){
							if( ! isSpecial(selectSql.get(i).toString())){
								if(selectSql.get(i).toString().contains(".")){
									String aux = selectSql.get(i).toString();
									if(aux.contains(groupby)){
										selectVal = selectVal.concat("\"" + aux.substring(0,aux.indexOf("."))).concat("\":\"$").concat(selectSql.get(i).toString()).concat("\",");
									}else{
										nocontenido = true;
									}
								}else{
									selectVal = selectVal.concat("\"" + selectSql.get(i).toString()).concat("\":\"$").concat(selectSql.get(i).toString()).concat("\",");
								}

							}else{  // total:{$sum:"$price"}
								selectOper = transformOper(selectSql.get(i).toString(), QueryType.SELECT).concat(",");
							}
						}

						if ( selectVal.length() > 1 ){
							selectVal = selectVal.substring(0, selectVal.lastIndexOf(","));
							queryMongoDB = queryMongoDB.concat("_id:{").concat(selectVal);
							if(nocontenido){
								if(groupby.contains(".")){
									queryMongoDB = queryMongoDB.concat(",\"" + groupby.substring(0,groupby.indexOf("."))).concat("\":\"$").concat(groupby).concat("\"");
								}else{
									queryMongoDB = queryMongoDB.concat(",\"" + groupby).concat("\":\"$").concat(groupby).concat("\"");
								}
							}
							queryMongoDB = queryMongoDB.concat("}");
						}else {
							if(groupBySql.getGroupBy().size() >0){
								selectVal = "_id:\"$".concat(groupby).concat("\"");
								queryMongoDB = queryMongoDB.concat(selectVal);
							}else{
								queryMongoDB = queryMongoDB.concat("_id:null");
							}

						}

						if( selectOper.length() > 1 ){
							selectOper = selectOper.substring(0, selectOper.lastIndexOf(","));
							if(selectVal.length()>1){
								queryMongoDB = queryMongoDB.concat(",");
							}
							queryMongoDB = queryMongoDB.concat(selectOper);
						}				
						queryMongoDB = queryMongoDB.concat("}}");
					}
				}
				if( orderBySql != null ){
					order = true;
					queryMongoDB = queryMongoDB.concat(",{").concat(transformSort()).concat("}");
				}
				if( whereSql != null){
					queryMongoDB = queryMongoDB.concat(",{").concat(transformWhere(QueryType.SELECT)).concat("}");
				}
				queryMongoDB = queryMongoDB.concat("])");

			}else{
				throw new SQLException ("ERROR - Expect select [EXPRESSION] or select count or select distinct");
			}
		}
		//order by
		if (orderBySql != null && !order){
			String [] order = orderBySql.get(0).toString().split(" ");
			if(order[1].contains("DESC")){
				queryMongoDB = queryMongoDB.concat(".").concat(DB_MONGODB_ORDERBY).concat("(").concat("{").concat("\""+order[0]+"\"").concat(":-1").concat("}").concat(")");
			}else if (order[1].contains("ASC")){
				queryMongoDB = queryMongoDB.concat(".").concat(DB_MONGODB_ORDERBY).concat("(").concat("{").concat("\""+order[0]+"\"").concat(":1").concat("}").concat(")");
			}else{
				throw new SQLException ("ERROR - Expect ORDER BY DESC or ORDER BY ASC");
			}
		}

		//limit 
		queryMongoDB = queryMongoDB.concat(transformLimit());

		//skip
		queryMongoDB = queryMongoDB.concat(transformSkip());
		queryMongoDB = queryMongoDB.concat(";"); //finish statement Sql To MongoDB

		return queryMongoDB;
	}

	private boolean isSpecialSelect(){
		//add select elements
		boolean special = false;
		for(int i=0; i<selectSql.size(); i++){
			if( isSpecial(selectSql.get(i).toString())){
				special = true;
				break;
			}
		}
		return special;
	}

	private boolean isSpecialSelectCount(){
		return selectSql.toString().contains("COUNT(");
	}

	private String transformSort() throws ParseException{
		String statementMongo = "";
		if(orderBySql != null){
			String [] order = orderBySql.get(0).toString().split(" ");
			statementMongo = statementMongo.concat("$sort:{");
			if(order[1].contains("DESC")){
				statementMongo = statementMongo.concat("\""+order[0]+"\"").concat(":-1").concat("}");
				return statementMongo;
			}else if (order[1].contains("ASC")){
				statementMongo = statementMongo.concat("\""+order[0]+"\"").concat(":1").concat("}");
				return statementMongo;
			}else{
				throw new ParseException ("ERROR - Expect ORDER BY DESC or ORDER BY ASC");
			}
		}else{
			throw new ParseException ("ERROR - Expect ORDER BY DESC or ORDER BY ASC");
		}

	}

	private String transformSelect ()throws ParseException{

		String statementMongo="";

		if (selectSql != null){
			String elementsSelect =",";
			boolean pass = false;

			if(selectSql.get(0).toString().equals("*")){ // select * from [X]
				if(whereSql != null) {
					statementMongo = statementMongo.concat("{").concat(transformWhere(QueryType.SELECT)).concat("}");
				} else {
					statementMongo = statementMongo.concat(transformWhere(QueryType.SELECT));
				}
			}
			else{ 	
				statementMongo = statementMongo.concat("{");
				for(int i =0; i<selectSql.size(); i++){ //recolect select elements
					elementsSelect = elementsSelect +  selectSql.get(i) + ",";
				}

				statementMongo = statementMongo.concat(transformWhere(QueryType.SELECT)).concat("},{");

				boolean containId = false;	
				for(int i=0; i < selectSql.size(); i++){ //recorremos los elementos del select 
					// si no lleva el ID => lo ponemos a 0
					if( (!elementsSelect.contains(",id,") && !elementsSelect.contains(",_id,")) && !containId){
						containId = true;
						statementMongo = statementMongo.concat("_id").concat(":0,");	
					}
					if( (!selectSql.get(i).toString().equals("_id")) && (!selectSql.get(i).toString().equals("id"))){
						statementMongo = statementMongo.concat("\""+  selectSql.get(i).toString().concat("\":1"));
						pass = true;
					}

					if( i+1 < selectSql.size() && pass ){
						statementMongo = statementMongo.concat(",");
						pass = false;
					}
				}
				statementMongo = statementMongo.concat("}");
			}
		}else{
			throw new ParseException ("ERROR transform select query is null ");
		}

		return statementMongo;
	}

	private String transformCount(QueryType select) throws ParseException{
		String statementCountMongo= "";
		//db.[collection].count(
		if(selectSql != null){

			if(selectSql.get(0).toString().contains("*") && whereSql == null){ // select count (*) from [X]  => db.users.count()

				return statementCountMongo;
			}
			else if(!selectSql.get(0).toString().contains("*") && whereSql == null){ //{ user_id: { $exists: true } }
				if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
					String elementCount = selectSql.get(0).toString();
					elementCount = elementCount.substring(elementCount.indexOf("(") + 1, elementCount.indexOf(")"));
					statementCountMongo = statementCountMongo.concat("{").concat("\"" + elementCount).concat("\":").
							concat("{").concat("$exists:true").concat("}").concat("}");
				}else{
					String elementCount = selectSql.get(0).toString();
					elementCount = elementCount.substring(elementCount.indexOf("(") + 1, elementCount.indexOf(")"));
					statementCountMongo = statementCountMongo.concat("{").concat(elementCount).concat(":").
							concat("{").concat("$exists:true").concat("}").concat("}");
				}

			}else{  
				statementCountMongo = statementCountMongo.concat("{").concat(transformWhere(select)).concat("}");
			}
		}
		return statementCountMongo;
	}

	private String transformWhere(QueryType select) throws ParseException{

		String statementWhereMongo = "";

		if(whereSql != null){

			Vector operands = ((Expression) whereSql).getOperands();
			String operator = ((Expression) whereSql).getOperator();
			String signe = getOperatorMongoDB(operator);

			if(operands != null){

				if (signe.equals("$near")){
					Exp op1 = (Exp) operands.get(0);
					Exp op2 = (Exp) operands.get(1);
					Exp op3 = (Exp) operands.get(2);
					if(!(op1 instanceof Constant)  || !(op2 instanceof Expression) ){
						throw new ParseException ("ERROR - Expected: Expected: where + column name + S_near + (ST_Point(longitude, latitude), max_distance);");
					}else{
						String distance = op1.toString();
						Expression point = (Expression) op2;
						if(distance.trim().length() == 0 ){
							throw new ParseException ("ERROR - Wrong distance. Expected: where + column name + S_near + (ST_Point(longitude, latitude), max_distance);");
						}

						if(! "ST_POINT".equalsIgnoreCase(point.getOperator())){
							throw new ParseException ("ERROR - Expected: where + column name + S_near + (ST_Point(longitude, latitude), max_distance);");
						} else{
							String type = "\"Point\"";
							String latitude = point.getOperands().get(0).toString();
							String longitude=(String) point.getOperands().get(1).toString();

							if(longitude.trim().length() == 0 || latitude.trim().length() == 0){
								throw new ParseException ("ERROR - Wrong coordinates. Expected: where + column name + S_near + (ST_Point(longitude, latitude), max_distance);");
							}

							String column = op3.toString().trim();

							statementWhereMongo = statementWhereMongo.concat("\""+ column + "\":").concat("{"+signe + ":")
									.concat("{$geometry: {type: "+ type +", coordinates: ["+longitude + "," + latitude+"]},").concat("$maxDistance: ").concat(distance)
									.concat("}")
									.concat("}");
						}

					}
				}else if(operator.toUpperCase().equals("IN")){
				        String result;
				        operator = "$in";
				        Exp op1 = (Exp) operands.get(0);
					Exp op2 = (Exp) operands.get(1);
				        if(operands.size() >=3){
				            result = "\"" + op1.toString().concat("\" :").concat("{").concat(operator).concat(":").concat("[");
				            // el primer elemento es siempre la columna
				            for (int i = 1; i < operands.size(); i++) {
						result = result.concat(operands.get(i).toString()).concat(",");
					    }
				            result = result.substring(0, result.length()-1);
				            result = result.concat("]}");
				    	
				        }else{
				            result = "\"" + op1.toString().concat("\" :").concat("{").concat(operator).concat(":").
					    		concat(op2.toString()).concat("}");
				        }
				        if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
				            statementWhereMongo = result;
				        }else{
				            statementWhereMongo = op1.toString().concat(":").concat("{").concat(operator).concat(":").
	        			    			concat(op2.toString()).concat("}");
				        }
				
				}else{
					for(int i=0; i< operands.size(); i++){
						Exp op1 = (Exp) operands.get(i);
						signe = getOperatorMongoDB(operator);

						// OR o AND
						if (signe.equals("$and") || signe.equals("$or")){
							if(i==operands.size()-1){
								throw new ParseException ("ERROR - Expected at least two or more operands for "+signe+" clause");
							}
							statementWhereMongo = statementWhereMongo.concat(signe).concat(":").concat("[").concat("{").concat(validateExpression(op1,null,select)).concat("}");
							do{
								i++;
								statementWhereMongo=statementWhereMongo.concat(",{");
								statementWhereMongo=statementWhereMongo.concat(validateExpression((Exp) operands.get(i),null,select)).concat("}");
							}while(i<operands.size()-1);
							statementWhereMongo=statementWhereMongo.concat("]");
						}else if(signe.equals("")){
							if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
								String auxop1 = op1.toString();
								boolean aux1Contiene = false;
								if(auxop1.contains("%")){
									aux1Contiene = true;
								}

								if(operator.equals("LIKE") && auxop1.contains("%")){
									if( (auxop1.startsWith("\"%") && auxop1.endsWith("%\"") ) ){//|| ((auxop2.startsWith("\"%") && auxop2.endsWith("%\"")))){
										if(auxop1.contains("%")){
											auxop1 = auxop1.replace("%", "/");
											if(auxop1.contains("\"")) {
												auxop1 = auxop1.replace("\"", "");
											}
										}
									}else{
										if(auxop1.startsWith("\"%") ){
											auxop1 = auxop1.replace("%", "/");
											if(auxop1.contains("\"")) {
												auxop1 = auxop1.replace("\"", "");
											}
										}else{
											if(aux1Contiene){
												auxop1 = ("/^").concat(auxop1);
											}
										}
										if(auxop1.endsWith("%\"")){
											auxop1 = auxop1.replace("%", "/");
											if(auxop1.contains("\"")) {
												auxop1 = auxop1.replace("\"", "");
											}
										}else{
											if(aux1Contiene){
												auxop1 =auxop1.concat("^/");
											}
										}
									}
									statementWhereMongo = statementWhereMongo.concat(auxop1);
								}
								else{
									if(statementWhereMongo.endsWith(":")){
										statementWhereMongo = statementWhereMongo.concat(auxop1);
									}else{
										if(auxop1.startsWith("\"") && auxop1.endsWith("\"")){
											statementWhereMongo = statementWhereMongo.concat(auxop1);
										}else{
											statementWhereMongo = statementWhereMongo.concat("\""+auxop1+"\"");
										}
									}
								}
								if(i < operands.size()-1){
									statementWhereMongo = statementWhereMongo.concat(":");
								}
							}else{
								statementWhereMongo = statementWhereMongo.concat(op1.toString());
								if(i <= operands.size()-1){
									statementWhereMongo = statementWhereMongo.concat(":");
								}
							}

						}else{ //qty: { $ne: 20 }

							if(op1 instanceof Expression){
								throw new ParseException ("ERROR - Expected: FIELD + Comparison Operator + VALUE + Logic Operator + FIELD + Comparison Operator + VALUE ");
							}else{
								if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
									statementWhereMongo = statementWhereMongo.concat("\""+ op1.toString()).concat("\" :").concat("{").concat(signe).concat(":");
									i=i+1;
									if(i<=operands.size()-1){
										statementWhereMongo = statementWhereMongo.concat(((Exp)operands.get(i)).toString()).concat("}");
									}

								}else{
									statementWhereMongo = statementWhereMongo.concat(op1.toString()).concat(":");
									i=i+1;
									if(i <= operands.size()-1){
										statementWhereMongo = statementWhereMongo.concat("{").concat(signe).concat(":").
												concat(((Exp)operands.get(i)).toString()).concat("}");
									}
								}
							}
						}
					}
				}
			}
		}
		return statementWhereMongo;
	}

	private String transformExpressions(Expression op1, Expression op2, String opr, QueryType select) throws ParseException{
		String signe = getOperatorMongoDB(opr);

		if(op1 != null && op2 != null){
			// OR o AND
			if (signe.equals("$and") || signe.equals("$or")){
				return signe.concat(":").concat("[").concat("{").concat(validateExpression(op1,null,select)).
						concat("}").concat(",").concat("{").concat(validateExpression(op2,null,select)).concat("}").concat("]");

			}else if(signe.equals("")){
				if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
					return "\""+ op1.toString().concat("\" :").concat(op2.toString());
				}else{
					return op1.toString().concat(":").concat(op2.toString());
				}

			}else{ //qty: { $ne: 20 }

				if(op1 instanceof Expression){
					throw new ParseException ("ERROR - Expected: FIELD + Comparison Operator + VALUE + Logic Operator + FIELD + Comparison Operator + VALUE ");
				}else{
					if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
						return "\"" + op1.toString().concat("\" :").concat("{").concat(signe).concat(":").concat(op2.toString()).concat("}");
					}else{
						return op1.toString().concat(":").concat("{").concat(signe).concat(":").concat(op2.toString()).concat("}");
					}
				}
			}
		}
		throw new ParseException("ERROR - Empty expressions");
	}

	private String transformHaving(Exp having, QueryType select) throws ParseException{
		String statementMongo="";
		Vector operands = ((Expression) having).getOperands();
		String operator = ((Expression) having).getOperator();

		if(operands != null){

			Exp op1 = (Exp) operands.get(0);
			Exp op2 = (Exp) operands.get(1);

			String signe = getOperatorMongoDB(operator);

			// AND
			if (signe.equals("$and")){
				statementMongo = statementMongo.concat(validateExpression(op1,null,select)).concat(",").concat(validateExpression(op2,null,select));

			}else if(signe.equals("")){
				if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
					statementMongo = statementMongo.concat("\"" + op1.toString()).concat("\" :").concat(op2.toString());
				}else{
					statementMongo = statementMongo.concat(op1.toString()).concat(":").concat(op2.toString());
				}

			}else if(signe.equals("$or")){  // Example OR $or: [ { status: "A" } , { age: 50 } ] 
				if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
					statementMongo = statementMongo.concat(signe).concat(":").concat("[").concat("{").concat(validateExpression(op1,null,select)).
							concat("}").concat(",").concat("{").concat(validateExpression(op2,null,select)).concat("}").concat("]");
				}else{

					statementMongo = statementMongo.concat(signe).concat(":").concat("[").concat("{").concat(validateExpression(op1,null,select)).
							concat("}").concat(",").concat("{").concat(validateExpression(op2,null,select)).concat("}").concat("]");
				}

			}else{ //qty: { $ne: 20 }


				if(op1 instanceof Expression && (!op1.toString().contains("count") && (!op1.toString().contains("COUNT")))){
					throw new ParseException ("ERROR - Expected: FIELD + Comparison Operator + VALUE + Logic Operator + FIELD + Comparison Operator + VALUE ");
				}else{
					if(select.name().equals("SELECT") || select.name().equals("UPDATE") || select.name().equals("DELETE")){
						if(op1.toString().contains("COUNT") || op1.toString().contains("count")){
							statementMongo = statementMongo.concat("count:{").concat(signe).concat(":").concat(op2.toString()).concat("}");
						}else{
							statementMongo = statementMongo.concat("\""+op1.toString()).concat("\" :").concat("{").concat(signe).concat(":").concat(op2.toString()).concat("}");
						}
					}else{
						if(op1.toString().contains("COUNT") || op1.toString().contains("count")){
							statementMongo = statementMongo.concat("count:{").concat(signe).concat(":").concat(op2.toString()).concat("}");
						}else{
							statementMongo = statementMongo.concat(op1.toString()).concat(":{").concat(signe).concat(":").concat(op2.toString()).concat("}");
						}
					}
				}
			}
		}
		return statementMongo;
	}

	private String transformGroup(Vector group) throws ParseException{

		String statementMongo ="";
		for(int i=0; i <group.size(); i++){
			statementMongo = statementMongo.concat("\"").concat("$").concat(group.get(i).toString()).concat("\"");
			if(i<group.size()-1){
				statementMongo = statementMongo.concat(",");
			}
		}
		return statementMongo;
	}

	//  A op B => age: { $gt: 25 }
	//  A
	private String validateExpression(Exp operando, String key, QueryType select) throws ParseException{

		if(operando instanceof Expression){
			Expression exp = (Expression) operando;

			Vector ops = exp.getOperands();
			String opr = exp.getOperator();

			Exp op1 = (Exp) ops.get(0);
			Exp op2 = (Exp) ops.get(1);

			if(op1 instanceof Expression && op2 instanceof Expression){
				if(opr.equals("OR") || opr.equals("AND")){
					return transformExpressions((Expression)op1, (Expression)op2, opr, select);
				}else{
					return "{".concat(transformExpressions((Expression)op1, (Expression)op2, opr, select)).concat("}");	
				}
			}

			String operator = getOperatorMongoDB(opr);

			if (operator.equals("")){ // a=b;
				if(select.name().equals("SELECT") || select.name().equals("UPDATE") || select.name().equals("DELETE")){
					return "\"" + op1.toString().concat("\" :").concat(op2.toString());
				}else{
					return op1.toString().concat(":").concat(op2.toString());
				}

			}else if(operator.equals("max") || (operator.equals("min"))){
				if(select.name().equals("SELECT") || select.name().equals("UPDATE") || select.name().equals("DELETE")){
					return operator.substring(1).concat("({").concat("\""+op1.toString()).concat("\" :").
							concat(op2.toString()).concat("})");
				}else{
					return operator.substring(1).concat("({").concat(op1.toString()).concat(":").
							concat(op2.toString()).concat("})");
				}
			}else if(operator.equals("+")){
				if(key != null){
					if(key.equals(op1.toString()) || key.equals(op2.toString())){
						if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
							return "$inc:{".concat("\"" +op1.toString()).concat("\" :").concat(op2.toString()).concat("}");
						}else{
							return "$inc:{".concat(op1.toString()).concat(":").concat(op2.toString()).concat("}");
						}
					}
				}else{
					if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
						return "{".concat("\"" + op1.toString() + "\"").concat(operator).concat(op2.toString()).concat("}");	
					}else{
						return "{".concat(op1.toString()).concat(operator).concat(op2.toString()).concat("}");	
					}
				}
			} else  if(operator.toUpperCase().equals("$IN")){
			        String result;
			        if(ops.size() >=3){
			            result = "\"" + op1.toString().concat("\" :").concat("{").concat(operator).concat(":").concat("[");
			            // el primer elemento es siempre la columna
			            for (int i = 1; i < ops.size(); i++) {
					result = result.concat(ops.get(i).toString()).concat(",");
				    }
			            result = result.substring(0, result.length()-1);
			            result = result.concat("]}");
			    	
			        }else{
			            result = "\"" + op1.toString().concat("\" :").concat("{").concat(operator).concat(":").
				    		concat(op2.toString()).concat("}");
			        }
			        if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
			    		return result;
			        }else{
        			    	return op1.toString().concat(":").concat("{").concat(operator).concat(":").
        			    			concat(op2.toString()).concat("}");
			        }
			        
			    }else{
			    	if(select.name().equals("SELECT")|| select.name().equals("UPDATE") || select.name().equals("DELETE")){
			    		return "\"" + op1.toString().concat("\" :").concat("{").concat(operator).concat(":").
				    		concat(op2.toString()).concat("}");
			    	}else{
			    		return op1.toString().concat(":").concat("{").concat(operator).concat(":").
			    				concat(op2.toString()).concat("}");
			    	}
			    }
		}else{
			throw new ParseException("ERROR 2: Expected FIELD + [comparison operator] + VALUE");
		}
		throw new ParseException("ERROR 2: Expected FIELD + [comparison operator] + VALUE");
	}

	//select .. limit [number];
	private String transformLimit(){

		if (limitSql != 0){
			return ".".concat(DB_MONGODB_LIMIT).concat("(").concat(Integer.toString(limitSql)).concat(")");
		}
		return "";
	}

	//select ...[limit] [number] skip [number];
	private String transformSkip(){
		if(skipSql != 0 ){
			return ".".concat(DB_MONGODB_SKIP).concat("(").concat(Integer.toString(skipSql)).concat(")");
		}
		return "";
	}

	// db.users.insert( {user_id: "bcd001",age: 45,status: "A"} )
	private String transformInsert(Insert statement) throws SQLException{

		String statementMongo = DB_MONGODB;

		if( statement != null){

			Vector columns = statement.getColumns();
			String table = statement.getTable();
			Vector values = statement.getValues();
//			Query query = statement.getQuery();
			statement.getQuery();
			if (!table.equals("")) {
				statementMongo = statementMongo.concat(table).concat(".").concat(DB_MONGODB_INSERT).concat("(");
			} else {
				throw new SQLException ("ERROR - Expected name table to insert elements");
			}
			if (values.size() != columns.size()) {
				throw new SQLException ("ERROR - Numbers columns and values are differents");
			} else {
				statementMongo = statementMongo.concat("{");
				for (int i=0; i<columns.size(); i++){
					statementMongo = statementMongo.concat("\""+ columns.get(i).toString()+ "\"").concat(":").concat(values.get(i).toString());
					if(i< columns.size()-1){
						statementMongo = statementMongo.concat(",");
					}
				}
				statementMongo = statementMongo.concat("}");
			}
			statementMongo = statementMongo.concat(")").concat(";");
		}
		return statementMongo;
	}

	//db.users.update( { status: "A" } , { $inc: { age: 3 } },{ multi: true })
	private String transformUpdate(Update statement) throws SQLException, ParseException{
		String statementMongo = DB_MONGODB;
		if(statement != null){
			String table = statement.getTable();
			Exp expWhere = statement.getWhere();
			Hashtable set = statement.getSet();

			if(!table.equals("")){
				statementMongo = statementMongo.concat(table).concat(".").concat(DB_MONGODB_UPDATE).concat("(");
			}
			if(expWhere != null){
				try {
					String aux = validateExpression(expWhere,null,QueryType.UPDATE);
					if(aux.startsWith("{") && aux.endsWith("}")){
						statementMongo = statementMongo.concat(aux).concat(",").concat("{");
					}else{
						statementMongo = statementMongo.concat("{").concat(validateExpression(expWhere,null,QueryType.UPDATE)).concat("}").concat(",").concat("{");
					}
				} catch (ParseException e) {
					Log.error("ERROR - Expected where [EXPRESSION]");
				}
			}else{
				throw new SQLException ("ERROR - Expected update [TABLE] set [NEW VALUE] where [EXPRESSION]");
			}
			if(set != null){
				statementMongo = statementMongo.concat(validateSet(set)).concat("}").concat(",");
			}else{
				throw new SQLException ("ERROR - Expected update [TABLE] set [NEW VALUE] where [EXPRESSION]");
			}
			//default this method updates all documents that meet the query criteria 
			statementMongo = statementMongo.concat("{").concat("multi:true").concat("}").concat(");");

		}
		return statementMongo;
	}

	private String validateSet (Hashtable set)throws ParseException{
		String statementMongo = "";
		if(set != null){
			statementMongo = statementMongo.concat("$set:{");
			Enumeration e = set.keys();
			boolean exp = false;
			while( e.hasMoreElements() ){
				Object key = e.nextElement();
				Object value = set.get(key);
				if (value instanceof Expression){
					statementMongo = statementMongo.replace("$set:{", "");
					exp = true;
					statementMongo = statementMongo.concat(validateExpression((Exp)value, key.toString(),QueryType.UPDATE));
				}else{ //not a expression is a value
					statementMongo = statementMongo.concat("\""+key.toString()).concat("\":").concat(value.toString());	
				}	 
				if( e.hasMoreElements()){
					statementMongo = statementMongo.concat(",");
				}
				else{
					if(!exp){
						statementMongo = statementMongo.concat("}");
					}
				}
			}
		}
		return statementMongo;
	}

	//db.users.remove( { status: "D" } )
	private String transformDelete(Delete statement)throws SQLException{
		String statementMongo = DB_MONGODB;

		if(statement != null){
			String table = statement.getTable();
			Exp expWhere= statement.getWhere();

			if(!table.equals("")){
				statementMongo = statementMongo.concat(table).concat(".").concat(DB_MONGODB_DELETE).concat("(");
			}
			if(expWhere != null){
				try {
					statementMongo = statementMongo.concat("{").concat(validateExpression(expWhere,null,QueryType.DELETE)).concat("}");
				} catch (ParseException e) {
					Log.error("ERROR - Expected where [EXPRESSION]");
				}
			}else{
				statementMongo = statementMongo.concat("{}");
			}
			statementMongo = statementMongo.concat(");");
		}
		return statementMongo;
	}

	private static String getOperatorMongoDB(String op) throws ParseException  {

		if(op.equals("=") || op.toUpperCase().equals("LIKE")) {
			return "";
		} else if(op.equals("!=") || op.toUpperCase().equals("NOT")) {
			return "$ne";
		} else if (op.equals(">")) {
			return "$gt";
		} else if (op.equals("<")) {
			return "$lt";
		} else if (op.equals("<=")) {
			return "$lte";
		} else if (op.equals(">=")) {
			return "$gte";
		} else if (op.equals("||") || op.toUpperCase().equals("OR")) {
			return "$or";
		} else if (op.equals("&&") || op.toUpperCase().equals("AND")) {
			return "$and";
		} else if (op.toUpperCase().equals("IN")) {
			return "$in";
		} else if (op.toUpperCase().equals("ALL")) {
			return "$all";	 
		} else if (op.toUpperCase().equals("+")) {
			return "+";
		} else if (op.toUpperCase().contains("MAX")) {
			return "$max";
		} else if (op.toUpperCase().contains("MIN")) {
			return "$min";
		} else if (op.toUpperCase().contains("SUM")) {
			return "$sum";
		} else if (op.toUpperCase().contains("AVG")) {
			return "$avg";
		}else if (op.toUpperCase().contains("S_NEAR")) {
			return "$near";  
		}else {
			throw new ParseException ("Unkown operator. Expected: =,!=,>,<,>=,<=, OR, AND, LIKE");
		} 
	}

	private boolean isSpecial(String statement){
		return (statement.contains(" ") || 
				statement.toUpperCase().replace(" ", "").contains("MAX(") ||
				statement.toUpperCase().replace(" ", "").contains("MIN(") ||
				statement.toUpperCase().replace(" ", "").contains("AVG("));
	}

	private String transformOper(String oper, QueryType select)throws ParseException{

		if(oper.contains("(")){

			if(oper.contains(" ")){  //sum(x) as y
				String [] opers =oper.split(" ");
				String aux = opers[0].substring(opers[0].indexOf("(") +1, opers[0].indexOf(")"));
				if(select.name().equals("SELECT") || select.name().equals("UPDATE") || select.name().equals("DELETE")){
					return "\""+ opers[1]+ "\"".concat(":{").concat(getOperatorMongoDB(opers[0])).concat(":\"$").concat(aux).concat("\"}");
				}else{
					return opers[1].concat(":{").concat(getOperatorMongoDB(opers[0])).concat(":\"$").concat(aux).concat("\"}");
				}
			}else{ // sum(x)
				if(select.name().equals("SELECT") || select.name().equals("UPDATE") || select.name().equals("DELETE")){
					String aux = oper.substring(oper.indexOf("(") +1, oper.indexOf(")"));
					return "{".concat(getOperatorMongoDB(oper)).concat(":\"$").concat("\""+ aux +"\"").concat("\"}");
				}else{
					String aux = oper.substring(oper.indexOf("(") +1, oper.indexOf(")"));
					return "{".concat(getOperatorMongoDB(oper)).concat(":\"$").concat(aux).concat("\"}");
				}
			}

		}else
			throw new ParseException("ERROR - Expected [OPERATOR] FIELD, for example SUM(price)");

	}
}
