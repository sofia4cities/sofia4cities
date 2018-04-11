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
import java.util.Scanner;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.config.model.DigitalTwinDevice;
import com.indracompany.sofia2.config.model.PropertyDigitalTwinType;
import com.indracompany.sofia2.config.repository.DigitalTwinDeviceRepository;
import com.indracompany.sofia2.config.repository.PropertyDigitalTwinTypeRepository;
import com.indracompany.sofia2.config.services.utils.ZipUtil;

import freemarker.cache.ClassTemplateLoader;
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
	
	@Value("${digitaltwin.maven.exec.path}")
	private String mavenExecPath;
	
	private Template digitalTwinStatusTemplate;
	private Template pomTemplate;
	private Template deviceApplicationTemplate;
	private Template deviceConfigurationTemplate;
	
	@PostConstruct
	public void init() {
		Configuration cfg  = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		try {
			TemplateLoader templateLoader = new ClassTemplateLoader(getClass(), "/digitaltwin/templates");
			
			cfg.setTemplateLoader(templateLoader);
			digitalTwinStatusTemplate = cfg.getTemplate("DigitalTwinStatusTemplate.ftl");
			pomTemplate = cfg.getTemplate("pomTemplate.ftl");
			deviceApplicationTemplate = cfg.getTemplate("DeviceApplicationTemplate.ftl");
			deviceConfigurationTemplate = cfg.getTemplate("applicationPropertiesTemplate.ftl");
		} catch (IOException e) {
			log.error("Error configuring the template loader.", e);
		}
	}
	
	
	public File generateProject(String identificacion, Boolean compile) {
		
		List<PropertiesDTO> properties = new ArrayList<PropertiesDTO>();
		List<PropertiesDTO> statusProperties=new ArrayList<PropertiesDTO>();
		List<String> inits = new ArrayList<String>();
		List<PropertiesDTO> cls = new ArrayList<PropertiesDTO>();
		
		DigitalTwinDevice device = digitalTwinDeviceRepo.findByIdentification(identificacion);
		List<PropertyDigitalTwinType> propsDigitalTwin = propDigitalTwinTypeRepo.findByTypeId(device.getTypeId());
		String logic = device.getLogic();
		
		if(logic.startsWith("\"")) {
			logic = logic.substring(1,logic.length()-1);
			logic = logic.replace("\\\"","\"");
		}
		
		String projectDirectory=tempDir+File.separator+UUID.randomUUID();
		
		File src = new File(projectDirectory + File.separator + device.getIdentification() + File.separator +"src" + File.separator + "main");
		if(!src.exists()) {
			Boolean success = src.mkdirs();
			if(!success) {
				log.error("Creating project for Digital Twin Device falied");
				return null;
			}
		}else {
			log.error("Creating project for Digital Twin Device falied, the temporary directory don't exist: " + src.getAbsolutePath());
			return null;
		}
		
		for(PropertyDigitalTwinType prop : propsDigitalTwin) {
			properties.add(new PropertiesDTO(prop.getName(), GeneratorJavaTypesMapper.mapPropertyName(prop.getType())));
			statusProperties.add(new PropertiesDTO(prop.getName(), GeneratorJavaTypesMapper.mapPropertyName(prop.getType())));
			properties.add(new PropertiesDTO("operation"+prop.getName().substring(0, 1).toUpperCase() + prop.getName().substring(1), "OperationType"));
			inits.add("setOperation" + prop.getName().substring(0, 1).toUpperCase() + prop.getName().substring(1) + "(OperationType."+ prop.getDirection().toUpperCase() +");");
			cls.add(new PropertiesDTO(prop.getName(), GeneratorJavaTypesMapper.mapPropertyName(prop.getType())));
		}
		
		//Status Template properties
		Map<String, Object> dataStatusMap=new HashMap<String, Object>();
		dataStatusMap.put("properties", properties);
		dataStatusMap.put("statusProperties", statusProperties);
		dataStatusMap.put("inits", inits);
		dataStatusMap.put("package", "digitaltwin.device.status;");
		dataStatusMap.put("mapClass", cls);
		
		Map<String, Object> dataApplicationPropertiesMap=new HashMap<String, Object>();
		dataApplicationPropertiesMap.put("serverPort", "10000");
		dataApplicationPropertiesMap.put("serverContextPath", device.getContextPath());
		dataApplicationPropertiesMap.put("applicationName", identificacion);
		dataApplicationPropertiesMap.put("apiKey", device.getDigitalKey());
		dataApplicationPropertiesMap.put("deviceId", device.getIdentification());
		dataApplicationPropertiesMap.put("deviceRestLocalSchema", device.getUrlSchema());
		dataApplicationPropertiesMap.put("deviceRestLocalIp", device.getIp());
		dataApplicationPropertiesMap.put("sofia2BrokerEndpoint", device.getUrl());
		
		
		//pom.xml Template properties
		Map<String, Object> dataPomMap=new HashMap<String, Object>();
		dataPomMap.put("projectName", identificacion);
		
		
		
		Writer writerDeviceApplication=null;
		Writer writerTwinStatus=null;
		Writer writerApplicationProperties=null;
		Writer writerPom=null;
		PrintWriter outLogic=null;
		File zipFile = null;
		
		try {
			zipFile = File.createTempFile(device.getIdentification(), ".zip");
			
			//Create DeviceApplication.java
			log.info("New file is going to be generate on: " + src.getAbsolutePath() + File.separator + "java" + File.separator + "digitaltwin" + File.separator + "device");
			File app = new File(src.getAbsolutePath() + File.separator + "java" + File.separator + "digitaltwin" + File.separator + "device");
			if(!app.isDirectory()) {
				app.mkdirs();
			}
			writerDeviceApplication = new FileWriter (app + File.separator + "DeviceApplication.java");
			deviceApplicationTemplate.process(new HashMap(), writerDeviceApplication);
			writerDeviceApplication.flush();
			
			//Create DigitalTwinStatus.java
			File fileJava = new File(app + File.separator + "status");
			if(!fileJava.isDirectory()) {
				fileJava.mkdirs();
			}
			writerTwinStatus = new FileWriter (fileJava + File.separator + "DigitalTwinStatus.java");
			digitalTwinStatusTemplate.process(dataStatusMap, writerTwinStatus);
			writerTwinStatus.flush();
			
			//Create logic.js
			File fileStatic = new File(src.getAbsolutePath() + File.separator + "resources" + File.separator  + "static" + File.separator + "js");
			if(!fileStatic.isDirectory()) {
				fileStatic.mkdirs();
			}
			outLogic = new PrintWriter(fileStatic + File.separator + "logic.js");
			outLogic.println(logic.replace("\\n", System.getProperty("line.separator")));
			outLogic.flush();
			
			
			//Create application.yml
			writerApplicationProperties = new FileWriter (src.getAbsolutePath() + File.separator + "resources" + File.separator + "application.yml");
			deviceConfigurationTemplate.process(dataApplicationPropertiesMap, writerApplicationProperties);
			writerApplicationProperties.flush();
			
			
			//Create pom.xml
			writerPom = new FileWriter (projectDirectory + File.separator + device.getIdentification() + File.separator +"pom.xml");
			pomTemplate.process(dataPomMap, writerPom);
			writerPom.flush();
		
		}catch (Exception e) {
			log.error("Error generating class DigitalTwinStatus.java", e);
		}
		finally{
			try {
				if(null!=writerDeviceApplication) {
					writerDeviceApplication.close();
				}
			} catch (Exception e) {
				log.error("Error closing File object", e);
			}
			
			try {
				if(null!=writerTwinStatus) {
					writerTwinStatus.close();
				}
			} catch (Exception e) {
				log.error("Error closing File object", e);
			}
			
			try {
				if(null!=writerApplicationProperties) {
					writerApplicationProperties.close();
				}
			} catch (Exception e) {
				log.error("Error closing File object", e);
			}
			
			try {
				if(null!=writerPom) {
					writerPom.close();
				}
			} catch (Exception e) {
				log.error("Error closing File object", e);
			}
			
			try {
				if(null!=outLogic) {
					outLogic.close();
				}
			} catch (Exception e) {
				log.error("Error closing File object", e);
			}

		}
		if(compile) {
			this.buildProjectMaven(projectDirectory+File.separator+device.getIdentification());
		}
		
		File fileProjectDirectory = new File(projectDirectory);
		try {
			zipUtil.zipDirectory(fileProjectDirectory, zipFile);
		} catch (IOException e) {
			log.error("Zip file deviceTwin failed", e);
		}
		
		
		//Removes the directory
		//TODO
//		this.deleteDirectory(fileProjectDirectory);
		
		return zipFile;
	}
	
	private boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	
	private void buildProjectMaven(String projectPath) {
		try {
			File pathToExecutable = new File(mavenExecPath);
			ProcessBuilder builder = new ProcessBuilder( pathToExecutable.getAbsolutePath(), "clean", "package");
			File workingDirectory=new File(projectPath);
			log.info("Sets working directory: {}", workingDirectory);
			log.info("Absolute path: {}", workingDirectory);
			
			builder.directory( workingDirectory); // this is where you set the root folder for the executable to run with
			builder.redirectErrorStream(true);
			Process process =  builder.start();
	
			Scanner s = new Scanner(process.getInputStream());
			StringBuilder text = new StringBuilder();
			while (s.hasNextLine()) {
			  text.append(s.nextLine());
			  text.append("\n");
			}
			s.close();
	
			int result = process.waitFor();
			//TODO --> Borrar
			System.out.println(text);
			log.info( "Process exited with result %d and output %s%n", result, text );
		}catch(Exception e) {
			log.error("Error compiling project", e);
		}
		
		
		
	}
}
