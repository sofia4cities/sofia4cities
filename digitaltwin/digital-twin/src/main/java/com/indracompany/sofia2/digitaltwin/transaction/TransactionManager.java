/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.digitaltwin.transaction;

import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.digitaltwin.action.execute.ActionExecutor;
import com.indracompany.sofia2.digitaltwin.status.IDigitalTwinStatus;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TransactionManager {
	
	@Autowired
	private ActionExecutor actionExecutor;
	
	@Autowired
	  private ApplicationContext applicationContext;
	
	private ConcurrentHashMap<String, Transaction> map;
	private ExecutorService executor;
	
	@PostConstruct
	public void init() {
		map = new ConcurrentHashMap<String, Transaction>();
		executor = Executors.newFixedThreadPool(1);
	}
	
	public void setProperty(String propertyName,String property, String idTransaction) {
		try {
			JSONObject propJSON = new JSONObject(property);
			String propertyValue = propJSON.getString(propertyName);
			
			Transaction transaction = map.get(idTransaction);
			if(transaction!=null) {
				
				//Get list of properties and add the new property
				Properties properties = transaction.getProperties();
				properties.put(propertyName, propertyValue);
				transaction.setProperties(properties);
				
				//Update map with the new property in the transaction object
				map.put(idTransaction, transaction);
			}else {
				//If doesn't exist a transaction on the hashmap, we will create it
				Properties properties = new Properties();
				properties.put(propertyName, propertyValue);
				map.put(idTransaction, new Transaction(properties));
			}
			
		} catch (JSONException e) {
			log.error("Invalid JSON property: " + property, e);
		}
	}
	
	public void executeSetproperty(String idTransaction, String action) {
		Transaction transaction = map.get(idTransaction);
		if(transaction!=null) {
			Properties properties = transaction.getProperties();
			executor.execute(new Runnable() {

				@Override
				public void run() {
					try {
						IDigitalTwinStatus digitalTwinStatus = applicationContext.getBean("digitalTwinStatus", IDigitalTwinStatus.class);
						Enumeration<?> e = properties.propertyNames();
						while(e.hasMoreElements()) {
							String name = (String) e.nextElement();
							digitalTwinStatus.setProperty(name, properties.getProperty(name));
						}
						map.remove(idTransaction);
						//Execute action
						actionExecutor.executeAction(action);
					} catch (Exception e) {
						log.error("Error setting transactional properties with idTransaction: " + idTransaction);
					}
				}
			});
		}else {
			//Execute action
			actionExecutor.executeAction(action);
		}

	}


}
