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
package com.indracompany.sofia2.controlpanel.helper.digitaltwin.device;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.DigitalTwinDevice;
import com.indracompany.sofia2.config.model.PropertyDigitalTwinType;
import com.indracompany.sofia2.config.repository.DigitalTwinDeviceRepository;
import com.indracompany.sofia2.config.repository.PropertyDigitalTwinTypeRepository;
import com.indracompany.sofia2.config.services.utils.ZipUtil;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DigitalTwinDeviceHelper {
	
	@Autowired
	private DigitalTwinDeviceRepository digitalTwinDeviceRepo;
	
	@Autowired
	private PropertyDigitalTwinTypeRepository propDigitalTwinTypeRepo;
	
	@Autowired
	private ZipUtil zipUtil;
	
	@Value("${digitaltwin.temp.dir}")
	private String tempDir;
	
	@Value("${digitaltwin.src.main}")
	private String srcPath;
	
	@Value("${digitaltwin.src.app}")
	private String appPath;
	
	private Template digitalTwinStatusTemplate;
	private Template pomTemplate;
	private Template deviceApplicationTemplate;
	private Map<String, Object> dataMap;
	private Configuration cfg;
	
	@PostConstruct
	public void init() {
		dataMap = new HashMap<String, Object>();
		cfg = new Configuration();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			TemplateLoader templateLoader = new FileTemplateLoader(new File(classLoader.getResource("templates").getFile()));
			cfg.setTemplateLoader(templateLoader);
			digitalTwinStatusTemplate = cfg.getTemplate("DigitalTwinStatusTemplate.ftl");
			pomTemplate = cfg.getTemplate("pomTemplate.ftl");
			deviceApplicationTemplate = cfg.getTemplate("DeviceApplicationTemplate.ftl");
		} catch (IOException e) {
			log.error("Error configuring the template loader.", e);
		}
	}
	
	public File generateProject(String identificarion) {

		File zipFile = null;
		
		List<PropertiesDTO> properties = new ArrayList<PropertiesDTO>();
		List<String> inits = new ArrayList<String>();
		List<PropertiesDTO> cls = new ArrayList<PropertiesDTO>();
		
		DigitalTwinDevice device = digitalTwinDeviceRepo.findByIdentification(identificarion);
		List<PropertyDigitalTwinType> propsDigitalTwin = propDigitalTwinTypeRepo.findByTypeId(device.getTypeId());
		String logic = device.getLogic();
		
		if(logic.startsWith("\"")) {
			logic = logic.substring(1,logic.length()-1);
			logic = logic.replace("\\\"","\"");
		}
		
		File src = new File(tempDir + srcPath);
		if(!src.isDirectory()) {
			Boolean success = src.mkdirs();
			if(!success) {
				log.error("Creating project for Digital Twin Device falied");
				return null;
			}
		}
		
		for(PropertyDigitalTwinType prop : propsDigitalTwin) {
			properties.add(new PropertiesDTO(prop.getName(), prop.getType().substring(0, 1).toUpperCase() + prop.getType().substring(1)));
			properties.add(new PropertiesDTO("operation"+prop.getName().substring(0, 1).toUpperCase() + prop.getName().substring(1), "OperationType"));
			inits.add("setOperation" + prop.getName().substring(0, 1).toUpperCase() + prop.getName().substring(1) + "(OperationType."+ prop.getDirection().toUpperCase() +");");
			cls.add(new PropertiesDTO(prop.getName(), prop.getType().substring(0, 1).toUpperCase() + prop.getType().substring(1)));
		}
		
		dataMap.put("properties", properties);
		dataMap.put("inits", inits);
		dataMap.put("package", "digitaltwin.device.status;");
		dataMap.put("mapClass", cls);
		
		Writer writer=null;
		PrintWriter out=null;
		try {
			zipFile = File.createTempFile("deviceTwin", ".zip");
			
			//Create DeviceApplication.java
			File app = new File(src.getAbsolutePath() + File.separator + appPath);
			if(!app.isDirectory()) {
				app.mkdirs();
			}
			writer = new FileWriter (app + File.separator + "DeviceApplication.java");
			deviceApplicationTemplate.process(new HashMap(), writer);
			writer.flush();
			
			//Create DigitalTwinStatus.java
			File fileJava = new File(app + File.separator + "status");
			if(!fileJava.isDirectory()) {
				fileJava.mkdirs();
			}
			writer = new FileWriter (fileJava + File.separator + "DigitalTwinStatus.java");
			digitalTwinStatusTemplate.process(dataMap, writer);
			writer.flush();
			
			//Create logic.js
			File fileStatic = new File(src.getAbsolutePath() + File.separator + "resources" + File.separator  + "static" + File.separator + "js");
			if(!fileStatic.isDirectory()) {
				fileStatic.mkdirs();
			}
			out = new PrintWriter(fileStatic + File.separator + "logic.js");
			out.println(logic.replace("\\n", System.getProperty("line.separator")));
			out.flush();
			
			//Create application.properties
			out = new PrintWriter(src.getAbsolutePath() + File.separator + "resources" + File.separator + "application.properties");
			StringBuilder str = new StringBuilder();
			str.append("api.key:" + device.getApiKey());
			str.append(System.getProperty("line.separator"));
			str.append("url:" + device.getUrl());			
			out.println(str.toString());
			out.flush();
			
			//Create pom.xml
			writer = new FileWriter (tempDir + File.separator + "devicetwin" + File.separator +"pom.xml");
			pomTemplate.process(new HashMap(), writer);
			writer.flush();
		}catch (Exception e) {
			log.error("Error generating class DigitalTwinStatus.java", e);
		}
		finally{
			try {
				writer.close();
				out.close();
			} catch (IOException e) {
				log.error("Error closing File object", e);
			}
		}
		
		try {
			zipUtil.zipDirectory(new File(tempDir), zipFile);
		} catch (IOException e) {
			log.error("Zip file deviceTwin failed", e);
		}
		
		return zipFile;
	}
}
