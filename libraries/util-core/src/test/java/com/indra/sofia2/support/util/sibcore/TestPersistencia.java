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
package com.indra.sofia2.support.util.sibcore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.indra.sofia2.support.bbdd.sib.persistence.nativedb.disk.DAOMongoDBImpl;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations ={"classpath:/META-INF/spring/applicationContext.xml"})
public class TestPersistencia {
	
	@Autowired
    @Qualifier("DAODiskDBTR")
	DAOMongoDBImpl dao;
	
	private static Log log = LogFactory.getLog(TestPersistencia.class);
	
	@Test
	public void test() {
		try{
			log.info(dao.findNativeQuery("db.prueba.find({birth:'Dec 03, 1924'})"));
			//dao.remove(null, "prueba", null);
			log.info("**************************INSERTO TENIDO EN CUENTA EL ID Y SIN EL*********************************");
			String id = dao.insert("prueba", "name: { first: 'Pedro', last: 'Toribio' },birth: 'Dec 03, 1924'");
			log.info("ID DEVUELTO .:"+id);
			log.info(dao.find("prueba",id));
			id = dao.insert("prueba", id+",name: { first: 'Pedro', last: 'Toribio' },birth: 'Dec 03, 1924'");
			log.info("ID DEVUELTO .:"+id);
			log.info(dao.findAll("prueba"));
			log.info(dao.find("prueba",id));
			id = dao.insert("prueba", "name: { first: 'Pedro', last: 'Toribio' },birth: 'Dec 03, 1924'");
			log.info("ID DEVUELTO .:"+id);
			log.info(dao.find("prueba",id));
			log.info("*******************************AHORA MUESTRO EL ULTIMO VALOR*************************************");
			id = dao.insert("prueba", "name: { first: 'Pedro', last: 'Toribio' },birth: 'Dec 03, 1924'");
			log.info(dao.find("prueba","{name.first: 'Pedro'}"));
			id = dao.insert("prueba", "name: { first: 'Pedro', last: 'Toribio' },birth: 'Dec 03, 1924'");
			log.info(dao.find("prueba","{name.first: 'Pedro'}"));
			id = dao.insert("prueba", "name: { first: 'Pedro', last: 'Toribio' },birth: 'Dec 03, 1924'");
			log.info(dao.find("prueba","{name.first: 'Pedro'}"));
			id = dao.insert("prueba", "name: { first: 'Pedro', last: 'Toribio' },birth: 'Dec 03, 1924'");
			log.info(dao.find("prueba","{name.first: 'Pedro'}"));
			id = dao.insert("prueba", "name: { first: 'Pedro', last: 'Toribio' },birth: 'Dec 03, 1924'");
			log.info(dao.find("prueba","{name.first: 'Pedro'}"));
			log.info("***************************************ACTUALIZO TODOS******************************************");
			log.info(dao.findAll("prueba"));
			dao.update("prueba","{name.first: 'Pedro'}", "{$set: { name.first: 'Joselito' }}" );
			log.info(dao.findAll("prueba"));
			log.info("***************************************ACTUALIZO EL ULTIMO***************************************");
			dao.update("prueba","{name.first: 'Joselito'}", "{$set: { name.first: 'Pedro' }}" );
			log.info(dao.findAll("prueba"));
			dao.insert("prueba", "{name: { first: 'Pedro', last: 'Toribio' },birth: 'Dec 03, 1924'}");
			log.info(dao.findAll("prueba"));
			dao.remove("prueba", "{name.first: 'Joselito'}");
			log.info(dao.findAll("prueba"));
			dao.insert("prueba", "{name: { first: 'Marcos', last: 'Toribio' },birth: 'Dec 03, 1924'}");
			log.info(dao.findAll("prueba"));
			log.info(dao.find("prueba","{name.first: 'Pedro'}"));
			log.info(dao.find("prueba","{name.first: 'Marcos'}"));
			log.info(dao.find("prueba","{name.first: 'Joselito'}"));
			
			dao.find("prueba","{name.first: 'Pedro'}"); 
			
			log.info(dao.findAll("prueba"));
		}catch (Throwable e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
	}

}
