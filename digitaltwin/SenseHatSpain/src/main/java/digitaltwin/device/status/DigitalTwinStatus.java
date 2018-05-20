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
package digitaltwin.device.status;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.indracompany.sofia2.digitaltwin.property.controller.OperationType;
import com.indracompany.sofia2.digitaltwin.status.IDigitalTwinStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DigitalTwinStatus implements IDigitalTwinStatus {
	@Getter
	@Setter
	private Double pressure;
	@Getter
	@Setter
	private OperationType operationPressure;
	@Getter
	@Setter
	private Double temperature;
	@Getter
	@Setter
	private OperationType operationTemperature;

	@Getter
	@Setter
	private OperationType operationJoystick;
	@Getter
	@Setter
	private Double humidity;
	@Getter
	@Setter
	private OperationType operationHumidity;

	private Map<String, Class> mapClass;

	@PostConstruct
	public void init() {
		// Init Operation types values
		setOperationPressure(OperationType.OUT);
		setOperationTemperature(OperationType.OUT);
		setOperationHumidity(OperationType.OUT);

		mapClass = new HashMap<String, Class>();
		mapClass.put("temperature", Double.class);
		mapClass.put("humidity", Double.class);
		mapClass.put("pressure", Double.class);
	}

	@Override
	public Boolean validate(OperationType operationType, String property) {
		try {
			Class cls = Class.forName(DigitalTwinStatus.class.getName());
			Method method = cls.getDeclaredMethod(
					"getOperation" + property.substring(0, 1).toUpperCase() + property.substring(1), null);
			OperationType operation = (OperationType) method.invoke(this, null);

			if (operation.equals(operationType) || operation.equals(OperationType.IN_OUT)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			log.error("Validation of property " + property + " failed.", e);
			return false;
		}
	}

	@Override
	public Object getProperty(String property) {
		try {
			Class cls = Class.forName(DigitalTwinStatus.class.getName());
			Method method = cls.getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1), null);

			return method.invoke(this, new Class[] {});

		} catch (Exception e) {
			log.error("get property " + property + " failed.", e);
			return null;
		}
	}

	@Override
	public void setProperty(String property, Object value) {
		try {

			// Class cls = Class.forName(DigitalTwinStatus.class.getName());
			Class cls = this.getClass();
			log.info("cls: " + cls);
			log.info("get Class for property: " + mapClass.get(property));
			log.info("setProperty: " + property + " - value: " + value);

			Method method = cls.getMethod("set" + property.substring(0, 1).toUpperCase() + property.substring(1),
					mapClass.get(property));
			method.invoke(this, mapClass.get(property).cast(value));

		} catch (Exception e) {
			log.error("set property " + property + " failed.", e);
		}

	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("pressure", pressure);
		properties.put("temperature", temperature);
		properties.put("humidity", humidity);

		return properties;
	}

}
