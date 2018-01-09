package com.indracompany.sofia2.persistence.mongodb;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.indracompany.sofia2.persistence.ContextData;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;

//import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;

@SpringBootApplication
//@EnableJpaAuditing
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class PersistenceRITestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersistenceRITestApplication.class, args);
	}
	
	private static final String MONGO_DB_URL = "localhost";
    private static final String MONGO_DB_NAME = "sofia";
    

	
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
}
