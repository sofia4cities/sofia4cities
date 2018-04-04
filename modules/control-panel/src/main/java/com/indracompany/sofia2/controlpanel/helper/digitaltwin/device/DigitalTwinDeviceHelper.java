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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
	
	private Template template;
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
			template = cfg.getTemplate("DigitalTwinStatusTemplate.ftl");
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
			out = new PrintWriter (app + File.separator + "DeviceApplication.java");
			out.println("package digitaltwin.device;\r\n" + 
					"\r\n" + 
					"import org.springframework.boot.SpringApplication;\r\n" + 
					"import org.springframework.boot.autoconfigure.SpringBootApplication;\r\n" + 
					"\r\n" + 
					"@SpringBootApplication(scanBasePackages=\"com.indracompany.sofia2, digitaltwin.device\")\r\n" + 
					"public class DeviceApplication {\r\n" + 
					"\r\n" + 
					"	public static void main(String[] args) {\r\n" + 
					"		SpringApplication.run(DeviceApplication.class, args);\r\n" + 
					"	}\r\n" + 
					"}\r\n" + 
					"");
			out.flush();
			
			//Create DigitalTwinStatus.java
			File fileJava = new File(app + File.separator + "status");
			if(!fileJava.isDirectory()) {
				fileJava.mkdirs();
			}
			writer = new FileWriter (fileJava + File.separator + "DigitalTwinStatus.java");
			template.process(dataMap, writer);
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
			createPom();
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
	
	private void createPom() {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// dependencies elements
			Document doc = docBuilder.newDocument();
			Element project = doc.createElement("project");
			doc.appendChild(project);
			
			// set attribute to project element
			Attr attr = doc.createAttribute("xmlns");
			attr.setValue("http://maven.apache.org/POM/4.0.0");
			project.setAttributeNode(attr);
			
			Attr attr2 = doc.createAttribute("xmlns:xsi");
			attr2.setValue("http://www.w3.org/2001/XMLSchema-instance");
			project.setAttributeNode(attr2);
			
			Attr attr3 = doc.createAttribute("xsi:schemaLocation");
			attr3.setValue("http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
			project.setAttributeNode(attr3);
			
			Element modelVersion = doc.createElement("modelVersion");
			modelVersion.appendChild(doc.createTextNode("4.0.0"));
			project.appendChild(modelVersion);
			
			Element groupId = doc.createElement("groupId");
			groupId.appendChild(doc.createTextNode("digitaltwin"));
			project.appendChild(groupId);
			
			Element artifactId = doc.createElement("artifactId");
			artifactId.appendChild(doc.createTextNode("device"));
			project.appendChild(artifactId);
			
			Element version = doc.createElement("version");
			version.appendChild(doc.createTextNode("0.0.1-SNAPSHOT"));
			project.appendChild(version);
			
			Element packaging = doc.createElement("packaging");
			packaging.appendChild(doc.createTextNode("jar"));
			project.appendChild(packaging);
			
			Element parent = doc.createElement("parent");
			project.appendChild(parent);
			
			Element groupIdParent = doc.createElement("groupId");
			groupIdParent.appendChild(doc.createTextNode("org.springframework.boot"));
			parent.appendChild(groupIdParent);
			
			Element artifactIdParent = doc.createElement("artifactId");
			artifactIdParent.appendChild(doc.createTextNode("spring-boot-starter-parent"));
			parent.appendChild(artifactIdParent);
			
			Element versionParent = doc.createElement("version");
			versionParent.appendChild(doc.createTextNode("1.5.10.RELEASE"));
			parent.appendChild(versionParent);
			
			Element properties = doc.createElement("properties");
			project.appendChild(properties);
			
			Element sourceEncoding = doc.createElement("project.build.sourceEncoding");
			sourceEncoding.appendChild(doc.createTextNode("UTF-8"));
			properties.appendChild(sourceEncoding);
			
			Element outputEncoding = doc.createElement("project.reporting.outputEncoding");
			outputEncoding.appendChild(doc.createTextNode("UTF-8"));
			properties.appendChild(outputEncoding);
			
			Element javaVersion = doc.createElement("java.version");
			javaVersion.appendChild(doc.createTextNode("1.8"));
			properties.appendChild(javaVersion);
			
			Element dependencies = doc.createElement("dependencies");
			project.appendChild(dependencies);

			// dependency elements
			Element dependency = doc.createElement("dependency");
			dependencies.appendChild(dependency);

			
			Element groupIdD = doc.createElement("groupId");
			groupIdD.appendChild(doc.createTextNode("com.indracompany.sofia2"));
			dependency.appendChild(groupIdD);

			Element artifactIdD = doc.createElement("artifactId");
			artifactIdD.appendChild(doc.createTextNode("sofia2-digital-twin"));
			dependency.appendChild(artifactIdD);

			Element versionD = doc.createElement("version");
			versionD.appendChild(doc.createTextNode("0.0.1-SNAPSHOT"));
			dependency.appendChild(versionD);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(tempDir + File.separator + "devicetwin" + File.separator +"pom.xml"));

			transformer.transform(source, result);

		  } catch (ParserConfigurationException e0) {
			log.error("error doing pom.xml.",e0);
		  } catch (TransformerException e1) {
			 log.error("error doing pom.xml.",e1);
		  }
	}

}
