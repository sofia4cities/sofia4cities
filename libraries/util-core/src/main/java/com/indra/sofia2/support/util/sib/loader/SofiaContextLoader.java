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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import com.indra.jee.arq.spring.core.arranque.ArqSpringContextLoaderListener;
import com.indra.jee.arq.spring.core.contexto.ArqSpringContext;
import com.indra.jee.arq.spring.core.util.constantes.ConstantesContexto;
import com.indra.jee.arq.spring.core.util.constantes.ConstantesProperties;
import com.indra.sofia2.support.bbdd.plugins.load.PluginLoader;
import com.indra.sofia2.support.util.version.VersionProperties;


/**
 * Se sobreescribe SpringContextLoader para dar soporte al uso de profiles y la carga de
 * properties en funcion del profile.
 * @author Indra
 *
 */
public class SofiaContextLoader {
	
	public static final String MODULE_PROPERTIES_FILE = "META-INF/spring/sofia2-module.properties";
	private static final int SERVLET_MAJOR_VERSION_2 = 2;
	private static final int SERVLET_MINOR_VERSION_5 = 5;
	private static final String PLUGINS_VARIABLE_NAME = "PLUGINS";
	private static final String DEFAULT_PLUGINS_DIR = "sofia2-plugins";
	private static final String DEFAULT_CONFIG_DIR = "sofia2-config";
	private static final String HOME_ENVIRONMENT_VARIABLE = "HOME";

	/**
	 * Log generales de la arquitectura.
	 */
	private static Logger log = LoggerFactory.getLogger(SofiaContextLoader.class);
		
	/**
	 * contexto
	 */
	private static ApplicationContext contexto;
	public static ApplicationContext getContexto() {
		return contexto;
	}

	/**
	 * Config param for the root WebApplicationContext implementation class to
	 * use: "<code>contextClass</code>"
	 */
	public static final String CONTEXT_CLASS_PARAM = "contextClass";

	/**
	 * Optional servlet context parameter (i.e., "<code>locatorFactorySelector</code>")
	 * used only when obtaining a parent context using the default implementation
	 * of {@link #loadParentContext(ServletContext servletContext)}.
	 * Specifies the 'selector' used in the
	 * {@link ContextSingletonBeanFactoryLocator#getInstance(String selector)}
	 * method call, which is used to obtain the BeanFactoryLocator instance from
	 * which the parent context is obtained.
	 * <p>The default is <code>classpath*:beanRefContext.xml</code>,
	 * matching the default applied for the
	 * {@link ContextSingletonBeanFactoryLocator#getInstance()} method.
	 * Supplying the "parentContextKey" parameter is sufficient in this case.
	 */
	public static final String LOCATOR_FACTORY_SELECTOR_PARAM = "locatorFactorySelector";

	/**
	 * Optional servlet context parameter (i.e., "<code>parentContextKey</code>")
	 * used only when obtaining a parent context using the default implementation
	 * of {@link #loadParentContext(ServletContext servletContext)}.
	 * Specifies the 'factoryKey' used in the
	 * {@link BeanFactoryLocator#useBeanFactory(String factoryKey)} method call,
	 * obtaining the parent application context from the BeanFactoryLocator instance.
	 * <p>Supplying this "parentContextKey" parameter is sufficient when relying
	 * on the default <code>classpath*:beanRefContext.xml</code> selector for
	 * candidate factory references.
	 */
	public static final String LOCATOR_FACTORY_KEY_PARAM = "parentContextKey";

	/**
	 * Name of the class path resource (relative to the ContextLoader class)
	 * that defines ContextLoader's default strategy names.
	 */
	private static final String DEFAULT_STRATEGIES_PATH = "ContextLoader.properties";


	private static final Properties DEFAULT_STRATERGIES;

	static {
		// Load default strategy implementations from properties file.
		// This is currently strictly internal and not meant to be customized
		// by application developers.
		try {
			ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, ArqSpringContextLoaderListener.class);
			DEFAULT_STRATERGIES = PropertiesLoaderUtils.loadProperties(resource);
		}
		catch (IOException ex) {
			throw new IllegalStateException("Could not load 'ContextLoader.properties': " + ex.getMessage());
		}
	}


	/**
	 * Map from (thread context) ClassLoader to corresponding 'current' WebApplicationContext.
	 */
	private static final Map<ClassLoader, WebApplicationContext> CURRENT_CONTEXT_PER_THREAD =
			new ConcurrentHashMap<ClassLoader, WebApplicationContext>(1);

	/**
	 * The 'current' WebApplicationContext, if the ContextLoader class is
	 * deployed in the web app ClassLoader itself.
	 */
	private static volatile WebApplicationContext currentContext;

	/**
	 * The root WebApplicationContext instance that this loader manages.
	 */
	private WebApplicationContext context;

	/**
	 * Holds BeanFactoryReference when loading parent factory via
	 * ContextSingletonBeanFactoryLocator.
	 */
	private BeanFactoryReference parentContextRef;


	/**
	 * Initialize Spring's web application context for the given servlet context,
	 * according to the "{@link #CONTEXT_CLASS_PARAM contextClass}" and
	 * "{@link #CONFIG_LOCATION_PARAM contextConfigLocation}" context-params.
	 * @param servletContext current servlet context
	 * @return the new WebApplicationContext
	 * @see #CONTEXT_CLASS_PARAM
	 * @see #CONFIG_LOCATION_PARAM
	 */
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		if (servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
			throw new IllegalStateException(
					"Cannot initialize context because there is already a root application context present - " +
					"check whether you have multiple ContextLoader* definitions in your web.xml!");
		}

		try {
			// Determine parent for root web application context, if any.
			ApplicationContext parent = loadParentContext(servletContext);

			// Store context in local instance variable, to guarantee that
			// it is available on ServletContext shutdown.
			this.context = createWebApplicationContext(servletContext, parent);
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);

			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			if (ccl == SofiaContextLoader.class.getClassLoader()) {
				currentContext = this.context;
			}
			else if (ccl != null) {
				CURRENT_CONTEXT_PER_THREAD.put(ccl, this.context);
			}
			log.debug("Inicializando el ServletContext "+WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			
			contexto=this.context;
			return this.context;
		}
		catch (RuntimeException ex) {
			log.error("Error al inicializar el ServletContext", ex);
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
			throw ex;
		}
		catch (Error err) {
			log.error("Error al inicializar el ServletContext", err);
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, err);
			throw err;
		}
	}

	/**
	 * Instantiate the root WebApplicationContext for this loader, either the
	 * default context class or a custom context class if specified.
	 * <p>This implementation expects custom contexts to implement the
	 * {@link ConfigurableWebApplicationContext} interface.
	 * Can be overridden in subclasses.
	 * <p>In addition, {@link #customizeContext} gets called prior to refreshing the
	 * context, allowing subclasses to perform custom modifications to the context.
	 * @param sc current servlet context
	 * @param parent the parent ApplicationContext to use, or <code>null</code> if none
	 * @return the root WebApplicationContext
	 * @see ConfigurableWebApplicationContext
	 */
	protected WebApplicationContext createWebApplicationContext(ServletContext sc, ApplicationContext parent) {
		Class<?> contextClass = determineContextClass(sc);
		if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
			throw new ApplicationContextException("Custom context class [" + contextClass.getName() +
					"] is not of type [" + ConfigurableWebApplicationContext.class.getName() + "]");
		}
		ConfigurableWebApplicationContext wac =
				(ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

		// Assign the best possible id value.
		if (sc.getMajorVersion() == SERVLET_MAJOR_VERSION_2 && sc.getMinorVersion() < SERVLET_MINOR_VERSION_5) {
			// Servlet <= 2.4: resort to name specified in web.xml, if any.
			String servletContextName = sc.getServletContextName();
			wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
					ObjectUtils.getDisplayString(servletContextName));
		}
		else {
			// Servlet 2.5's getContextPath available!
			try {
				String contextPath = (String) ServletContext.class.getMethod("getContextPath").invoke(sc);
				wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
						ObjectUtils.getDisplayString(contextPath));
			}
			catch (Exception ex) {
				throw new IllegalStateException("Failed to invoke Servlet 2.5 getContextPath method", ex);
			}
		}
		wac.setParent(parent);
		wac.setServletContext(sc);
		/* Se recupera la configuarcion del entorno */
		try{
			String entorno = System.getProperty(ConstantesContexto.ARQSPRING_ENTORNO);
			log.debug("Entorno de ejecuciÃƒÂ³n "+entorno);
			/* Se indican los ficheros de SPRING a cargar en funcion del entorno */
			wac.setConfigLocations(inicializarFicherosDeSpring(entorno));
			customizeContext(sc, wac);
			wac.refresh();
			/* Se establece la configuracion de la aplicacion */
			imprimirArranqueVersion(entorno,"WEB");
		}catch (Exception e) {
			log.error("Error",e);
		}
		this.context=wac;
		return wac;
	}
	
	private static String getPluginsDirectory() {
		String pluginsDir = System.getProperty(PLUGINS_VARIABLE_NAME);

		if (pluginsDir != null && !pluginsDir.isEmpty()) {
			log.info("Using plugin directory defined by the system property '{}'. Path = {}.", PLUGINS_VARIABLE_NAME,
					pluginsDir);
		} else {
			// We're building an URI here
			pluginsDir = System.getenv(HOME_ENVIRONMENT_VARIABLE) + "/" + DEFAULT_PLUGINS_DIR;
			log.info("Using default plugins directory. Path = {}.", pluginsDir);
		}
		return pluginsDir;
	}
	
	public static String getConfigDirectory() {
		String configDir = System.getProperty(ConstantesContexto.ARQSPRING_CONFIG);
		if (configDir != null && !configDir.isEmpty()) {
			log.info("Using plugin directory defined by the system property '{}'. Path = {}.",
					ConstantesContexto.ARQSPRING_CONFIG, configDir);
		} else if (new File(System.getenv(HOME_ENVIRONMENT_VARIABLE) + File.separator + DEFAULT_CONFIG_DIR).exists()) {
			configDir = System.getenv(HOME_ENVIRONMENT_VARIABLE) + "/" + DEFAULT_CONFIG_DIR;
			log.info("Using default config directory. Path = {}.", configDir);
		} else {
			log.info("The default config directory doesn't exist. Falling back to local configuration.");
		}
		return configDir;
	}

	/**
	 * Metodo que devuelte un array con la locsalizacion de los ficheros de spring dependiendo del entorno.
	 * @param entorno.
	 * @param contextConfigLocation.
	 */
	private static String[] inicializarFicherosDeSpring(String entorno) {
		
		/*
		 * If the PLUGINS variable is not set, we'll read the plugins from the $HOME/sofia2-plugins directory
		 */
		
		String pluginsDir = getPluginsDirectory();
		
		log.info("Loading application descriptor file...");
		
		String moduleName = null;
//		String aplicacion = null;
		try {
			Properties moduloProperties = loadModuloProperties();
//			aplicacion = moduloProperties.getProperty(ConstantesProperties.ARQUITECTURA_APLICACION_NOMBRE);
			moduleName = moduloProperties.getProperty(ConstantesProperties.ARQUITECTURA_MODULO_NOMBRE);
		}catch (Exception e) {
			log.error(e.getMessage());
		}
		
		try {
			/*
			 * The plugins directory no longer contains the application name: it's redundant.
			 */
			// Cargamos plugin globales a todos los modulos
			String rootPluginsDir = pluginsDir + /*"/" + aplicacion +*/ "/";
			PluginLoader.getInstance().loadPlugins(rootPluginsDir);
			// Cargamos plugin especificos de cada modulo
			String modulePluginsDir = pluginsDir + /*"/" + aplicacion +*/ "/" + moduleName + "/";
			PluginLoader.getInstance().loadPlugins(modulePluginsDir);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		List<String> rutas = new ArrayList<>();
		rutas.add("classpath*:META-INF/arq-spring/applicationContext-ArqSpring.xml");
		rutas.add("classpath*:META-INF/arq-spring/applicationContext-ArqSpring-*.xml");		
		rutas.add("classpath*:META-INF/spring/applicationContext.xml");
		rutas.add("classpath*:META-INF/spring/applicationContext-*.xml");
		rutas.add("classpath*:META-INF/spring/sofia2-plugin-context-*.xml");
		
		// Inicializamos ficheros externalizados de aplicacion
		String configDirectory = getConfigDirectory();
		if (configDirectory != null /* && aplicacion != null */ && moduleName != null) {
			rutas.add("file:" + configDirectory + /* "/"+aplicacion+ */"/" + moduleName + "/applicationContext-*.xml");
		}
		
		if (entorno != null) {
			rutas.add("classpath*:META-INF/spring/" + entorno + "_applicationContext-*.xml");
		} else {
			rutas.add("classpath*:META-INF/spring/local_applicationContext-*.xml");
		}

		String str[] = new String[rutas.size()];
		rutas.toArray(str);
		log.debug("Application configuration paths: {}.", rutas);
		return str;
	}
	
	/**
	 * Metodo que recupera el fichero de propiedades modulo.properties, con configuracion especifica de cada aplicacion
	 * @return
	 * @throws Exception
	 */
	public static Properties loadModuloProperties()  throws Exception{
		Properties info = new Properties();
		info.load(SofiaContextLoader.class.getClassLoader().getResourceAsStream(MODULE_PROPERTIES_FILE));
		return info;
	}
	
	/**
	 * Return the WebApplicationContext implementation class to use, either the
	 * default XmlWebApplicationContext or a custom context class if specified.
	 * @param servletContext current servlet context
	 * @return the WebApplicationContext implementation class to use
	 * @see #CONTEXT_CLASS_PARAM
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 */
	protected Class<?> determineContextClass(ServletContext servletContext) {
		String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
		if (contextClassName != null) {
			try {
				return ClassUtils.forName(contextClassName, ClassUtils.getDefaultClassLoader());
			}
			catch (ClassNotFoundException ex) {
				throw new ApplicationContextException(
						"Failed to load custom context class [" + contextClassName + "]", ex);
			}
		}
		else {
			contextClassName = DEFAULT_STRATERGIES.getProperty(WebApplicationContext.class.getName());
			try {
				return ClassUtils.forName(contextClassName, SofiaContextLoader.class.getClassLoader());
			}
			catch (ClassNotFoundException ex) {
				throw new ApplicationContextException(
						"Failed to load default context class [" + contextClassName + "]", ex);
			}
		}
	}

	/**
	 * Customize the {@link ConfigurableWebApplicationContext} created by this
	 * ContextLoader after config locations have been supplied to the context
	 * but before the context is <em>refreshed</em>.
	 * <p>The default implementation is empty but can be overridden in subclasses
	 * to customize the application context.
	 * @param servletContext the current servlet context
	 * @param applicationContext the newly created application context
	 * @see #createWebApplicationContext(ServletContext, ApplicationContext)
	 */
	protected void customizeContext(
			ServletContext servletContext, ConfigurableWebApplicationContext applicationContext) {
	}

	/**
	 * Template method with default implementation (which may be overridden by a
	 * subclass), to load or obtain an ApplicationContext instance which will be
	 * used as the parent context of the root WebApplicationContext. If the
	 * return value from the method is null, no parent context is set.
	 * <p>The main reason to load a parent context here is to allow multiple root
	 * web application contexts to all be children of a shared EAR context, or
	 * alternately to also share the same parent context that is visible to
	 * EJBs. For pure web applications, there is usually no need to worry about
	 * having a parent context to the root web application context.
	 * <p>The default implementation uses
	 * {@link org.springframework.context.access.ContextSingletonBeanFactoryLocator},
	 * configured via {@link #LOCATOR_FACTORY_SELECTOR_PARAM} and
	 * {@link #LOCATOR_FACTORY_KEY_PARAM}, to load a parent context
	 * which will be shared by all other users of ContextsingletonBeanFactoryLocator
	 * which also use the same configuration parameters.
	 * @param servletContext current servlet context
	 * @return the parent application context, or <code>null</code> if none
	 * @see org.springframework.context.access.ContextSingletonBeanFactoryLocator
	 */
	protected ApplicationContext loadParentContext(ServletContext servletContext) {
		ApplicationContext parentContext = null;
		String locatorFactorySelector = servletContext.getInitParameter(LOCATOR_FACTORY_SELECTOR_PARAM);
		String parentContextKey = servletContext.getInitParameter(LOCATOR_FACTORY_KEY_PARAM);

		if (parentContextKey != null) {
			// locatorFactorySelector may be null, indicating the default "classpath*:beanRefContext.xml"
			BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(locatorFactorySelector);
			log.debug("PatentKey de Arquitectura "+parentContextKey);
			this.parentContextRef = locator.useBeanFactory(parentContextKey);
			parentContext = (ApplicationContext) this.parentContextRef.getFactory();
		}

		return parentContext;
	}

	/**
	 * Close Spring's web application context for the given servlet context. If
	 * the default {@link #loadParentContext(ServletContext)} implementation,
	 * which uses ContextSingletonBeanFactoryLocator, has loaded any shared
	 * parent context, release one reference to that shared parent context.
	 * <p>If overriding {@link #loadParentContext(ServletContext)}, you may have
	 * to override this method as well.
	 * @param servletContext the ServletContext that the WebApplicationContext runs in
	 */
	public void closeWebApplicationContext(ServletContext servletContext) {
		servletContext.log("Closing Spring root WebApplicationContext");
		try {
			if (this.context instanceof ConfigurableWebApplicationContext) {
				((ConfigurableWebApplicationContext) this.context).close();
			}
		}
		finally {
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			if (ccl == SofiaContextLoader.class.getClassLoader()) {
				currentContext = null;
			}
			else if (ccl != null) {
				CURRENT_CONTEXT_PER_THREAD.remove(ccl);
			}
			servletContext.removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			if (this.parentContextRef != null) {
				this.parentContextRef.release();
			}
		}
	}


	/**
	 * Obtain the Spring root web application context for the current thread
	 * (i.e. for the current thread's context ClassLoader, which needs to be
	 * the web application's ClassLoader).
	 * @return the current root web application context, or <code>null</code>
	 * if none found
	 * @see org.springframework.web.context.support.SpringBeanAutowiringSupport
	 */
	public static WebApplicationContext getCurrentWebApplicationContext() {
		ClassLoader ccl = Thread.currentThread().getContextClassLoader();
		if (ccl != null) {
			WebApplicationContext ccpt = CURRENT_CONTEXT_PER_THREAD.get(ccl);
			if (ccpt != null) {
				return ccpt;
			}
		}
		return currentContext;
	}
	/**
	 * Inicializa el context de Spring para aplicaciones stand alone.
	 * @return ApplicationContext
	 */
	public static ApplicationContext crearContexto(){
		if (contexto==null){
			try{
				log.debug("CONTEXTO "+"STANDALONE");
				String entorno = System.getProperty(ConstantesContexto.ARQSPRING_ENTORNO);
				log.debug("ENTORNO "+entorno);
				contexto = new ClassPathXmlApplicationContext(inicializarFicherosDeSpring(entorno));
				Properties info  = new Properties();
				info.load(SofiaContextLoader.class.getClassLoader().getResourceAsStream(MODULE_PROPERTIES_FILE)); 			
				imprimirArranqueVersion(entorno, "STANDALONE");
				log.debug("Arquitectura Arrancada correctamente");
			}catch (Exception e) {
				log.error("Arquitectura Arrancada con errores", e);
			}
		}
		return contexto;
	}
	
	/**
	 * Metodo que imprime el arranque de la arquitectura
	 */
	public synchronized static void imprimirArranqueVersion(String entorno, String contexto) {
		String entornoReal = entorno;
		if (entornoReal==null){
			entornoReal="LOCAL";
		}
		String contextoReal = contexto;
		if (contextoReal==null){
			contextoReal="TEST";
		}
		
		String compilationDate = null;
		try {
			compilationDate = ArqSpringContext.getPropiedad(VersionProperties.COMPILATION_DATE);
		} catch (Exception e){}
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("********************************************************************************************************************").append("\n");
		sb.append("                                                                                  .                                 \n");
		sb.append("                                                                                   E@@ppw,.                         \n");
		sb.append("                                                                                   $@@@@@@@@@@Eppu,..               \n");
		sb.append("                                                                                   {@@@@@@@@@@@@@@@@@@@@@pp-        \n");
		sb.append("         ,,=======,                       ,@@@@    @@                              :@@EEPhP\"\"\"thEE@@@@@@@@@         \n");
		sb.append("       @BBRMMTTTMMM                      ]BB^``    BB                               @`            `E@@@@@@P         \n");
		sb.append("      ;BBL                               ]BM                                        $L              $@@@@@          \n");
		sb.append("       BBN               ;@KBBBBKp   :KKKBBBKKK    KK     #KBBBBBBW,           :LL|4BKBBBBBW@pp     }@@@@L          \n");
		sb.append("        RBBB@,         ,BBM`    TBBw     ]BN       EBL           `BB@          `L|EEBRRRRKKBBM     ]BBKBB,.         \n");
		sb.append("          `TBBBN=      ]BB       jBB     $BM       EBL            ]BB           |4EE8RRRBR\"     ,@BBBBBBBLLLLLLu^`  \n");
		sb.append("              \"BBBW    ]BN       :BB     $BM       EBL    ,@#KBBBBBBB           IEEE5RBM     ,@BBBBBRBRRNLLLu^`     \n");
		sb.append("                 BBN   8BW       :BB     $BM       EBL   4BB      ]BB           !|(5EB    ,4BBBBBBBBEEEEEE@,        \n");
		sb.append("                ,BBR   TBB       4BB     $BM       EBL   ]BN      ]BB           `||EE`    BRRRRRRRRBBBEEEZ@||@pu.   \n");
		sb.append("       @@@===@@KBBM     TBBNw==@BBM      $BM       EBL   TBBW=,=@KBBB             $EE               BBBB@|@|||||||@^\n");
		sb.append("       `\"\"\"TT\"\"^`         `\"\"T\"\"`        \"\"        \"\"      `\"T\"\"`  \"\"            ;@5E,,,,,,,,,,,,,,;BBBB||||||||||P \n");
		sb.append("                                                                                .|||ZEEEEEEEEEEPZ5BBBBBE|||||||||P  \n");
		sb.append("                                                                               .@||||L5EEEEEZ||L|||LZRBE|||||@||P   \n");
		sb.append("                                                                               @|||||||L5ZL|||||||||||   ^tE|@|@`   \n");
		sb.append("                                                                              i||||||||||||||||||||||@      `^E`    \n");
		sb.append("                                                                              ^^tZ55|||||||||||||||||P              \n");
		sb.append("                                                                                     ``^^^tZ55|||||||L              \n");
		sb.append("                                                                                               ``^^^t               \n");
		sb.append("\n");
		sb.append("      ").append("Module ...........: ").append(ArqSpringContext.getModulo()).append("\n");
		sb.append("      ").append("Version...........: ").append(ArqSpringContext.getVersionModulo()).append("\n");
		sb.append("      ").append("Compilation date..: ").append(compilationDate).append("\n");
		sb.append("      ").append("Config directory..: ").append(ArqSpringContext.getConfig()).append("\n");
		sb.append("      ").append("Instance ID.......: ").append(ArqSpringContext.getInstancia()).append("\n");
		sb.append("\n");
	    sb.append("********************************************************************************************************************").append("\n");
	    sb.append("\n");
	    sb.append("\n");
	    sb.append("\n");
	    log.warn(sb.toString());

	}

}
