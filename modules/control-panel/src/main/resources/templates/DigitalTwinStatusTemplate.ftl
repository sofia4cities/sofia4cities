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
package ${package}
import java.lang.reflect.Method;
import java.util.Properties;
import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.indracompany.sofia2.digitaltwin.status.IDigitalTwinStatus;
import com.indracompany.sofia2.digitaltwin.property.controller.OperationType;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DigitalTwinStatus implements IDigitalTwinStatus{
	
	<#list properties as property>
		@Getter
		@Setter
		private ${property.type} ${property.name};
	</#list>

	@PostConstruct
	public void init() {
		//Init Operation types values
		<#list inits as init>
			${init}
		</#list>
	}

	@Override
	public Boolean validate(OperationType operationType, String property) {
		try {
			Class cls = Class.forName(DigitalTwinStatus.class.getName());
			Method method = cls.getDeclaredMethod("getOperation"+property.substring(0, 1).toUpperCase() + property.substring(1), null);
			OperationType operation = (OperationType) method.invoke(this,null);
			
			if(operation.equals(operationType) || operation.equals(OperationType.IN_OUT)) {
				return true;
			}
			return false;
		}catch (Exception e) {
			log.error("Validation of property "+ property + " failed.", e);
			return false;
		} 
	}

	@Override
	public String getProperty(String property) {
		try {
			Class cls = Class.forName(DigitalTwinStatus.class.getName());
			Method method = cls.getMethod("get"+property.substring(0, 1).toUpperCase() + property.substring(1), null);
			
			return (String) method.invoke(this, new Class[]{});

		}catch (Exception e) {
			log.error("get property "+ property + " failed.", e);
			return null;
		} 
	}

	@Override
	public void setProperty(String property, String value) {
		try {
			Class cls = Class.forName(DigitalTwinStatus.class.getName());
			
			Method method = cls.getMethod("set"+property.substring(0, 1).toUpperCase() + property.substring(1), String.class);
			method.invoke(this, value);

		}catch (Exception e) {
			log.error("set property "+ property + " failed.", e);
		} 
		
	}
	
	@Override
	public Properties toProperties() {
		Properties properties = new Properties();
		
		<#list properties as property>
			properties.put("${property.name}",${property.name});
		</#list>
		
		return properties;
	}
	
}
