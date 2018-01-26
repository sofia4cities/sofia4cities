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
package com.indra.sofia2.support.parsersqlnative;

import java.io.ByteArrayInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.indra.sofia2.support.parsersqlnative.Constant;
import com.indra.sofia2.support.parsersqlnative.Exp;
import com.indra.sofia2.support.parsersqlnative.Expression;
import com.indra.sofia2.support.parsersqlnative.Query;
import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;

public class CheckRowNum
{

	private static Log log = LogFactory.getLog(CheckRowNum.class);

	public CheckRowNum()
	{
	}

	public static void main(String[] args)
	{
		CheckRowNum cr = new CheckRowNum();

		String sql="SELECT * from Dato where x=1 and y=4;";
		try
		{
			log.info( cr.fixSqlWithRowNum(sql));
		}
		catch ( Exception ex)
		{
			log.info(ex);
			ex.printStackTrace();
		}

	}
		
	public String fixSqlWithRowNum( String statementSql) throws Exception
	{
		ByteArrayInputStream  si = new ByteArrayInputStream ( statementSql.getBytes("UTF-8") );
		
		Parser par = new Parser(si);
		Statement statement = par.processStatement();
		Query query = ( Query ) statement;
		Expression  valueWhere  = ( Expression )query.getWhere();
		
		for ( int i =0; i < valueWhere.nbOperands() ; i++)
		{
			log.info(valueWhere.getOperand(i));
		}

		Expression  obj = new Expression(
				"<",
				new Constant("ROWNUM", Constant.COLUMNNAME),
				new Constant("900", Constant.NUMBER));
		valueWhere.addOperand((Exp)(obj));

		for ( int i =0; i < valueWhere. nbOperands() ; i++)
		{
			log.info(valueWhere.getOperand(i) );
		}

		return statement.toString();
	}
}

