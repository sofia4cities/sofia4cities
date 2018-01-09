/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MongoDbCredentials {
	
	@Value("${sofia2.database.mongodb.authenticationDatabase:admin}")
	@Getter @Setter private String authenticationDatabase;

	@Value("${sofia2.database.mongodb.useAuth:false}")
	@Getter @Setter private boolean enableMongoDbAuthentication;

	@Value("${sofia2.database.mongodb.username:username}")
	@Getter @Setter private String username;

	@Value("${sofia2.database.mongodb.password:password}")
	@Getter @Setter private String password;
	
	@PostConstruct
	public void init() {
		if (authenticationDatabase.isEmpty() || username.isEmpty() || password.isEmpty()) {
			log.warn(
					"The authentication database, the username or the password has not been specified. MongoDB authentication will be DISABLED.");
			enableMongoDbAuthentication = false;
		}
	}

}
