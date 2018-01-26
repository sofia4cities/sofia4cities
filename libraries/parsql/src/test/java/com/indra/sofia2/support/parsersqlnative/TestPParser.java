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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.indra.sofia2.support.parsersqlnative.PParser;
import com.indra.sofia2.support.parsersqlnative.ParseException;
import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.util.ParserConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestPParser implements ParserConstants {

	private static Log log = LogFactory.getLog(TestPParser.class);
	
	public static void main(String args[]) throws ParseException {

		PParser p = null ;

		if ( args.length < 1  ) {
			log.info("Reading from stdin (exit; to finish)");
			p = new PParser(System.in) ;

		} else {

			try {
				p = new PParser(new DataInputStream(
						new FileInputStream(args[0]))) ;
			} catch (FileNotFoundException e) {
				log.info("File " + args[0] +
						" not found. Reading from stdin") ;
				p = new PParser(System.in) ;
			}
		} 

		if ( args.length > 0 ) {
			log.info(args[0]) ;
		}

		Statement st = null;
		while((st = p.sQLStatement()) != null) {
			log.info(st.toString());
		}
		log.info("Parse Successful") ;

	}

}
