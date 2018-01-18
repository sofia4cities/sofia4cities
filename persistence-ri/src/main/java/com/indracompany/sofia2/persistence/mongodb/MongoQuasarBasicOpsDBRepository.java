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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.interfaces.BasicOpsQuasarDBRepository;
import com.indracompany.sofia2.persistence.quasar.connector.QuasarDbHttpConnector;
import com.indracompany.sofia2.persistence.quasar.connector.dto.QuasarResponseDTO;
import com.indracompany.sofia2.ssap.SSAPQueryResultFormat;

import lombok.extern.slf4j.Slf4j;

@Component("MongoQuasarBasicOpsDBRepository")
@Scope("prototype")
@Lazy
@Slf4j
public class MongoQuasarBasicOpsDBRepository implements BasicOpsQuasarDBRepository{
	
	@Autowired
	private QuasarDbHttpConnector connector;
	
	@Value("${sofia2.database.mongodb.quasar.queries.defaultLimit:100}")
	private int maxRecordsLimit;
	
	@Override
	public QuasarResponseDTO executeQuery(String query, int offset, SSAPQueryResultFormat resultType,
			/*UserCDB user,*/ String formatter) throws Exception {
		try {
			String accept;
			switch(resultType){
				case TABLE: accept=ACCEPT_TEXT_CSV;
							break;
				case JSON:
				default:	accept=ACCEPT_APPLICATION_JSON;
			}
			
			return this.connector.query(query, offset, this.maxRecordsLimit, accept/*, user*/, formatter);
		
		} catch (Exception e) {
			log.error("Error executing query in Quasar: "+query, e);
			throw e;
		}
	}

}
