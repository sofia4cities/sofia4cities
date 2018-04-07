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
package com.indracompany.sofia2.digitaltwin.action.execute;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.digitaltwin.status.IDigitalTwinStatus;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ActionExecutor {
	
	@Autowired
	private IDigitalTwinStatus digitalTwinStatus;
	
	public void executeAction(String name) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		Invocable invocable = (Invocable) engine;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			engine.eval(new FileReader(classLoader.getResource("static/js/logic.js").getFile()));
			
			invocable.invokeFunction("onAction"+name.substring(0, 1).toUpperCase() + name.substring(1), 
					digitalTwinStatus.toMap());
		}catch(ScriptException e1) {
			log.error("Execution logic for action " + name + " failed", e1);
		}catch(NoSuchMethodException e2) {
			log.error("Action " + name + " not found", e2);
		} catch (FileNotFoundException e) {
			log.error("File logic.js not found.", e);
		}
	}

}
