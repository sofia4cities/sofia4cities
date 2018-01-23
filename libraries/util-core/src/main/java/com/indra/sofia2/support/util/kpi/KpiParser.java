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
package com.indra.sofia2.support.util.kpi;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public final class KpiParser {

	final static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	final static String DATE_FORMAT = "yyyy-MM-dd";
	
	private static Expression getTemporaryParameters(String campoFecha,final Calendar calendar,boolean firstTime,Date fechaIni) throws Exception{
		
		Between bet=new Between();
		DateTimeLiteralExpression  dateTime=new DateTimeLiteralExpression();
		DateTimeLiteralExpression dateTimeEnd=new DateTimeLiteralExpression();
		TemporalidadKpi fecha=TemporalidadKpi.valueOf(campoFecha);
		Calendar fechaInicial=(Calendar) calendar.clone();
		Calendar fechaFinal=(Calendar) calendar.clone();

		switch (fecha) {
			case $current_minute:
				break;
			case $current_hour:
				//minuto y segundo 00 y hora actual hasta el seg, minuto y hora actual 
				fechaInicial.set(Calendar.MINUTE,0);
				fechaFinal.set(Calendar.MINUTE,59);
				break;
			case $current_day:
				break;
			case $current_year:
				fechaInicial.set(Calendar.MONTH,0);// los meses en calendar empiezan en 0
				fechaFinal.set(Calendar.MONTH,11);
			case $current_month:
				//dia 1 mes 1 del año actual hasta dia,mes y año actual 
				fechaInicial.set(Calendar.DAY_OF_MONTH,1);
				fechaFinal.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); 
				break;
			default:
				throw new IllegalArgumentException("Unknown temporary reference: "+campoFecha);
		}
		if (fecha==TemporalidadKpi.$current_minute || fecha==TemporalidadKpi.$current_hour){
			
			dateTimeEnd.setType(DateTime.TIMESTAMP);
			dateTimeEnd.setValue(getFormat(fechaFinal.getTime(),ISO_FORMAT));
			dateTime.setType(DateTime.TIMESTAMP);
			
			if (firstTime && fechaIni!=null){
				dateTime.setValue(getFormat(fechaIni,ISO_FORMAT));
			}else if (firstTime && fechaIni==null){
				//no se informa de la fecha inicial, se sustituye el between por un <= a la fecha final
				MinorThanEquals minor=new MinorThanEquals();
				minor.setRightExpression(dateTimeEnd);
			}else {
				fechaInicial.set(Calendar.SECOND,0);
				dateTime.setValue(getFormat(fechaInicial.getTime(),ISO_FORMAT));
			}
		}else {
			dateTime.setType(DateTime.DATE);
			dateTimeEnd.setType(DateTime.DATE);
			dateTimeEnd.setValue(getFormat(fechaFinal.getTime(),DATE_FORMAT));
			
			if (firstTime){
				dateTime.setValue(getFormat(fechaIni,DATE_FORMAT));
			}else if (firstTime && fechaIni==null){
				//no se informa de la fecha inicial, se sustituye el between por un <= a la fecha final
				MinorThanEquals minor=new MinorThanEquals();
				minor.setRightExpression(dateTimeEnd);
			}else {
				dateTime.setValue(getFormat(fechaInicial.getTime(),DATE_FORMAT));
			}
		}
		
		bet.setBetweenExpressionStart(dateTime);
		bet.setBetweenExpressionEnd(dateTimeEnd);
		return bet;
	}
	
	private static String getFormat(Date date,String pattern){
		return "(\""+new SimpleDateFormat(pattern).format(date)+"\")";
	}
	
	private static String operatefields(String queryResult,Calendar calendar){
		
		//Tratamiento de los campos temporales: resto de operadores
		Pattern p = Pattern.compile("(\\$current_\\w+)");
		Matcher matcher = p.matcher(queryResult);
		DateTimeLiteralExpression dateTime=new DateTimeLiteralExpression();
		while(matcher.find()){
			TemporalidadKpi fecha=TemporalidadKpi.valueOf(matcher.group());
			switch (fecha) {
				case $current_minute:
				case $current_hour:
					dateTime.setType(DateTime.TIMESTAMP);
					dateTime.setValue(getFormat(calendar.getTime(),ISO_FORMAT));
					queryResult=queryResult.replace(fecha.name(),dateTime.toString());
					break;
				case $current_day:
				case $current_month:
				case $current_year:
					dateTime.setType(DateTime.DATE);
					dateTime.setValue(getFormat(calendar.getTime(),DATE_FORMAT));
					queryResult=queryResult.replace(fecha.name(),dateTime.toString());
					break;
				default:
					throw new IllegalArgumentException("Unknown temporary reference: "+fecha.name());
			}
		}
		return queryResult;
	}
	
	public static String queryAnalyzerAndLimit1(String query,boolean firstTime,Date fechaInicial) throws Exception{
		
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Select select = (Select) parserManager.parse(new StringReader(query));
		PlainSelect ps = (PlainSelect) select.getSelectBody();
		Calendar calendar = Calendar.getInstance();
		
		//Tratamiento de los campos temporales: operadores equalsTo
		Expression e=generateExpression(ps.getWhere(),calendar,firstTime,fechaInicial);
		ps.setWhere(e);
		Limit limit=new Limit();
		limit.setRowCount(1);
		ps.setLimit(limit);
		return operatefields(ps.toString(),calendar);
	}
	
	public static String queryAnalyzer(String query,boolean firstTime,Date fechaInicial) throws Exception{
		
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Select select = (Select) parserManager.parse(new StringReader(query));
		PlainSelect ps = (PlainSelect) select.getSelectBody();
		Calendar calendar = Calendar.getInstance();
		
		//Tratamiento de los campos temporales: operadores equalsTo
		Expression e=generateExpression(ps.getWhere(),calendar,firstTime,fechaInicial);
		ps.setWhere(e);
		return operatefields(ps.toString(),calendar);
	}
	
	private static Expression generateExpression(Expression e,final Calendar calendar,final boolean firstTime,final Date fechaIni) throws Exception {
				
		if (e instanceof AndExpression){
			
			AndExpression campo = (AndExpression) e;
			if (campo.getLeftExpression() instanceof Expression){
				Expression ex=generateExpression(campo.getLeftExpression(),calendar,firstTime,fechaIni);
				campo.setLeftExpression(ex);
			} 
			if (campo.getRightExpression() instanceof Expression){
				Expression ex=generateExpression(campo.getRightExpression(),calendar,firstTime,fechaIni);
				campo.setRightExpression(ex);
			}
			return campo;		
		} else if (e instanceof OrExpression){
			
			OrExpression campo = (OrExpression) e;
			if (campo.getLeftExpression() instanceof Expression){
				Expression ex=generateExpression(campo.getLeftExpression(),calendar,firstTime,fechaIni);
				campo.setLeftExpression(ex);
			} 
			if (campo.getRightExpression() instanceof Expression){
				Expression ex=generateExpression(campo.getRightExpression(),calendar,firstTime,fechaIni);
				campo.setRightExpression(ex);
			}
			return campo;
		}else if (e instanceof EqualsTo){
			
			EqualsTo campo=(EqualsTo) e;
			if (campo.getLeftExpression().toString().contains("$current_") && campo.getRightExpression().toString().contains("$current_")){
				throw new Exception ("Two or more temporary dependencies detected in the same operand");
			} else if (campo.getLeftExpression().toString().contains("$current_")){
				// El campo a tratar es el izquierdo, dejamos el derecho intacto
				Expression expression=getTemporaryParameters(campo.getLeftExpression().toString(),calendar,firstTime,fechaIni);
				if (expression instanceof Between){
					Between bet= (Between) expression;
					bet.setLeftExpression(campo.getRightExpression());
					return bet;
				}else {
					MinorThanEquals minor=(MinorThanEquals) expression;
					minor.setLeftExpression(campo.getRightExpression());
					return minor;
				}

			}else if (campo.getRightExpression().toString().contains("$current_")){
				// El campo a tratar es el derecho, dejamos el izquierdo intacto
				
				Expression expression=getTemporaryParameters(campo.getRightExpression().toString(),calendar,firstTime,fechaIni);
				if (expression instanceof Between){
					Between bet= (Between) expression;
					bet.setLeftExpression(campo.getLeftExpression());
					return bet;
				}else {
					MinorThanEquals minor=(MinorThanEquals) expression;
					minor.setLeftExpression(campo.getLeftExpression());
					return minor;
				}
				
			}else {
				//Si no tiene $current_ no lo modificamos
				return campo;
			}
		}else {
			// Puede tener campos temporales pero no requieren parseo
			return e;
		}
	}
	
}
