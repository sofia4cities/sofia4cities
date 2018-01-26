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

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.indra.sofia2.support.parsersqlnative.parseator.Parser;
import com.indra.sofia2.support.parsersqlnative.parseator.ParserSql;
import com.indra.sofia2.support.parsersqlnative.parseator.ParserSqlToMongoDB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations ={"classpath:/META-INF/spring/applicationContext.xml"})
public class TestParserGeospatial {

	private static Log log = LogFactory.getLog(TestParserGeospatial.class);
	ParserSql parserToMogoDB = new ParserSqlToMongoDB();
	String sql;
	
	@Test
	public void testFeedbusLimit() throws SQLException{
		sql = "select * from  FeedautobusGijon WHERE Feed.geometry S_near(ST_Point(-8.4, 43.37), 300000) limit 3;";
		try {
			String result = checkExpression(sql);
			Assert.assertEquals(result, "db.FeedautobusGijon.find({\"Feed.geometry\":{$near:{$geometry: {type: \"Point\", coordinates: [-8.4, 43.37]},$maxDistance:  300000}}}).limit(3);");
		} catch (ParseException e) {
			assert false;
		}
	}
	
	@Test
	public void testFeedbus() throws SQLException{
		sql = "select * from  FeedautobusGijon WHERE Feed.geometry S_near(ST_Point(-8.4, 43.37), 300000);";
		try {
			String result = checkExpression(sql);
			Assert.assertEquals(result, "db.FeedautobusGijon.find({\"Feed.geometry\":{$near:{$geometry: {type: \"Point\", coordinates: [-8.4, 43.37]},$maxDistance:  300000}}});");
		} catch (ParseException e) {
			assert false;
		}
	}
	
	@Test
	public void testTwitter() throws  SQLException{
		 sql = "select * from  TweetsDemoBarna WHERE Tweet.geometry S_NEAR (ST_Point(-3.6504403593750157, 40.82478718269643), 616607.2364220816);";
			
			try {
				String result = checkExpression(sql);
				Assert.assertEquals(result, "db.TweetsDemoBarna.find({\"Tweet.geometry\":{$near:{$geometry: {type: \"Point\", coordinates: [-3.6504403593750157, 40.82478718269643]},$maxDistance:  616607.2364220816}}});");
			} catch (ParseException e) {
				assert false;
			}
	}
	
	@Test
	public void testInstagram() throws  SQLException{
		 sql = "select * from  feedInstagram WHERE Feed.geometry S_NEAR (ST_Point(-8.398616667, 43.373688333), 10);";
			
			try {
				checkExpression(sql);
				String result = checkExpression(sql);
				Assert.assertEquals(result, "db.feedInstagram.find({\"Feed.geometry\":{$near:{$geometry: {type: \"Point\", coordinates: [-8.398616667, 43.373688333]},$maxDistance:  10}}});");
			} catch (ParseException e) {
				assert false;
			}
	}
	
	@Test
	public void testGijon() throws  SQLException{
		sql = "select * from  FeedautobusGijon WHERE Feed.geometry S_near(ST_Point(-8.4, 43.37), 300000);";
		
		try {
			String result = checkExpression(sql);
			Assert.assertEquals(result, "db.FeedautobusGijon.find({\"Feed.geometry\":{$near:{$geometry: {type: \"Point\", coordinates: [-8.4, 43.37]},$maxDistance:  300000}}});");
		} catch (ParseException e) {
			assert false;
		}
	}
	
	@Test
	public void testErrDistance() throws  SQLException{
		try {
			sql="select * from  TweetsDemoBarna WHERE Tweet.geometry S_NEAR (ST_Point(-3.6504403593750157, 40.82478718269643) );";
			checkExpression(sql);
			assert false;
		} catch (ParseException e) {
			log.error("Error para la sentencia " + sql);
			log.error(e.getMessage());
			log.info("");
			assert true;
		}
		
	}
	@Test
	public void testErrLatitude() throws  SQLException{
		try {
			sql="select * from  TweetsDemoBarna WHERE Tweet.geometry S_NEAR (ST_Point(-3.6504403593750157), 616607.2364220816 );";
			checkExpression(sql);
			assert false;
		} catch (ParseException e) {
			log.error("Error para la sentencia " + sql);
			log.error(e.getMessage());
			log.info("");
			assert true;
		}
		try {
			sql="select * from  TweetsDemoBarna WHERE Tweet.geometry S_NEAR (ST_Point(-3.6504403593750157,  ), 616607.2364220816 );";
			checkExpression(sql);
			assert false;
		} catch (ParseException e) {
			log.error("Error para la sentencia " + sql);
			log.error(e.getMessage());
			log.info("");
			assert true;
		}
	}
	@Test
	public void testErrColumn() throws  SQLException{
		try {
			sql="select * from  TweetsDemoBarna WHERE S_NEAR (ST_Point(-3.6504403593750157,  ), 616607.2364220816 );";
			checkExpression(sql);
			assert false;
		} catch (ParseException e) {
			log.error("Error para la sentencia " + sql);
			log.error(e.getMessage());
			log.info("");
			assert true;
		}
		
	}
	
	
	
	public String checkExpression(String query) throws ParseException, SQLException{
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		Parser parser = new Parser(query);
		Statement statement = parser.processStatement();
		log.info("SQL de entrada:         " + query);
		String queryMongo = parserToMogoDB.postProcess(statement);
		log.info("MONGODB de salida:     " + queryMongo + "\n");
		return queryMongo;
	}

	
}
