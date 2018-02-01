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
package com.indracompany.sofia2.persistence.mongodb;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.persistence.mongodb.quasar.connector.QuasarMongoDBbHttpConnector;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Luis Miguel Gracia
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
// @ContextConfiguration(classes = EmbeddedMongoConfiguration.class)
public class MongoByQuasarIntegrationTest {

	@Autowired
	QuasarMongoDBbHttpConnector connector;

	@Test
	public void testQueryAsJson() {
		try {
			String query = "select * from movie";
			String result = connector.queryAsJson(query, 0, 100);
			JSONArray jsonResult = new JSONArray(result);
			Assert.assertTrue(jsonResult.length() > 0);
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB by Quasar. " + e);
		}
	}

	// @Test
	public void testQueryAsTable() {
		try {
			String query = "select * from movie";
			String result = connector.queryAsTable(query, 0, 100);

			Assert.assertTrue(result.indexOf("|") != -1);
		} catch (Exception e) {
			Assert.fail("No connection with MongoDB by Quasar. " + e);
		}
	}

}
