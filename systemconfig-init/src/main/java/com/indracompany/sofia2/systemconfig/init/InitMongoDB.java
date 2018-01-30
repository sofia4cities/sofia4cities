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
package com.indracompany.sofia2.systemconfig.init;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;
import com.indracompany.sofia2.persistence.interfaces.ManageDBRepository;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Luis Miguel Gracia
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sofia2.init.mongodb")
public class InitMongoDB {

	@Autowired
	ManageDBRepository manageDb;

	@Autowired
	BasicOpsDBRepository basicOps;

	@PostConstruct
	public void init() {
		init_AuditGeneral();
	}

	public void init_AuditGeneral() {
		log.info("init AuditGeneral");
		/*
		 * db.createCollection("AuditGeneral"); db.AuditGeneral.createIndex({type: 1});
		 * db.AuditGeneral.createIndex({user: 1});
		 * db.AuditGeneral.createIndex({ontology: 1}); db.AuditGeneral.createIndex({kp:
		 * 1});
		 */
		if (manageDb.getListOfTables4Ontology("AuditGeneral").isEmpty()) {
			try {
				log.info("No Collection AuditGeneral...");
				manageDb.createTable4Ontology("AuditGeneral", "{}");
				manageDb.createIndex("AuditGeneral", "type");
				manageDb.createIndex("AuditGeneral", "user");
				manageDb.createIndex("AuditGeneral", "ontology");
				manageDb.createIndex("AuditGeneral", "kp");
			} catch (Exception e) {
				log.error("Error init_AuditGeneral:" + e.getMessage());
				manageDb.removeTable4Ontology("AuditGeneral");
			}
		}
	}

}
