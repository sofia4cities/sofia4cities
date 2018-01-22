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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableJpaAuditing
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class PersistenceRITestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersistenceRITestApplication.class, args);
	}
	
	private static final String MONGO_DB_URL = "localhost";
    private static final String MONGO_DB_NAME = "sofia2_s4c";
    
    /*
	@Bean
    public MongoTemplate mongoTemplate() throws IOException {
        EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
        mongo.setBindIp(MONGO_DB_URL);
        mongo.setPort(27017);
//        mongo.setVersion("3.4");
        MongoClient mongoClient = mongo.getObject();        
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, MONGO_DB_NAME);
//        CollectionOptions options = new CollectionOptions(2018, -1, false);
//        
//        DBCollection a = mongoTemplate.createCollection("jjcollection", options);
//        ContextData data = new ContextData();
//        data.setClientConnection("12345");
//        data.setClientPatform("1234");
//        data.setClientSession("ssssdd");
//        data.setTimezoneId("dfdfd");
//        data.setUser("user");
//        mongoTemplate.save(data, "jjcollection");
//        CommandResult b = a.getStats();
//        boolean c = b.ok();
//        List<ContextData> result = mongoTemplate.findAll(ContextData.class, "jjcollection");
////        ContextData result = mongoTemplate.findOne(Query.query(Criteria.where("user").is("user")), ContextData.class);
//        String user = result.get(0).getUser();
//        System.out.println(user);
        //mongoTemplate.inse
        return mongoTemplate;
    }
    */
}
