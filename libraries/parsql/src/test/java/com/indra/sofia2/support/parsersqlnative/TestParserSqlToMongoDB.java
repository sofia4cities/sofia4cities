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

import com.indra.sofia2.support.parsersqlnative.ParseException;
import com.indra.sofia2.support.parsersqlnative.Statement;
import com.indra.sofia2.support.parsersqlnative.parseator.Parser;
import com.indra.sofia2.support.parsersqlnative.parseator.ParserSql;
import com.indra.sofia2.support.parsersqlnative.parseator.ParserSqlToMongoDB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations ={"classpath:/META-INF/spring/applicationContext.xml"})
public class TestParserSqlToMongoDB {

	private static Log log = LogFactory.getLog(TestParserSqlToMongoDB.class);
	
	// Paser validate SQL statement
	// PaserSqlToMongoDB transfor to MongoDB
	@Test
	public void testParser() throws ParseException{

		ParserSql parserToMogoDB = new ParserSqlToMongoDB();

		String sql1 = "SELECT * FROM SensorTemperatura;";
		Parser p = new Parser(sql1);
		Statement st =  p.processStatement();
		try{
			log.info("SQL:         " + st.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st), "db.SensorTemperatura.find();");
		}catch (SQLException e) {

			log.info("Problems to proccces");
		}
	}
	@Test
	public void testPaser1() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql2 = "select A,B,C FROM SensorTemperatura;";
		Parser p2 = new Parser(sql2);
		Statement st2 = p2.processStatement();
		try{
			log.info("SQL:         " + st2.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st2) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st2), "db.SensorTemperatura.find({},{_id:0,\"A\":1,\"B\":1,\"C\":1});");
		}catch(SQLException e){
			log.info("Problems to process");
		}
	}

	@Test
	public void testPaser2() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql3 = "select id, a, b from SensorTemperatura;";
		Parser p3 = new Parser(sql3);
		Statement st3 = p3.processStatement();
		try{
			log.info("SQL:         " + st3.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st3) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st3), "db.SensorTemperatura.find({},{\"a\":1,\"b\":1});");
		}catch(SQLException e){
			log.info("Problems to process");
		}
	}

	@Test
	public void testParser3()throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql4 = "select * from users where status = \"A\";";
		Parser p4 = new Parser(sql4);
		Statement st4 = p4.processStatement();
		try{
			log.info("SQL:         " + st4.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st4) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st4), "db.users.find({\"status\":\"A\"});");
		}catch(SQLException e){

		}
	}

	@Test
	public void testParser4() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql5 = "SELECT * from Dato where x=\"1\" and y=4;";
		Parser p5 = new Parser(sql5);
		Statement st5 = p5.processStatement();
		try{
			log.info("SQL:         " + st5.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st5) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st5), "db.Dato.find({\"x\" :\"1\",\"y\" :4});");

		}catch(Exception e){

		}
	}

	@Test
	public void testParser5() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql6 = "SELECT * from Dato where (x=1 and (y=4 or y=2));";
		Parser p6 = new Parser(sql6);
		Statement st6 = p6.processStatement();
		try{
			log.info("SQL:         " + st6.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st6) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st6), "db.Dato.find({\"x\" :1,$or:[{\"y\" :4},{\"y\" :2}]});");
		}catch(Exception e){

		}
	}
	
	@Test
	public void testParserWhere() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql6 = "SELECT * from Dato where (x=1 and (y=4 or y=2));";
		Parser p6 = new Parser(sql6);
		Statement st6 = p6.processStatement();
		try{
			log.info("SQL:         " + st6.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st6) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st6), "db.Dato.find({\"x\" :1,$or:[{\"y\" :4},{\"y\" :2}]});");
		}catch(Exception e){

		}
	}

	@Test
	public void testParserWhere1() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql8 = "select * from users where age < 24;";
		
		try{
			Parser p8 = new Parser(sql8);
			Statement st8 = p8.processStatement();
			log.info("SQL:         " + st8.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st8) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st8), "db.users.find({\"age\" :{$lt:24}});");

		}catch(Exception e){

		}
	}
	
	@Test
	public void testParserWhere2() throws ParseException{
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();

		String sql9 = "select * from users where age > 22 AND age <= 50;";
		Parser p9 = new Parser(sql9);
		try{
			Statement st9 = p9.processStatement();
			log.info("SQL:         " + st9.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st9) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st9), "db.users.find({\"age\" :{$gt:22},\"age\" :{$lte:50}});");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserWhere3() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql7 = "select count(*) from SensorTemperatura where (SensorTemperatura.medida > 24 or SensorTemperatura.medida =29 ) and (SensorTemperatura.identificador = \"preuba\");";

		try{
			Parser p7 = new Parser(sql7);
			Statement st7 = p7.processStatement();
			log.info("SQL:         " + st7.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st7) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st7), "db.SensorTemperatura.count({$or:[{\"SensorTemperatura.medida\" :{$gt:24}},{\"SensorTemperatura.medida\" :29}],\"SensorTemperatura.identificador\" :\"preuba\"});");

		}catch(Exception e){

		}
	}
	
	@Test
	public void testParserWhere4() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql7 = "select count(*) from SensorTemperatura where (SensorTemperatura.medida > 24 and (SensorTemperatura.identificador = \"prueba\" or SensorTemperatura.identificador = \"ST-TA111\");";

		try{
			Parser p7 = new Parser(sql7);
			Statement st7 = p7.processStatement();
			log.info("SQL:         " + st7.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st7) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st7), "db.users.find({\"age\" :{$gt:24}});");

		}catch(Exception e){

		}
	}
	
	@Test 
	public void testParserWhereOrderByDesc() throws ParseException{
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql10 = "select * from users where status = \"A\" ORDER BY user_id DESC;";
		Parser p10 = new Parser(sql10);
		try{
			Statement st10 = p10.processStatement();
			log.info("SQL:         " + st10.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st10) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st10), "db.users.find({\"status\":\"A\"}).sort({\"user_id\":-1});");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test 
	public void testParserWhereOrderByAsc() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql11 = "select * from users where status = \"A\" ORDER BY user_id ASC;";
		Parser p11 = new Parser(sql11);
		try{
			Statement st11 = p11.processStatement();
			log.info("SQL:         " + st11.toString() + ";");
			log.info("MONGODB1:     " + parserToMogoDB.postProcess(st11) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st11), "db.users.find({\"status\":\"A\"}).sort({\"user_id\":1});");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserCount() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql12 = "select count (*) from users;";
		Parser p12 = new Parser (sql12);
		try{
			Statement st12 = p12.processStatement();
			log.info("SQL:         " + st12.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st12) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st12), "db.users.count();");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserCountKey() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql13 = "select count (user_id) from users;";
		Parser p13 = new Parser (sql13);
		try{
			Statement st13 = p13.processStatement();
			log.info("SQL:         " + st13.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st13) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st13), "db.users.count({\"user_id\":{$exists:true}});");
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserCountWhere() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql14 = "select count (*) from users where age > 30;";
		Parser p14 = new Parser (sql14);

		try{
			Statement st14 = p14.processStatement();
			log.info("SQL:         " + st14.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st14) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st14), "db.users.count({\"age\" :{$gt:30}});");
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserCountWhere1() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql14 = "select count (*) from users where age = 30;";
		Parser p14 = new Parser (sql14);

		try{
			Statement st14 = p14.processStatement();
			log.info("SQL:         " + st14.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st14) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st14), "db.users.count({\"age\":30});");
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserDisctinct() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql15 = "select distinct (status) from users;";
		Parser p15 = new Parser (sql15);

		try{
			Statement st15 = p15.processStatement();
			log.info("SQL:         " + st15.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st15) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st15), "db.users.distinct('status');");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserDisctinctKeyWhere() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql16 = "select distinct (ord_dt) from orders where price > 10;";
		Parser p16 = new Parser (sql16);
		try{
			Statement st16 = p16.processStatement();
			log.info("SQL:         " + st16.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st16) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st16), "db.orders.distinct('ord_dt',{\"price\" :{$gt:10}});");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserLimit1() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql17 = "select * from users limit 1;";
		Parser p17 = new Parser (sql17);
		try{
			Statement st17 = p17.processStatement();
			log.info("SQL:         " + st17.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st17) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st17), "db.users.find().limit(1);");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserInsert() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql20 = "insert into users (user_id, age, status) values (\"bdc01\",45,\"A\");";
		Parser p20 = new Parser (sql20);
		try{
			Statement st20 = p20.processStatement();
			log.info("SQL:         " + st20.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st20) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st20), "db.users.insert({\"user_id\":\"bdc01\",\"age\":45,\"status\":\"A\"});");
			
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserDeleteWhere() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql21 = "delete from users where status = \"D\";";
		Parser p21 = new Parser (sql21);
		try{
			Statement st21 = p21.processStatement();
			log.info("SQL:         " + st21.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st21) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st21), "db.users.remove({\"status\" :\"D\"});");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserDelete() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql22 = "delete from users;";
		Parser p22 = new Parser(sql22);
		try{
			Statement st22 = p22.processStatement();
			log.info("SQL:         " + st22.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st22) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st22), "db.users.remove({});");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserUpdate() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql23 = "update users set status = \"C\" where age >24;";
		Parser p23 = new Parser(sql23);
		try{
			Statement st23 = p23.processStatement();
			log.info("SQL:         " + st23.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st23) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st23), "db.users.update({\"age\" :{$gt:24}},{$set:{\"status\":\"C\"}},{multi:true});");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserUpdate2() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql23 = "update users set status = \"C\" where age > 24 or age < 30;";
		Parser p23 = new Parser(sql23);
		try{
			Statement st23 = p23.processStatement();
			log.info("SQL:         " + st23.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st23) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st23), "db.users.update({$or:[{\"age\" :{$gt:24}},{\"age\" :{$lt:30}}]},{$set:{\"status\":\"C\"}},{multi:true});");

		}catch(Exception e){
			Assert.fail();
		}
	}
	@Test
	public void testParserUpdate3() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql23 = "update users set status = \"C\" where age > 24 and age < 30;";
		Parser p23 = new Parser(sql23);
		try{
			Statement st23 = p23.processStatement();
			log.info("SQL:         " + st23.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st23) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st23), "db.users.update({\"age\" :{$gt:24},\"age\" :{$lt:30}},{$set:{\"status\":\"C\"}},{multi:true});");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserUpdate4() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql23 = "update users set status = \"C\", age = 23 where age > 24 and age < 30;";
		Parser p23 = new Parser(sql23);
		try{
			Statement st23 = p23.processStatement();
			log.info("SQL:         " + st23.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st23) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st23), "db.users.update({\"age\" :{$gt:24},\"age\" :{$lt:30}},{$set:{\"status\":\"C\",\"age\":23}},{multi:true});");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserUpdateWhere() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql24 = "update users set age = age + 3 where status = \"A\";";
		Parser p24 = new Parser (sql24);
		try{
			Statement st24 = p24.processStatement();
			log.info("SQL:         " + st24.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st24) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st24), "db.users.update({\"status\" :\"A\"},{$inc:{\"age\" :3}},{multi:true});");
			
		}catch(Exception e){
			Assert.fail();
		}
	}

	@Test
	public void testParserLimit() throws ParseException {
		ParserSql parserToMongoDB = new ParserSqlToMongoDB();
		String sql29 = "select * from users where (X > 2) limit 1;";
		Parser p29 = new Parser (sql29);
		Statement st29 = p29.processStatement();
		try{
			log.info("SQL:         " + st29.toString() + ";");
			log.info("MONGODB:     " + parserToMongoDB.postProcess(st29) + "\n");
			Assert.assertEquals(parserToMongoDB.postProcess(st29), "db.users.find({\"X\" :{$gt:2}}).limit(1);");
		}catch(Exception e){
			Assert.fail();
		}
	}

	@Test
	public void testParserLimitSkip() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql30 = "select * from users limit 5 skip 10;";
		Parser p30 = new Parser (sql30);
		Statement st30 = p30.processStatement();
		try{
			log.info("SQL:         " + st30.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st30) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st30), "db.users.find().limit(5).skip(10);");

		}catch(Exception e){
			Assert.fail();
		}
	}

	@Test
	public void testParserskip() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql31 = "select * from users skip 10;";
		Parser p31 = new Parser(sql31);
		try{
			Statement st31 = p31.processStatement();
			log.info("SQL:         " + st31.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st31) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st31), "db.users.find().skip(10);");
		}catch(Exception e){
			log.info("ERROR: " + e.getMessage());
		}
	}
	
	@Test
	public void testParserLike() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql32 = "select * from users where user_id like \"%bc%\";";
		Parser p32 = new Parser(sql32);
		try{
			Statement st32 = p32.processStatement();
			log.info("SQL:         " + st32.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st32) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st32), "db.users.find({\"user_id\":/bc/});");
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserLike1() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql32 = "select * from users where user_id like \"bc%\";";
		Parser p32 = new Parser(sql32);
		try{
			Statement st32 = p32.processStatement();
			log.info("SQL:         " + st32.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st32) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st32), "db.users.find({\"user_id\":/^bc/});");
		}catch(Exception e){
			Assert.fail();
		}
	}

	@Test
	public void testParserLike2() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql32 = "select * from users where user_id like \"%bc\";";
		Parser p32 = new Parser(sql32);
		try{
			Statement st32 = p32.processStatement();
			log.info("SQL:         " + st32.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st32) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st32), "db.users.find({\"user_id\":/bc^/});");
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserLike3() throws ParseException {
		ParserSql parserToMongoDB = new ParserSqlToMongoDB();
		String sql = "select count(*) from TweetsDemoBarna where Tweet.tweet_text like \"%Fiestas%\";";
		Parser p = new Parser(sql);
		try{
			Statement st = p.processStatement();
			log.info("SQL:         " + st.toString() + ";");
			log.info("MONGODB:     " + parserToMongoDB.postProcess(st) + "\n");
			Assert.assertEquals(parserToMongoDB.postProcess(st), "db.TweetsDemoBarna.count({\"Tweet.tweet_text\":/Fiestas/});");     
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserSelectSumGroupBy() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql27 = "select cust_id, sum (price) as total from orders group by cust_id;";
		Parser p27 = new Parser (sql27);

		try{
			Statement st27 = p27.processStatement();
			log.info("SQL:         " + st27.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st27) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st27), "db.orders.aggregate([{$group:{_id:{\"cust_id\":\"$cust_id\"},\"total\":{$sum:\"$price\"}}}]);");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserkeyOrdersByGroupBy() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql28 = "select cut_id, sum (price) as total from orders group by cust_id order by total;";
		Parser p28 = new Parser (sql28);
		Statement st28 = p28.processStatement();
		try {
			log.info("SQL:         " + st28.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st28) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st28), "db.orders.aggregate([{$group:{_id:{\"cut_id\":\"$cut_id\"},\"total\":{$sum:\"$price\"}}},{$sort:{\"total\":1}}]);");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserkeygropBy() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql18 = "SELECT BUYERID, MAX(PRICE) AS total FROM ANTIQUES GROUP BY BUYERID;";
		Parser p18 = new Parser (sql18);
		try{
			Statement st18 = p18.processStatement();
			log.info("SQL:         " + st18.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st18) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st18), "db.ANTIQUES.aggregate([{$group:{_id:{\"BUYERID\":\"$BUYERID\"},\"total\":{$max:\"$PRICE\"}}}]);");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserkeygropByHaving() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql19 = "SELECT BUYERID, MAX(PRICE) AS total FROM ANTIQUES GROUP BY BUYERID HAVING (PRICE > 1000 and x=1);";
		Parser p19 = new Parser (sql19);
		try{
			Statement st19 = p19.processStatement();
			log.info("SQL:         " + st19.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st19) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st19), "db.ANTIQUES.aggregate([{$match:{\"PRICE\" :{$gt:1000},\"x\" :1}},{$group:{\"_id\":\"$BUYERID\"}},{$group:{_id:{\"BUYERID\":\"$BUYERID\"},\"total\":{$max:\"$PRICE\"}}}]);");

		}catch(Exception e){
			Assert.fail();
		}
	}

	// Tener cuidado si se quiere utilizar la palabra reservada COUNT, en ese caso se debe dejar como "count"
	@Test
	public void testParserCountAs() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql = "select count (*) AS Sensor FROM IndicesPrueba;";
		Parser psql= new Parser(sql);
		try{
			Statement sqls = psql.processStatement();
			log.info("SQL:        " + sql.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(sqls) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(sqls), "db.IndicesPrueba.aggregate([{$group:{\"_id\":null,Sensor:{$sum:1}}}]);");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserSelectKeySum() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql26 = "select sum (price) as total from orders;";
		Parser p26 = new Parser (sql26);
		try{
			Statement st26 = p26.processStatement();
			log.info("SQL:         " + st26.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st26) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st26), "db.orders.aggregate([{$group:{ _id: null,\"total\":{$sum:\"$price\"}}}]);");

		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserSelectSum() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql25 = "select state, sum (pop) as totalPop from zips;";
		Parser p25 = new Parser (sql25);
		try{
			Statement st25 = p25.processStatement();
			log.info("SQL:         " + st25.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st25) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st25), "db.zips.aggregate([{$group:{_id:{\"state\":\"$state\"},\"totalPop\":{$sum:\"$pop\"}}}]);");
			
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserCountGroupByHavingCount() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql25 = "select cust_id, count (*) from orders group by cust_id having count (*) > 1;";
		Parser p25 = new Parser (sql25);
		try{
			Statement st25 = p25.processStatement();
			log.info("SQL:         " + st25.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st25) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st25).trim(), "db.orders.aggregate([{$match:{count:{$gt:1}}},{$group:{\"_id\":\"$cust_id\",count:{$sum:1}}}]);");
			
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserWithDotCountGroupByHavingCount() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql25 = "select SensorTemperatura.identificador, sum(SensorTemperatura.medida) as total from SensorTemperatura group by SensorTemperatura.identificador;";
		Parser p25 = new Parser (sql25);
		try{
			Statement st25 = p25.processStatement();
			log.info("SQL:         " + st25.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st25) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st25).trim(), "db.SensorTemperatura.aggregate([{$group:{_id:{\"SensorTemperatura\":\"$SensorTemperatura.identificador\"},\"total\":{$sum:\"$SensorTemperatura.medida\"}}}]);");
			
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserMaxCountGroupByHavingCount() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql25 = "select max(SensorTemperatura.medida) as total from SensorTemperatura group by SensorTemperatura.identificador;";
		Parser p25 = new Parser (sql25);
		try{
			Statement st25 = p25.processStatement();
			log.info("SQL:         " + st25.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st25) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st25).trim(), "db.SensorTemperatura.aggregate([{$group:{_id:\"$SensorTemperatura.identificador\",\"total\":{$max:\"$SensorTemperatura.medida\"}}}]);");
			
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	
	@Test
	public void testParserTimestamp() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql25 = "select * from feedForecastMeteorologico where Feed.assetId = \"Depuradora de Bens\" and Feed.type = \"FORECAST\" and Feed.measuresTimestamp >= 'ISODate(\"2014-07-17T12:00:00Z\")' and Feed.measuresTimestamp < 'ISODate(\"2014-07-17T22:00:00Z\")' limit 1 order by Feed.timestamp DESC;";

		Parser p25 = new Parser (sql25);
		try{
			Statement st25 = p25.processStatement();
			log.info("SQL:         " + st25.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st25) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st25).trim(), 
			"db.feedForecastMeteorologico.find({\"Feed.assetId\" :\"Depuradora de Bens\",\"Feed.type\" :\"FORECAST\",\"Feed.measuresTimestamp\" :{$gte:ISODate(\"2014-07-17T12:00:00Z\")},\"Feed.measuresTimestamp\" :{$lt:ISODate(\"2014-07-17T22:00:00Z\")}}).sort({\"Feed.timestamp\":-1}).limit(1);");

			
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserTimestamp2() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql25 = "select * from feedForecastMeteorologico where Feed.measuresTimestamp >= 'ISODate(\"2014-07-17T12:00:00Z\")' and Feed.measuresTimestamp < 'ISODate(\"2014-07-17T22:00:00Z\")' limit 1 order by Feed.timestamp DESC;";

		Parser p25 = new Parser (sql25);
		try{
			Statement st25 = p25.processStatement();
			log.info("SQL:         " + st25.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st25) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st25).trim(), "db.feedForecastMeteorologico.find({\"Feed.measuresTimestamp\" :{$gte:ISODate(\"2014-07-17T12:00:00Z\")},\"Feed.measuresTimestamp\" :{$lt:ISODate(\"2014-07-17T22:00:00Z\")}}).sort({\"Feed.timestamp\":-1}).limit(1);");
			
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserTimestamp3() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql25 = "select * from feedForecastMeteorologico where Feed.assetId = \"Depuradora de Bens\" and Feed.type = \"FORECAST\" and (Feed.measuresTimestamp >= 'ISODate(\"2014-07-17T12:00:00Z\")' or Feed.measuresTimestamp < 'ISODate(\"2014-07-17T22:00:00Z\")') limit 1 order by Feed.timestamp DESC;";

		Parser p25 = new Parser (sql25);
		try{
			Statement st25 = p25.processStatement();
			log.info("SQL:         " + st25.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st25) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st25).trim(), 
			"db.feedForecastMeteorologico.find({\"Feed.assetId\" :\"Depuradora de Bens\",\"Feed.type\" :\"FORECAST\",$or:[{\"Feed.measuresTimestamp\" :{$gte:ISODate(\"2014-07-17T12:00:00Z\")}},{\"Feed.measuresTimestamp\" :{$lt:ISODate(\"2014-07-17T22:00:00Z\")}}]}).sort({\"Feed.timestamp\":-1}).limit(1);");

			
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testParserTimestamp4() throws ParseException {
		ParserSql parserToMogoDB = new ParserSqlToMongoDB();
		String sql25 = "select * from feedForecastMeteorologico where Feed.assetId = \"Depuradora de Bens\" and (Feed.measuresTimestamp >= 'ISODate(\"2014-07-17T12:00:00Z\")' or Feed.measuresTimestamp < 'ISODate(\"2014-07-17T22:00:00Z\")') limit 1 order by Feed.timestamp DESC;";

		Parser p25 = new Parser (sql25);
		try{
			Statement st25 = p25.processStatement();
			log.info("SQL:         " + st25.toString() + ";");
			log.info("MONGODB:     " + parserToMogoDB.postProcess(st25) + "\n");
			Assert.assertEquals(parserToMogoDB.postProcess(st25).trim(), 
			"db.feedForecastMeteorologico.find({\"Feed.assetId\" :\"Depuradora de Bens\",$or:[{\"Feed.measuresTimestamp\" :{$gte:ISODate(\"2014-07-17T12:00:00Z\")}},{\"Feed.measuresTimestamp\" :{$lt:ISODate(\"2014-07-17T22:00:00Z\")}}]}).sort({\"Feed.timestamp\":-1}).limit(1);");

			
		}catch(Exception e){
			Assert.fail();
		}
	}
	
}
