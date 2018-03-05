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
