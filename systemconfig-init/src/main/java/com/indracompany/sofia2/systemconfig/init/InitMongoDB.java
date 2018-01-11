package com.indracompany.sofia2.systemconfig.init;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class InitMongoDB {
	

	@Value("${sofia2.init.mongodb:false}")
	private boolean initMongoDB;
	
	@Autowired
	ManageDBRepository manageDb;
	
	@Autowired
	BasicOpsDBRepository basicOps;
	


	@PostConstruct
	public void init() {
		if (initMongoDB==true) {
			log.info("Start initMongoDB...");
			init_AuditGeneral();
		}
		else {
			log.info("Disable Start initMongoDB...");
		}
		//init_MensajesPlataforma();
	}
	
	public void init_AuditGeneral() {
		log.info("init AuditGeneral");
		/*
		 db.createCollection("AuditGeneral");
			db.AuditGeneral.createIndex({type: 1});
			db.AuditGeneral.createIndex({user: 1});
			db.AuditGeneral.createIndex({ontology: 1});
			db.AuditGeneral.createIndex({kp: 1});
		 */
		if (manageDb.getListOfTables4Ontology("AuditGeneral").isEmpty()) {
			try {
				log.info("No Collection AuditGeneral...");
				manageDb.createTable4Ontology("AuditGeneral", "{}");
				manageDb.createIndex("AuditGeneral","type");
				manageDb.createIndex("AuditGeneral","user");
				manageDb.createIndex("AuditGeneral","ontology");
				manageDb.createIndex("AuditGeneral","kp");				
			} 
			catch (Exception e) {
				log.error("Error init_AuditGeneral:"+e.getMessage());
				manageDb.removeTable4Ontology("AuditGeneral");
			}
		}
	}
	 

}
