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
package com.indra.sofia2.support.util.plugins.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.indra.sofia2.support.util.plugins.interfaces.events.EventPlugin;

@Component
public class EventPluginManager {
	
	@Autowired
	private ApplicationContext applicationContext;
		
	private Map<String, EventPlugin> pluginsImplementation = new HashMap<String, EventPlugin>();
	private Map<String, Boolean> inicializedImplementation = new HashMap<String, Boolean>();
	
	public void receiveresultEvent(String resultEventName, long timeStamp, List<String> returnNames, List<Map<String, Object>> inEvents, List<Map<String, Object>> removeEvents){
		Boolean inicializado = inicializedImplementation.get(resultEventName);
		if (inicializado==null){
			//Nunca se habia preguntado por el pugin de este evento, lo inicializamos
			inicializedImplementation.put(resultEventName, true);
			EventPlugin plugin=null;
			try{
				plugin = applicationContext.getBean(resultEventName.toUpperCase()+"_CEP", EventPlugin.class);
			}catch (Exception e){
			}
			pluginsImplementation.put(resultEventName, plugin);
		}
		//Recuperamos el plugin del Map
		EventPlugin plugin = pluginsImplementation.get(resultEventName);
		if (plugin!=null){
			plugin.receive(resultEventName, timeStamp, inEvents, removeEvents);
		}	
	}
	
	public boolean isRunning() {
		return true;
	}

}
