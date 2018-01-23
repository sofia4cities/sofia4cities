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
package com.indra.sofia2.support.util.sib.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.indra.jee.arq.spring.core.arranque.ArqSpringPropertiesConfiguration;
import com.indra.jee.arq.spring.core.arranque.ArqSpringPropertyPlaceHolderInterface;
import com.indra.jee.arq.spring.core.contexto.ArqSpringContext;
import com.indra.jee.arq.spring.core.infraestructura.log.I18nLog;
import com.indra.jee.arq.spring.core.infraestructura.log.I18nLogFactory;
import com.indra.jee.arq.spring.core.util.constantes.ConstantesContexto;
import com.indra.jee.arq.spring.core.util.constantes.ConstantesLog;
import com.indra.jee.arq.spring.core.util.constantes.ConstantesProperties;
import com.indra.sofia2.support.bbdd.plugins.load.PluginDefinition;
import com.indra.sofia2.support.bbdd.plugins.load.PluginLoader;

/**
 * Extension de la clase PropertyPlaceholderConfigurer de Spring para dar
 * soporte al uso de profiles
 * 
 * @author Indra
 * 
 */
public class SofiaPropertyPlaceHolder extends PropertyPlaceholderConfigurer implements ArqSpringPropertyPlaceHolderInterface {
	
	private static final String SYSVAR_ENTORNO=System.getProperty(ConstantesContexto.ARQSPRING_ENTORNO);
	private static final String SYSVAR_INSTANCIA=System.getProperty(ConstantesContexto.ARQSPRING_INSTANCIA);
	
	private Properties aplvarProperties;
	
	/**
	 * Log generales de la arquitectura.
	 */
	private static final I18nLog LOG = I18nLogFactory.getLogI18n(SofiaPropertyPlaceHolder.class, ConstantesLog.MENSAJES_GENERAL);

	/**
	 * Metodo que disponibiliza los properties gestionados por la Arquitectura
	 */
	public Properties disponibilizarProperties() {
		Properties properties = null;
		try {
			properties = mergeProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	/**
	 * Metodo de inicializaci�n del PlaceHolder de la Arquitectura
	 */
	public void init() {
		try {
			LOG.info("ArqSpringPropertyPlaceHolder.init.entorno", SYSVAR_ENTORNO);
			if (SYSVAR_ENTORNO==null) {
				LOG.warn("ArqSpringPropertyPlaceHolder.init.error.entorno");
			}
			LOG.info("ArqSpringPropertyPlaceHolder.init.instancia", SYSVAR_INSTANCIA);
			if (SYSVAR_INSTANCIA==null) {
				LOG.warn("ArqSpringPropertyPlaceHolder.init.error.instancia");
			}
			String configDirectory = SofiaContextLoader.getConfigDirectory();
			if (configDirectory != null) {
				aplvarProperties = SofiaContextLoader.loadModuloProperties();
				initConfiguracion(configDirectory, aplvarProperties);
			} else {
				initConfiguracionLocal();
			}
			initLog4jMDC(SYSVAR_ENTORNO);
		} catch (Exception e) {
			LOG.error("ArqSpringPropertyPlaceHolder.init.error", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Este metodo recupera las rutas de propiedades en entorno local.
	 * @param libreria
	 * @return
	 */
	private List<File> obtenerPropertiesLocal(Boolean libreria) throws Exception{
		List<File> recursos = new ArrayList<File>();
		Enumeration<URL> ficheros = getClass().getClassLoader().getResources("META-INF/spring/");
		while (ficheros.hasMoreElements()){
			try{
				URL urls = ficheros.nextElement();
				if (!urls.toURI().toString().contains("jar:")){
					File fileMetaInf=new File(urls.toURI().getPath());
					if (fileMetaInf.isDirectory()){
					    String[] filenames=fileMetaInf.list(); 
					    for (String fichero : filenames){
					    	if (libreria){
					    		if (fichero.endsWith(".properties")&&!fichero.contains("log4j")){
						    		URL url = getClass().getClassLoader().getResource("META-INF/spring/"+fichero);
						    		recursos.add(new File(url.getPath()));
						    	}
					    	}else{
					    		if (fichero.endsWith(".properties")&&!fichero.startsWith("lib_")&&!fichero.contains("log4j")){
						    		URL url = getClass().getClassLoader().getResource("META-INF/spring/"+fichero);
						    		recursos.add(new File(url.getPath()));
					    		}
					    	}
					    }
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return recursos;
	}
	/**
	 * Este metodo inicia la carga de ficheros de configuración en entornos locales.
	 * @param libreria
	 * @return
	 */
	private void initConfiguracionLocal() throws Exception {
		LOG.info("Usando configuracion local....");
		List<File> recursos = new ArrayList<File>();
		recursos= obtenerPropertiesLocal((ArqSpringContext.recuperarIdentificador()!=null));
		Properties propertiesCompleto = new Properties();
		inicializarPropertiesTotal(recursos, propertiesCompleto);
		initLog4JModoLocal(propertiesCompleto);
	}
	/**
	 * Realiza la carga de los ficheros de configuracion
	 * @param rutaConfiguracion
	 * @param info
	 * @throws Exception
	 */
	private void initConfiguracion(String rutaConfiguracion, Properties info) throws Exception {
		Properties propertiesCompleto = new Properties();
		String identificadorHilo=ArqSpringContext.recuperarIdentificador();
		List<File> recursosAplicacion = new ArrayList<File>();
		StringBuffer rutaConfiguracionAplicacion = new StringBuffer(rutaConfiguracion);
		rutaConfiguracionAplicacion.append("/");
//		rutaConfiguracionAplicacion.append(info.getProperty(ConstantesProperties.ARQUITECTURA_APLICACION_NOMBRE));
//		rutaConfiguracionAplicacion.append("/");
		LOG.info("ArqSpringPropertyPlaceHolder.init.rutaConfiguracion",rutaConfiguracionAplicacion.toString());
		
		// Cargo ficheros que no son de entonro y son de libreria
		
		String patronLibreria=null;
		
		if (identificadorHilo!=null){
			StringBuffer patronLibreriaBuffer = new StringBuffer("lib_");
			patronLibreriaBuffer.append(identificadorHilo);
			patronLibreriaBuffer.append("_");
			patronLibreria=patronLibreriaBuffer.toString();
		}
		
		recursosAplicacion.addAll(obtenerFicheros(
				new File(rutaConfiguracionAplicacion.toString()),
				patronLibreria, ".properties", "log4j.properties",
				null));
		
		inicializarPropertiesTotal(recursosAplicacion, propertiesCompleto);
		
		List<File> recursosModulo = new ArrayList<File>();
		recursosModulo.add(new File(getClass().getClassLoader()
					.getResource(SofiaContextLoader.MODULE_PROPERTIES_FILE)
					.getPath()));
		StringBuffer rutaConfiguracionModulo = new StringBuffer(rutaConfiguracionAplicacion);
		rutaConfiguracionModulo.append(info.getProperty(ConstantesProperties.ARQUITECTURA_MODULO_NOMBRE));
		rutaConfiguracionModulo.append("/");
		LOG.info("ArqSpringPropertyPlaceHolder.init.rutaConfiguracion",rutaConfiguracionModulo.toString());
		
		recursosModulo.addAll(obtenerFicheros(
				new File(rutaConfiguracionModulo.toString()),
				patronLibreria, ".properties", "log4j.properties",
				null));
		
		inicializarPropertiesTotal(recursosModulo, propertiesCompleto);
		initLog4J(rutaConfiguracionModulo.toString(), propertiesCompleto);
	}
	/**
	 * Inicializa todos los ficheros de propiedades, tambien los import
	 * @param recursos
	 * @param propertiesCompleto
	 * @throws IOException
	 */
	private void inicializarPropertiesTotal(List<File> recursos, Properties propertiesCompleto) throws IOException{
		CompositeConfiguration config = new CompositeConfiguration();
		File resource = null;
		for (Iterator<File> iterator = recursos.iterator(); iterator.hasNext();) {
			try{
				resource = iterator.next();
				config.addConfiguration(new ArqSpringPropertiesConfiguration(resource));
			}catch (ConfigurationException e) {
				LOG.error(e,"ArqSpringPropertyPlaceHolder.inicializarPropertiesTotal.error",resource.getPath());
			}
		}
		Map<String, PluginDefinition> pluginDefinitions = PluginLoader.getInstance().getPluginDefinitions();
		for (PluginDefinition pluginDefinition : pluginDefinitions.values()) {
			propertiesCompleto.putAll(pluginDefinition.getPluginDefinitionFileContent());
		}
		LOG.debug("Usando esta configuración:");
		for (Iterator<?> iterator = config.getKeys(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object objeto = config.getProperty(key);
			String valor=null;
			if (objeto instanceof  ArrayList){
				valor = fromArrayString(config.getStringArray(key));
			}else{
				valor = (String)config.getString(key);
			}
			propertiesCompleto.put(key, valor);
			LOG.debug("\t" + key + "=" + valor);
		}
		setProperties(propertiesCompleto);
		LOG.debug("Las propiedades cargadas son: ");
		
		for (String key : propertiesCompleto.stringPropertyNames()) {
			Object value = propertiesCompleto.get(key);
			LOG.debug("\t" + key + "=" + value);
		}
		
	}
	/**
	 * Inicializa el log4j en entornos de desarrollo local
	 * @param propertiesCompleto
	 */
	private void initLog4JModoLocal(Properties propertiesCompleto) {
		String log4JProperties = null;
		try {
			log4JProperties = getClass().getClassLoader().getResource("log4j.properties").getPath();
			Configuration config = new PropertiesConfiguration(log4JProperties);
			for (String propiedad : propertiesCompleto.stringPropertyNames()){
				if (config.getProperty(propiedad)==null){
					config.addProperty(propiedad, propertiesCompleto.get(propiedad));
				}
			}
			Properties propertiesLog4J = new Properties();
			LOG.debug("Usando esta configuración de Log:");
			String key = null;
			String valor = null;
			for (Iterator<?> iterator = config.getKeys(); iterator.hasNext();) {
				key = (String) iterator.next();
				Object objeto = config.getProperty(key);
				if (objeto instanceof  ArrayList){
					valor = fromArrayString(config.getStringArray(key));
				}else{
					valor = (String)config.getProperty(key);
				}
				propertiesLog4J.setProperty(key, valor);
				LOG.debug("\t" + key + "=" + valor);
			}			
			LogManager.resetConfiguration();
			PropertyConfigurator.configure(propertiesLog4J);			
			LOG.info("ArqSpringPropertyPlaceHolder.initLog4J.configuracionCambiada",log4JProperties);
		} catch (Throwable e) {
			LOG.error("Error cargando fichero de configuración log4j.properties",e);
		}
	}
	/**
	 * Inicializa el componente Log4J externalizado para aplicaciones productivas.
	 * @param rutaConfiguracion
	 * @param propertiesCompleto
	 */
	private void initLog4J(String rutaConfiguracion, Properties propertiesCompleto) {
		String log4JProperties = null;
		try {
			LOG.info("ArqSpringPropertyPlaceHolder.init.initLog4j");
			log4JProperties = rutaConfiguracion + "log4j.properties";

			Configuration config = new PropertiesConfiguration(log4JProperties);

			for (String propiedad : propertiesCompleto.stringPropertyNames()){
				if (config.getProperty(propiedad)==null){
					config.addProperty(propiedad, propertiesCompleto.get(propiedad));
				}
			}
			
			Properties propertiesLog4J = new Properties();
			LOG.debug("Usando esta configuración de Log:");
			String key = null;
			String valor = null;
			for (Iterator<?> iterator = config.getKeys(); iterator.hasNext();) {
				key = (String) iterator.next();
				Object objeto = config.getProperty(key);
				if (objeto instanceof  ArrayList){
					valor = fromArrayString(config.getStringArray(key));
				}else{
					valor = (String)config.getProperty(key);
				}
				propertiesLog4J.setProperty(key, valor);
				LOG.debug("\t" + key + "=" + valor);
			}			
			LogManager.resetConfiguration();
			PropertyConfigurator.configure(propertiesLog4J);
		
			LOG.info("ArqSpringPropertyPlaceHolder.initLog4J.configuracionCambiada",log4JProperties);
		} catch (Throwable e) {
			LOG.error("Error cargando fichero de configuración log4j.properties",e);
		}
	}
	/**
	 * Carga en el MDC de logj4 el valor de la variable cloneId, HOSTNAME
	 */
	private void initLog4jMDC(String entorno) {
		if (SYSVAR_INSTANCIA != null) {
			MDC.put(ConstantesContexto.ARQSPRING_INSTANCIA, SYSVAR_INSTANCIA);
		} else {
			MDC.put(ConstantesContexto.ARQSPRING_INSTANCIA, "local");
		}
		if (entorno != null) {
			MDC.put(ConstantesContexto.ARQSPRING_ENTORNO, entorno);
		} else {
			MDC.put(ConstantesContexto.ARQSPRING_ENTORNO, "local");
		}
	}
	/**
	 * Metodo utilizado en la carga de propiedades para convertir valores separados por ,
	 * @param conf
	 * @return
	 */
	private String fromArrayString(String[] conf) {
		if (conf== null || conf.length==0) {
			return "";
		}
		if (conf.length==1) {
			return conf[0];
		}
		String valor="";
		for (int i = 0; i < conf.length; i++) {
			valor = valor.concat(conf[i]);
			if (i<conf.length-1) {
				valor = valor.concat(",");
			}
		}
		return valor;
	}
	/**
	 * Metodo que recupera los ficheros de propiedades, tanto para librerias como para aplicaciones.
	 * @param ficheroRaiz
	 * @param identificadorHilo
	 * @param extension
	 * @param excluir
	 * @param recursos
	 * @return
	 */
	private List<File> obtenerFicheros(File ficheroRaiz, String identificadorHilo,
			String extension, String excluir, List<File> recursos) {
		List<File> recursosTemporal = recursos;
		if (recursosTemporal == null) {
			recursosTemporal = new ArrayList<File>();
		}
		LOG.debug("ArqSpringPropertyPlaceHolder.init.processficheroRaizDirectory", ficheroRaiz.getName());
		try {
			if (ficheroRaiz.exists() && ficheroRaiz.isDirectory()) {
				LOG.debug("ArqSpringPropertyPlaceHolder.init.ficheroRaizDirectory",
						ficheroRaiz.getName());
				File[] ficheros = ficheroRaiz.listFiles();
				for (int x = 0; x < ficheros.length; x++) {
					try {
						if (!ficheros[x].isDirectory()){
							LOG.debug("ArqSpringPropertyPlaceHolder.init.ficheroRaiz",ficheros[x]);
							if (identificadorHilo!=null){
								if ((ficheros[x].getName().startsWith(identificadorHilo)
										||!ficheros[x].getName().startsWith("lib_"))
										&&ficheros[x].getName().endsWith(extension)
										&& !ficheros[x].getName().contains(excluir)){
									LOG.debug("ArqSpringPropertyPlaceHolder.init.cumplePatrones",ficheros[x]);
									recursosTemporal.add(ficheros[x]);
								}
							}else{
								if (!ficheros[x].getName().startsWith("lib_")
									 &&ficheros[x].getName().endsWith(extension)
									 && !ficheros[x].getName().contains(excluir)){
									LOG.debug("ArqSpringPropertyPlaceHolder.init.cumplePatrones",ficheros[x]);
									recursosTemporal.add(ficheros[x]);
								}
							}
						}
					} catch (NullPointerException ex) {
						LOG.error("Error cargando fichero de configuración " + ficheroRaiz.getName(), ex);
					}
				}
			} 
		} catch (NullPointerException ex) {
			LOG.error("Error cargando fichero de configuración " + ficheroRaiz.getName(), ex);
		}
		return recursosTemporal;
	}
}
