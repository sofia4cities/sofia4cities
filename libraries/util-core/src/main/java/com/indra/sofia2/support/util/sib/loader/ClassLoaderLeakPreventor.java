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
package com.indra.sofia2.support.util.sib.loader;


import java.beans.PropertyEditorManager;
import java.lang.management.ManagementFactory;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.indra.jee.arq.spring.core.infraestructura.log.I18nLog;
import com.indra.jee.arq.spring.core.infraestructura.log.I18nLogFactory;


/**
 * The Class ClassLoaderLeakPreventor.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ClassLoaderLeakPreventor implements ServletContextListener {
	
	 /** The Constant LOG. */
 	private static final I18nLog LOG = I18nLogFactory.getLogI18n(ClassLoaderLeakPreventor.class);
	
	 /** Default no of milliseconds to wait for threads to finish execution. */
	  public static final int THREAD_WAIT_MS_DEFAULT = 5 * 1000; // 5 seconds

	  /** Default no of milliseconds to wait for shutdown hook to finish execution. */
	  public static final int SHUTDOWN_HOOK_WAIT_MS_DEFAULT = 10 * 1000; // 10 seconds
	  
	  private static final String[] THREAD_WHITELIST_PREFFIXES = {"hz"};
	  
	  private static boolean isWhitelistedThreadName(String name){
		  for (String prefix: THREAD_WHITELIST_PREFFIXES){
			  if (name.startsWith(prefix))
				  return true;
		  }
		  return false;
	  }
	  
	  ///////////
	  // Settings
	  
	  
	  /** Should threads tied to the web app classloader be forced to stop at application shutdown?. */
	  protected boolean stopThreads = true;
	  
	  /** Should Timer threads tied to the web app classloader be forced to stop at application shutdown?. */
	  protected boolean stopTimerThreads = true;
	  
	  /** Should shutdown hooks registered from the application be executed at application shutdown?. */
	  protected boolean executeShutdownHooks = true;

	  /** 
	   * No of milliseconds to wait for threads to finish execution, before stopping them.
	   */
	  protected int threadWaitMs = SHUTDOWN_HOOK_WAIT_MS_DEFAULT;

	  /** 
	   * No of milliseconds to wait for shutdown hooks to finish execution, before stopping them.
	   * If set to -1 there will be no waiting at all, but Thread is allowed to run until finished.
	   */
	  protected int shutdownHookWaitMs = SHUTDOWN_HOOK_WAIT_MS_DEFAULT;

	  /** Is it possible, that we are running under JBoss?. */
	  private boolean mayBeJBoss = false;

	  /**
  	 * Checks if is may be j boss.
  	 *
  	 * @return true, if is may be j boss
  	 */
  	public boolean isMayBeJBoss() {
		return mayBeJBoss;
	}

	/**
	 * Sets the may be j boss.
	 *
	 * @param mayBeJBoss the new may be j boss
	 */
	public void setMayBeJBoss(boolean mayBeJBoss) {
		this.mayBeJBoss = mayBeJBoss;
	}

	/** The java_lang_ thread_thread locals. */
	protected final Field java_lang_Thread_threadLocals;

	  /** The java_lang_ thread_inheritable thread locals. */
  	protected final Field java_lang_Thread_inheritableThreadLocals;

	  /** The java_lang_ thread local1 thread local map_table. */
  	protected final Field java_lang_ThreadLocal1ThreadLocalMap_table;

	  /** The java_lang_ thread local1 thread local map1 entry_value. */
  	protected Field java_lang_ThreadLocal1ThreadLocalMap1Entry_value;

	  /**
  	 * Instantiates a new class loader leak preventor.
  	 */
  	public ClassLoaderLeakPreventor() {
	    // Initialize some reflection variables
	    java_lang_Thread_threadLocals = findField(Thread.class, "threadLocals");
	    java_lang_Thread_inheritableThreadLocals = findField(Thread.class, "inheritableThreadLocals");
	    java_lang_ThreadLocal1ThreadLocalMap_table = findFieldOfClass("java.lang.ThreadLocal$ThreadLocalMap", "table");
	    
	    if(java_lang_Thread_threadLocals == null) {
			LOG.error("java.lang.Thread.threadLocals not found; something is seriously wrong!");
		}
	    
	    if(java_lang_Thread_inheritableThreadLocals == null) {
			LOG.error("java.lang.Thread.inheritableThreadLocals not found; something is seriously wrong!");
		}

	    if(java_lang_ThreadLocal1ThreadLocalMap_table == null) {
			LOG.error("java.lang.ThreadLocal$ThreadLocalMap.table not found; something is seriously wrong!");
		}
	  }

	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  // Implement javax.servlet.ServletContextListener 
	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  
	  /**
  	 * Context initialized.
  	 *
  	 * @param servletContextEvent the servlet context event
  	 */
  	public void contextInitialized(ServletContextEvent servletContextEvent) {
	
	/*    stopThreads = ! "false".equals(servletContext.getInitParameter("ClassLoaderLeakPreventor.stopThreads"));
	    stopTimerThreads = ! "false".equals(servletContext.getInitParameter("ClassLoaderLeakPreventor.stopTimerThreads"));
	    executeShutdownHooks = ! "false".equals(servletContext.getInitParameter("ClassLoaderLeakPreventor.executeShutdownHooks"));
	    threadWaitMs = getIntInitParameter(servletContext, "ClassLoaderLeakPreventor.threadWaitMs", THREAD_WAIT_MS_DEFAULT);
	    shutdownHookWaitMs = getIntInitParameter(servletContext, "ClassLoaderLeakPreventor.shutdownHookWaitMs", SHUTDOWN_HOOK_WAIT_MS_DEFAULT);
	  */  
	    info("Settings for " + this.getClass().getName() + " (CL: 0x" +
	         Integer.toHexString(System.identityHashCode(getWebApplicationClassLoader())) + "):");
	    info("  stopThreads = " + stopThreads);
	    info("  stopTimerThreads = " + stopTimerThreads);
	    info("  executeShutdownHooks = " + executeShutdownHooks);
	    info("  threadWaitMs = " + threadWaitMs + " ms");
	    info("  shutdownHookWaitMs = " + shutdownHookWaitMs + " ms");
	    
	    final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

	    try {
	      // If package org.jboss is found, we may be running under JBoss
	      mayBeJBoss = (contextClassLoader.getResource("org/jboss") != null);
	    }
	    catch(Exception ex) {
	      LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", ex.getMessage());
	    }
	    

	    info("Initializing context by loading some known offenders with system classloader");
	    
	    // This part is heavily inspired by Tomcats JreMemoryLeakPreventionListener  
	    // See http://svn.apache.org/viewvc/tomcat/trunk/java/org/apache/catalina/core/JreMemoryLeakPreventionListener.java?view=markup
	    try {
	      // Switch to system classloader in before we load/call some JRE stuff that will cause 
	      // the current classloader to be available for gerbage collection
	      Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
	      
	      java.awt.Toolkit.getDefaultToolkit(); // Will start a Thread
	      
	      java.security.Security.getProviders();
	      
	      java.sql.DriverManager.getDrivers(); // Load initial drivers using system classloader

	      javax.imageio.ImageIO.getCacheDirectory(); // Will call sun.awt.AppContext.getAppContext()

	      try {
	        Class.forName("javax.security.auth.Policy")
	            .getMethod("getPolicy")
	            .invoke(null);
	      }
	      catch (IllegalAccessException iaex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", iaex.getMessage());
	      }
	      catch (InvocationTargetException itex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", itex.getMessage());
	      }
	      catch (NoSuchMethodException nsmex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", nsmex.getMessage());
	      }
	      catch (ClassNotFoundException e) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", e.getMessage());
	      }

	      try {
	        javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
	      }
	      catch (Exception ex) { // Example: ParserConfigurationException
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", ex.getMessage());
	      }

	      try {
	        Class.forName("javax.security.auth.login.Configuration", true, ClassLoader.getSystemClassLoader());
	      }
	      catch (ClassNotFoundException e) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", e.getMessage());
	      }

	      // This probably does not affect classloaders, but prevents some problems with .jar files
	      try {
	        // URL needs to be well-formed, but does not need to exist
	        new URL("jar:file://dummy.jar!/").openConnection().setDefaultUseCaches(false);
	      }
	      catch (Exception ex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", ex.getMessage());
	      }

	      /////////////////////////////////////////////////////
	      // Load Sun specific classes that may cause leaks
	      
	      
	      
	      try {
	        Class.forName("com.sun.jndi.ldap.LdapPoolManager");
	      }
	      catch(ClassNotFoundException cnfex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", cnfex.getMessage());
	      }

	      try {
	        Class.forName("sun.java2d.Disposer"); // Will start a Thread
	      }
	      catch (ClassNotFoundException cnfex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", cnfex.getMessage());
	      }

	      try {
	        Class<?> gcClass = Class.forName("sun.misc.GC");
	        final Method requestLatency = gcClass.getDeclaredMethod("requestLatency", long.class);
	        requestLatency.invoke(null, 3600000L);
	      }
	      catch (ClassNotFoundException cnfex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", cnfex.getMessage());
	      }
	      catch (NoSuchMethodException nsmex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", nsmex.getMessage());
	      }
	      catch (IllegalAccessException iaex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", iaex.getMessage());
	      }
	      catch (InvocationTargetException itex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextInitialized.error", itex.getMessage());
	      }
	    }
	    finally {
	      // Reset original classloader
	      Thread.currentThread().setContextClassLoader(contextClassLoader);
	    }
	  }

	  /**
  	 * Context destroyed.
  	 *
  	 * @param servletContextEvent the servlet context event
  	 */
  	public void contextDestroyed(ServletContextEvent servletContextEvent) {

	    final boolean jvmIsShuttingDown = isJvmShuttingDown();
	    if(jvmIsShuttingDown) {
	      LOG.info("JVM is shutting down - skip cleanup");
	      return; // Don't do anything more
	    }

	    LOG.info(getClass().getName() + " shutting down context by removing known leaks (CL: 0x" + 
	         Integer.toHexString(System.identityHashCode(getWebApplicationClassLoader())) + ")");
	    
	    //////////////////
	    // Fix known leaks
	    //////////////////
	    
	    java.beans.Introspector.flushCaches(); // Clear cache of strong references
	    
	    // Apache Commons Pool can leave unfinished threads. Anything specific we can do?
	    
	    clearBeanELResolverCache();

	    fixBeanValidationApiLeak();

	    fixGeoToolsLeak();
	    
	    // Can we do anything about Google Guice ?
	    
	    // Can we do anything about Groovy http://jira.codehaus.org/browse/GROOVY-4154 ?

	    clearIntrospectionUtilsCache();

	    // Can we do anything about Logback http://jira.qos.ch/browse/LBCORE-205 ?

	    ////////////////////
	    // Fix generic leaks
	    
	    // Deregister JDBC drivers contained in web application
	    deregisterJdbcDrivers();
	    
	    // Unregister MBeans loaded by the web application class loader
	    unregisterMBeans();
	    
//	    // Deregister shutdown hooks - execute them immediately
//	    deregisterShutdownHooks();
	    
	    deregisterPropertyEditors();

	    deregisterSecurityProviders();
	    
	    clearDefaultAuthenticator();
	    
	    deregisterRmiTargets();
	    
	    clearThreadLocalsOfAllThreads();
	    
	    stopThreadsMetodo();

	    try {
	      try { // First try Java 1.6 method
	        final Method clearCache16 = ResourceBundle.class.getMethod("clearCache", ClassLoader.class);
	        debug("Since Java 1.6+ is used, we can call " + clearCache16);
	        clearCache16.invoke(null, getWebApplicationClassLoader());
	      }
	      catch (NoSuchMethodException e) {
	        // Not Java 1.6+, we have to clear manually
	        final Map<?,?> cacheList = getStaticFieldValue(ResourceBundle.class, "cacheList"); // Java 5: SoftCache extends AbstractMap
	        final Iterator<?> iter = cacheList.keySet().iterator();
	        Field loaderRefField = null;
	        while(iter.hasNext()) {
	          Object key = iter.next(); // CacheKey
	          
	          if(loaderRefField == null) { // First time
	            loaderRefField = key.getClass().getDeclaredField("loaderRef");
	            loaderRefField.setAccessible(true);
	          }
	          WeakReference<ClassLoader> loaderRef = (WeakReference<ClassLoader>) loaderRefField.get(key); // LoaderReference extends WeakReference
	          ClassLoader classLoader = loaderRef.get();
	          
	          if(isWebAppClassLoaderOrChild(classLoader)) {
	            info("Removing ResourceBundle from cache: " + key);
	            iter.remove();
	          }
	          
	        }
	      }
	    }
	    catch(Exception ex) {
	    	 LOG.debug("ClassLoaderLeakPreventor.contextDestroyed.error", ex.getMessage());
	    }
	    
	    // Release this classloader from Apache Commons Logging (ACL) by calling
	    //   LogFactory.release(getCurrentClassLoader());
	    // Use reflection in case ACL is not present.
	    // Do this last, in case other shutdown procedures want to log something.
	    
	    final Class logFactory = findClass("org.apache.commons.logging.LogFactory");
	    if(logFactory != null) { // Apache Commons Logging present
	      info("Releasing web app classloader from Apache Commons Logging");
	      try {
	        logFactory.getMethod("release", java.lang.ClassLoader.class)
	            .invoke(null, getWebApplicationClassLoader());
	      }
	      catch (Exception ex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.contextDestroyed.error", ex.getMessage());
	      }
	    }
	    
	  }

	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  // Fix generic leaks
	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  
	  /**
  	 * Deregister JDBC drivers loaded by web app classloader.
  	 */
	  public void deregisterJdbcDrivers() {
	    final List<Driver> driversToDeregister = new ArrayList<Driver>();
	    final Enumeration<Driver> allDrivers = DriverManager.getDrivers();
	    while(allDrivers.hasMoreElements()) {
	      final Driver driver = allDrivers.nextElement();
	      if(isLoadedInWebApplication(driver)) {
			driversToDeregister.add(driver);
		}
	    }
	    
	    for(Driver driver : driversToDeregister) {
	      try {
	        warn("JDBC driver loaded by web app deregistered: " + driver.getClass());
	        DriverManager.deregisterDriver(driver);
	      }
	      catch (SQLException e) {
	    	  LOG.debug("ClassLoaderLeakPreventor.deregisterJdbcDrivers.error", e.getMessage());
	      }
	    }
	  }

	  /**
  	 * Unregister MBeans loaded by the web application class loader.
  	 */
	  protected void unregisterMBeans() {
	    try {
	      MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
	      final Set<ObjectName> allMBeanNames = mBeanServer.queryNames(new ObjectName("*:*"), null);
	      for(ObjectName objectName : allMBeanNames) {
	        try {
	          final ClassLoader mBeanClassLoader = mBeanServer.getClassLoaderFor(objectName);
	          if(isWebAppClassLoaderOrChild(mBeanClassLoader)) { // MBean loaded in web application
	            warn("MBean '" + objectName + "' was loaded in web application; unregistering");
	            mBeanServer.unregisterMBean(objectName);
	          }
	        }
	        catch(Exception e) { // MBeanRegistrationException / InstanceNotFoundException
	        	LOG.debug("ClassLoaderLeakPreventor.unregisterMBeans.error", e.getMessage());
	        }
	      }
	    }
	    catch (Exception e) { // MalformedObjectNameException
	    	LOG.debug("ClassLoaderLeakPreventor.unregisterMBeans.error", e.getMessage());
	    }
	  }

	  /** Find and deregister shutdown hooks. Will by default execute the hooks after removing them. */
	  protected void deregisterShutdownHooks() {
	    // We will not remove known shutdown hooks, since loading the owning class of the hook,
	    // may register the hook if previously unregistered 
	    Map<Thread, Thread> shutdownHooks = (Map<Thread, Thread>) getStaticFieldValue("java.lang.ApplicationShutdownHooks", "hooks");
	    if(shutdownHooks != null) { // Could be null during JVM shutdown, which we already avoid, but be extra precautious
	      // Iterate copy to avoid ConcurrentModificationException
	      for(Thread shutdownHook : new ArrayList<Thread>(shutdownHooks.keySet())) {
	        if(isThreadInWebApplication(shutdownHook)) { // Planned to run in web app          
	          removeShutdownHook(shutdownHook);
	        }
	      }
	    }
	  }

	  /**
  	 * Deregister shutdown hook and execute it immediately.
  	 *
  	 * @param shutdownHook the shutdown hook
  	 */
	  @SuppressWarnings("deprecation")
	  protected void removeShutdownHook(Thread shutdownHook) {
	    final String displayString = "'" + shutdownHook + "' of type " + shutdownHook.getClass().getName();
	    error("Removing shutdown hook: " + displayString);
	    Runtime.getRuntime().removeShutdownHook(shutdownHook);

	    if(executeShutdownHooks) { // Shutdown hooks should be executed
	      
	      info("Executing shutdown hook now: " + displayString);
	      // Make sure it's from this web app instance
	      shutdownHook.start(); // Run cleanup immediately
	      
	      if(shutdownHookWaitMs > 0) { // Wait for shutdown hook to finish
	        try {
	          shutdownHook.join(shutdownHookWaitMs); // Wait for thread to run
	        }
	        catch (InterruptedException e) {
	        	LOG.debug("ClassLoaderLeakPreventor.removeShutdownHook.error", e.getMessage());
	        }
	        if(shutdownHook.isAlive()) {
	          warn(shutdownHook + "still running after " + shutdownHookWaitMs + " ms - Stopping!");
	          shutdownHook.stop();
	        }
	      }
	    }
	  }

	  /**
  	 * Deregister custom property editors.
  	 */
	  protected void deregisterPropertyEditors() {
	    final Field registryField = findField(PropertyEditorManager.class, "registry");
	    if(registryField == null) {
	      error("Internal registry of " + PropertyEditorManager.class.getName() + " not found");
	    }
	    else {
	      try {
	        synchronized (PropertyEditorManager.class) {
	          final Map<Class<?>, Class<?>> registry = (Map) registryField.get(null);
	          if(registry != null) { // Initialized
	            final Set<Class> toRemove = new HashSet<Class>();
	            
	            for(Map.Entry<Class<?>, Class<?>> entry : registry.entrySet()) {
	              if(isLoadedByWebApplication(entry.getKey()) ||
	                 isLoadedByWebApplication(entry.getValue())) { // More likely
	                toRemove.add(entry.getKey());
	              }
	            }
	            
	            for(Class clazz : toRemove) {
	              warn("Property editor for type " + clazz +  " = " + registry.get(clazz) + " needs to be deregistered");
	              PropertyEditorManager.registerEditor(clazz, null); // Deregister
	            }
	          }
	        }
	      }
	      catch (Exception e) { // Such as IllegalAccessException
	    	  LOG.debug("ClassLoaderLeakPreventor.deregisterPropertyEditors.error", e.getMessage());
	      }
	    }
	  }
	  
	  /**
  	 * Deregister custom security providers.
  	 */
	  protected void deregisterSecurityProviders() {
	    final Set<String> providersToRemove = new HashSet<String>();
	    for(java.security.Provider provider : java.security.Security.getProviders()) {
	      if(isLoadedInWebApplication(provider)) {
	        providersToRemove.add(provider.getName());
	      }
	    }
	    
	    if(! providersToRemove.isEmpty()) {
	      warn("Removing security providers loaded in web app: " + providersToRemove);
	      for(String providerName : providersToRemove) {
	        java.security.Security.removeProvider(providerName);
	      }
	    }
	  }
	  
	  /** Clear the default java.net.Authenticator (in case current one is loaded in web app) */
	  protected void clearDefaultAuthenticator() {
	    final Authenticator defaultAuthenticator = getStaticFieldValue(Authenticator.class, "theAuthenticator");
	    if(defaultAuthenticator == null || // Can both mean not set, or error retrieving, so unset anyway to be safe 
	       isLoadedInWebApplication(defaultAuthenticator)) {
	      Authenticator.setDefault(null);
	    }
	  }

	  /** This method is heavily inspired by org.apache.catalina.loader.WebappClassLoader.clearReferencesRmiTargets() */
	  protected void deregisterRmiTargets() {
	    try {
	      final Class objectTableClass = findClass("sun.rmi.transport.ObjectTable");
	      if(objectTableClass != null) {
	        clearRmiTargetsMap((Map<?, ?>) getStaticFieldValue(objectTableClass, "objTable"));
	        clearRmiTargetsMap((Map<?, ?>) getStaticFieldValue(objectTableClass, "implTable"));
	      }
	    }
	    catch (Exception ex) {
	    	LOG.debug("ClassLoaderLeakPreventor.deregisterRmiTargets.error", ex.getMessage());
	    }
	  }
	  
	  /**
  	 * Iterate RMI Targets Map and remove entries loaded by web app classloader.
  	 *
  	 * @param rmiTargetsMap the rmi targets map
  	 */
	  protected void clearRmiTargetsMap(Map<?, ?> rmiTargetsMap) {
	    try {
	      final Field cclField = findFieldOfClass("sun.rmi.transport.Target", "ccl");
	      debug("Looping " + rmiTargetsMap.size() + " RMI Targets to find leaks");
	      for(Iterator<?> iter = rmiTargetsMap.values().iterator(); iter.hasNext(); ) {
	        Object target = iter.next(); // sun.rmi.transport.Target
	        ClassLoader ccl = (ClassLoader) cclField.get(target);
	        if(isWebAppClassLoaderOrChild(ccl)) {
	          warn("Removing RMI Target: " + target);
	          iter.remove();
	        }
	      }
	    }
	    catch (Exception ex) {
	    	LOG.debug("ClassLoaderLeakPreventor.clearRmiTargetsMap.error", ex.getMessage());
	    }
	  }

	  /**
  	 * Clear thread locals of all threads.
  	 */
  	protected void clearThreadLocalsOfAllThreads() {
	    final ThreadLocalProcessor clearingThreadLocalProcessor = new ClearingThreadLocalProcessor();
	    for(Thread thread : getAllThreads()) {
	      forEachThreadLocalInThread(thread, clearingThreadLocalProcessor);
	    }
	  }

	  /**
	   * Partially inspired by org.apache.catalina.loader.WebappClassLoader.clearReferencesThreads()
	   */
	  @SuppressWarnings("deprecation")
	  protected void stopThreadsMetodo() {
	    final Class<?> workerClass = findClass("java.util.concurrent.ThreadPoolExecutor$Worker");
	    final Field targetField = findField(Thread.class, "target");

	    for(Thread thread : getAllThreads()) {
	      if (!isWhitelistedThreadName(thread.getName())) {
		      final Runnable target = getFieldValue(targetField, thread);
		      if(thread != Thread.currentThread() && // Ignore current thread
		         (isThreadInWebApplication(thread) || isLoadedInWebApplication(target))) {
	
		        if(thread.getThreadGroup() != null && 
		           ("system".equals(thread.getThreadGroup().getName()) ||  // System thread
		            "RMI Runtime".equals(thread.getThreadGroup().getName()))) { // RMI thread (honestly, just copied from Tomcat)
		          
		          if("Keep-Alive-Timer".equals(thread.getName())) {
		            thread.setContextClassLoader(getWebApplicationClassLoader().getParent());
		            debug("Changed contextClassLoader of HTTP keep alive thread");
		          }
		        }
		        else if(thread.isAlive()) { // Non-system, running in web app
		        
		          if("java.util.TimerThread".equals(thread.getClass().getName())) {
		            if(stopTimerThreads) {
		              warn("Stopping Timer thread running in classloader.");
		              stopTimerThread(thread);
		            }
		            else {
		              info("Timer thread is running in classloader, but will not be stopped");
		            }
		          }
		          else {
		            
		            // If threads is running an java.util.concurrent.ThreadPoolExecutor.Worker try shutting down the executor
		            if(workerClass != null && workerClass.isInstance(target)) {
		              if(stopThreads) {
		                warn("Shutting down " + ThreadPoolExecutor.class.getName() + " running within the classloader.");
		                try {
		                  // java.util.concurrent.ThreadPoolExecutor, introduced in Java 1.5
		                  final Field workerExecutor = findField(workerClass, "this$0");
		                  final ThreadPoolExecutor executor = getFieldValue(workerExecutor, target);
		                  executor.shutdownNow();
		                }
		                catch (Exception ex) {
		                	LOG.debug("ClassLoaderLeakPreventor.stopThreads.error", ex.getMessage());
		                }
		              } else {
						info(ThreadPoolExecutor.class.getName() + " running within the classloader will not be shut down.");
					}
		            }
	
		            final String displayString = "'" + thread + "' of type " + thread.getClass().getName();
		            
		            if(stopThreads) {
		              
		              //Otroman, comentamos el código el tiempo de espera para que acabe el método run del thread
		              //(threads del pool nacar o quartz fuera del control del servidor de aplicaciones)	
		              //final String waitString = (threadWaitMs > 0) ? "after " + threadWaitMs + " ms " : "";
		              //warn("Stopping Thread " + displayString + " running in web app " + waitString);
		            	
		              warn("Stopping Thread " + displayString + " running in web app ");
	              
	//	              if(threadWaitMs > 0) {
	//	                try {
	//	                  thread.join(threadWaitMs); // Wait for thread to run
	//	                }
	//	                catch (InterruptedException e) {
	//	                	LOG.debug("ClassLoaderLeakPreventor.stopThreads.error", e.getMessage());
	//	                }
	//	              }
	
		              // Normally threads should not be stopped (method is deprecated), since it may cause an inconsistent state.
		              // In this case however, the alternative is a classloader leak, which may or may not be considered worse.
		              if(thread.isAlive()) {
						thread.stop();
					}
		            }
		            else {
		              warn("Thread " + displayString + " is still running in web app");
		            }
		              
		          }
		        }
		      }
		    }
	    } 
	  }

	  /**
  	 * Stop timer thread.
  	 *
  	 * @param thread the thread
  	 */
  	protected void stopTimerThread(Thread thread) {
	    // Seems it is not possible to access Timer of TimerThread, so we need to mimic Timer.cancel()
	    /** 
	    try {
	      Timer timer = (Timer) findField(thread.getClass(), "this$0").get(thread); // This does not work!
	      warn("Cancelling Timer " + timer + " / TimeThread '" + thread + "'");
	      timer.cancel();
	    }
	    catch (IllegalAccessException iaex) {
	      error(iaex);
	    }
	    */

	    try {
	      final Field newTasksMayBeScheduled = findField(thread.getClass(), "newTasksMayBeScheduled");
	      final Object queue = findField(thread.getClass(), "queue").get(thread); // java.lang.TaskQueue
	      final Method clear = queue.getClass().getDeclaredMethod("clear");
	      clear.setAccessible(true);

	      // Do what java.util.Timer.cancel() does
	      //noinspection SynchronizationOnLocalVariableOrMethodParameter
	      synchronized (queue) {
	        newTasksMayBeScheduled.set(thread, false);
	        clear.invoke(queue);
	        queue.notify(); // "In case queue was already empty."
	      }
	      
	      // We shouldn't need to join() here, thread will finish soon enough
	    }
	    catch (Exception ex) {
	    	LOG.debug("ClassLoaderLeakPreventor.stopTimerThread.error", ex.getMessage());
	    }
	  }
	  
	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  // Fix specific leaks
	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	  /**
  	 * Clean the cache of BeanELResolver.
  	 */
	  protected void clearBeanELResolverCache() {
	    final Class beanElResolverClass = findClass("javax.el.BeanELResolver");
	    if(beanElResolverClass != null) {
	      boolean cleared = false;
	      try {
	        final Method purgeBeanClasses = beanElResolverClass.getDeclaredMethod("purgeBeanClasses", ClassLoader.class);
	        purgeBeanClasses.setAccessible(true);
	        purgeBeanClasses.invoke(beanElResolverClass.newInstance(), getWebApplicationClassLoader());
	        cleared = true;
	      }
	      catch (NoSuchMethodException e) {
	    	  LOG.debug("ClassLoaderLeakPreventor.clearBeanELResolverCache.error", e.getMessage());
	      }
	      catch (Exception e) {
	    	  LOG.debug("ClassLoaderLeakPreventor.clearBeanELResolverCache.error", e.getMessage());
	      }
	      
	      if(! cleared) {
	        // Fallback, if purgeBeanClasses() could not be called
	        final Field propertiesField = findField(beanElResolverClass, "properties");
	        if(propertiesField != null) {
	          try {
	            final Map properties = (Map) propertiesField.get(null);
	            properties.clear();
	          }
	          catch (Exception e) {
	        	  LOG.debug("ClassLoaderLeakPreventor.clearBeanELResolverCache.error", e.getMessage());
	          }
	        }
	      }
	    }
	  }
	  
	  /**
  	 * Fix bean validation api leak.
  	 */
  	public void fixBeanValidationApiLeak() {
	    Class offendingClass = findClass("javax.validation.Validation$DefaultValidationProviderResolver");
	    if(offendingClass != null) { // Class is present on class path
	      Field offendingField = findField(offendingClass, "providersPerClassloader");
	      if(offendingField != null) {
	        final Object providersPerClassloader = getStaticFieldValue(offendingField);
	        if(providersPerClassloader instanceof Map) { // Map<ClassLoader, List<ValidationProvider<?>>> in offending code
	          //noinspection SynchronizationOnLocalVariableOrMethodParameter
	          synchronized (providersPerClassloader) {
	            // Fix the leak!
	            ((Map)providersPerClassloader).remove(getWebApplicationClassLoader());
	          }
	        }
	      }
	    }
	    
	  }
	  
	  /** Shutdown GeoTools cleaner thread as of http://jira.codehaus.org/browse/GEOT-2742 */
	  protected void fixGeoToolsLeak() {
	    final Class weakCollectionCleanerClass = findClass("org.geotools.util.WeakCollectionCleaner");
	    if(weakCollectionCleanerClass != null) {
	      try {
	        weakCollectionCleanerClass.getMethod("exit").invoke(null);
	      }
	      catch (Exception ex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.fixGeoToolsLeak.error", ex.getMessage());
	      }
	    }
	  }

	  /**
  	 * Clear IntrospectionUtils caches of Tomcat and Apache Commons Modeler.
  	 */
	  protected void clearIntrospectionUtilsCache() {
	    // Tomcat
	    final Class tomcatIntrospectionUtils = findClass("org.apache.tomcat.util.IntrospectionUtils");
	    if(tomcatIntrospectionUtils != null) {
	      try {
	        tomcatIntrospectionUtils.getMethod("clear").invoke(null);
	      }
	      catch (Exception ex) {
	    	  LOG.debug("ClassLoaderLeakPreventor.clearIntrospectionUtilsCache.error", ex.getMessage());
	      }
	    }

	    // Apache Commons Modeler
	    final Class modelIntrospectionUtils = findClass("org.apache.commons.modeler.util.IntrospectionUtils");
	    if(modelIntrospectionUtils != null && ! isWebAppClassLoaderOrChild(modelIntrospectionUtils.getClassLoader())) { // Loaded outside web app
	      try {
	        modelIntrospectionUtils.getMethod("clear").invoke(null);
	      }
	      catch (Exception ex) {
	        LOG.debug("ClassLoaderLeakPreventor.clearIntrospectionUtilsCache.error", ex.getMessage());
	      }
	    }
	  }

	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  // Utility methods
	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  
	  /**
  	 * Gets the web application class loader.
  	 *
  	 * @return the web application class loader
  	 */
  	protected ClassLoader getWebApplicationClassLoader() {
	    return Thread.currentThread().getContextClassLoader();
	    // Alternative return Thread.currentThread().getContextClassLoader();
	  }
	  
	  /**
  	 * Test if provided object is loaded with web application classloader.
  	 *
  	 * @param o the o
  	 * @return true, if is loaded in web application
  	 */
	  protected boolean isLoadedInWebApplication(Object o) {
	    return o != null && isLoadedByWebApplication(o.getClass());
	  }

	  /**
  	 * Test if provided class is loaded with web application classloader.
  	 *
  	 * @param clazz the clazz
  	 * @return true, if is loaded by web application
  	 */
	  protected boolean isLoadedByWebApplication(Class clazz) {
	    return clazz != null && isWebAppClassLoaderOrChild(clazz.getClassLoader());
	  }

	  /**
  	 * Test if provided ClassLoader is the classloader of the web application, or a child thereof.
  	 *
  	 * @param cl the cl
  	 * @return true, if is web app class loader or child
  	 */
	  protected boolean isWebAppClassLoaderOrChild(ClassLoader cl) {
		
		  ClassLoader clTransformado = cl;
		  
	    final ClassLoader webAppCL = getWebApplicationClassLoader();
	    // final ClassLoader webAppCL = Thread.currentThread().getContextClassLoader();

	    while(clTransformado != null) {
	      if(clTransformado == webAppCL) {
			return true;
		}
	      
	      clTransformado = clTransformado.getParent();
	    }

	    return false;
	  }

	  /**
  	 * Checks if is thread in web application.
  	 *
  	 * @param thread the thread
  	 * @return true, if is thread in web application
  	 */
  	protected boolean isThreadInWebApplication(Thread thread) {
	    return isLoadedInWebApplication(thread) || // Custom Thread class in web app
	       isWebAppClassLoaderOrChild(thread.getContextClassLoader()); // Running in web application
	  }
	  
	  /**
  	 * Gets the static field value.
  	 *
  	 * @param <E> the element type
  	 * @param clazz the clazz
  	 * @param fieldName the field name
  	 * @return the static field value
  	 */
  	protected <E> E getStaticFieldValue(Class clazz, String fieldName) {
	    Field staticField = findField(clazz, fieldName);
	    return (staticField != null) ? (E) getStaticFieldValue(staticField) : null;
	  }

	  /**
  	 * Gets the static field value.
  	 *
  	 * @param <E> the element type
  	 * @param className the class name
  	 * @param fieldName the field name
  	 * @return the static field value
  	 */
  	protected <E> E getStaticFieldValue(String className, String fieldName) {
	    Field staticField = findFieldOfClass(className, fieldName);
	    return (staticField != null) ? (E) getStaticFieldValue(staticField) : null;
	  }
	  
	  /**
  	 * Find field of class.
  	 *
  	 * @param className the class name
  	 * @param fieldName the field name
  	 * @return the field
  	 */
  	protected Field findFieldOfClass(String className, String fieldName) {
	    Class clazz = findClass(className);
	    if(clazz != null) {
	      return findField(clazz, fieldName);
	    } else {
			return null;
		}
	  }
	  
	  /**
  	 * Find class.
  	 *
  	 * @param className the class name
  	 * @return the class
  	 */
  	protected Class findClass(String className) {
	    try {
	      return Class.forName(className);
	    }
//	    catch (NoClassDefFoundError e) {
//	      // Silently ignore
//	      return null;
//	    }
	    catch (ClassNotFoundException e) {
	    	LOG.debug("ClassLoaderLeakPreventor.findClass.error", e.getMessage());
	      return null;
	    }
	    catch (Exception ex) { // Example SecurityException
	    	LOG.debug("ClassLoaderLeakPreventor.findClass.error", ex.getMessage());
	      return null;
	    }
	  }
	  
	  /**
  	 * Find field.
  	 *
  	 * @param clazz the clazz
  	 * @param fieldName the field name
  	 * @return the field
  	 */
  	protected Field findField(Class clazz, String fieldName) {
	    if(clazz == null) {
			return null;
		}

	    try {
	      final Field field = clazz.getDeclaredField(fieldName);
	      field.setAccessible(true); // (Field is probably private) 
	      return field;
	    }
	    catch (NoSuchFieldException ex) {
	      LOG.debug("ClassLoaderLeakPreventor.findField.error", ex.getMessage());
	      return null;
	    }
	    catch (Exception ex) { // Example SecurityException
	      LOG.debug("ClassLoaderLeakPreventor.findField.error", ex.getMessage());
	      return null;
	    }
	  }
	  
	  /**
  	 * Gets the static field value.
  	 *
  	 * @param <T> the generic type
  	 * @param field the field
  	 * @return the static field value
  	 */
  	protected <T> T getStaticFieldValue(Field field) {
	    try {
	      return (T) field.get(null);
	    }
	    catch (Exception ex) {
	      LOG.debug("ClassLoaderLeakPreventor.getStaticFieldValue.error", ex.getMessage());
	      return null;
	    }
	  }
	  
	  /**
  	 * Gets the field value.
  	 *
  	 * @param <T> the generic type
  	 * @param field the field
  	 * @param obj the obj
  	 * @return the field value
  	 */
  	protected <T> T getFieldValue(Field field, Object obj) {
	    try {
	      return (T) field.get(obj);
	    }
	    catch (Exception ex) {
	      LOG.debug("ClassLoaderLeakPreventor.getFieldValue.error", ex.getMessage());
	      return null;
	    }
	  }
	  
	  /**
  	 * Is the JVM currently shutting down?.
  	 *
  	 * @return true, if is jvm shutting down
  	 */
	  protected boolean isJvmShuttingDown() {
	    try {
	      final Thread dummy = new Thread(); // Will never be started
	      Runtime.getRuntime().removeShutdownHook(dummy);
	      return false;
	    }
	    catch (IllegalStateException isex) {
	      LOG.debug("ClassLoaderLeakPreventor.isJvmShuttingDown.error", isex.getMessage());
	      return true; // Shutting down
	    }
	    catch (Exception t) { // Any other Exception, assume we are not shutting down
	      LOG.debug("ClassLoaderLeakPreventor.isJvmShuttingDown.error", t.getMessage());
	      return false;
	    }
	  }

	  /**
  	 * Get a Collection with all Threads.
  	 * This method is heavily inspired by org.apache.catalina.loader.WebappClassLoader.getThreads()
  	 *
  	 * @return the all threads
  	 */
	  protected Collection<Thread> getAllThreads() {
	    // This is some orders of magnitude slower...
	    // return Thread.getAllStackTraces().keySet();
	    
	    // Find root ThreadGroup
	    ThreadGroup tg = Thread.currentThread().getThreadGroup();
	    while(tg.getParent() != null) {
			tg = tg.getParent();
		}
	    
	    // Note that ThreadGroup.enumerate() silently ignores all threads that does not fit into array
	    int guessThreadCount = tg.activeCount() + 50;
	    Thread[] threads = new Thread[guessThreadCount];
	    int actualThreadCount = tg.enumerate(threads);
	    while(actualThreadCount == guessThreadCount) { // Map was filled, there may be more
	      guessThreadCount *= 2;
	      threads = new Thread[guessThreadCount];
	      actualThreadCount = tg.enumerate(threads);
	    }
	    
	    // Filter out nulls
	    final List<Thread> output = new ArrayList<Thread>();
	    for(Thread t : threads) {
	      if(t != null) {
	        output.add(t);
	      }
	    }
	    return output;
	  }
	  
	  /**
  	 * Loop ThreadLocals and inheritable ThreadLocals in current Thread
  	 * and for each found, invoke the callback interface.
  	 *
  	 * @param threadLocalProcessor the thread local processor
  	 */
	  protected void forEachThreadLocalInCurrentThread(ThreadLocalProcessor threadLocalProcessor) {
	    final Thread thread = Thread.currentThread();

	    forEachThreadLocalInThread(thread, threadLocalProcessor);
	  }

	  /**
  	 * For each thread local in thread.
  	 *
  	 * @param thread the thread
  	 * @param threadLocalProcessor the thread local processor
  	 */
  	protected void forEachThreadLocalInThread(Thread thread, ThreadLocalProcessor threadLocalProcessor) {
	    try {
	      if(java_lang_Thread_threadLocals != null) {
	        processThreadLocalMap(thread, threadLocalProcessor, java_lang_Thread_threadLocals.get(thread));
	      }

	      if(java_lang_Thread_inheritableThreadLocals != null) {
	        processThreadLocalMap(thread, threadLocalProcessor, java_lang_Thread_inheritableThreadLocals.get(thread));
	      }
	    }
	    catch (/*IllegalAccess*/Exception ex) {
	    	LOG.debug("ClassLoaderLeakPreventor.forEachThreadLocalInThread.error", ex.getMessage());
	    }
	  }

	  /**
  	 * Process thread local map.
  	 *
  	 * @param thread the thread
  	 * @param threadLocalProcessor the thread local processor
  	 * @param threadLocalMap the thread local map
  	 * @throws IllegalAccessException the illegal access exception
  	 */
  	protected void processThreadLocalMap(Thread thread, ThreadLocalProcessor threadLocalProcessor, Object threadLocalMap) throws IllegalAccessException {
	    if(threadLocalMap != null && java_lang_ThreadLocal1ThreadLocalMap_table != null) {
	      final Object[] threadLocalMapTable = (Object[]) java_lang_ThreadLocal1ThreadLocalMap_table.get(threadLocalMap); // java.lang.ThreadLocal.ThreadLocalMap.Entry[]
	      for(Object entry : threadLocalMapTable) {
	        if(entry != null) {
	          // Key is kept in WeakReference
	          Reference reference = (Reference) entry;
	          final ThreadLocal<?> threadLocal = (ThreadLocal<?>) reference.get();

	          if(java_lang_ThreadLocal1ThreadLocalMap1Entry_value == null) {
	            java_lang_ThreadLocal1ThreadLocalMap1Entry_value = findField(entry.getClass(), "value");
	          }
	          
	          final Object value = java_lang_ThreadLocal1ThreadLocalMap1Entry_value.get(entry);

	          threadLocalProcessor.process(thread, reference, threadLocal, value);
	        }
	      }
	    }
	  }

	  /**
  	 * The Interface ThreadLocalProcessor.
  	 */
  	protected interface ThreadLocalProcessor {
	    
    	/**
    	 * Process.
    	 *
    	 * @param thread the thread
    	 * @param entry the entry
    	 * @param threadLocal the thread local
    	 * @param value the value
    	 */
    	void process(Thread thread, Reference entry, ThreadLocal<?> threadLocal, Object value);
	  }

	  /**
  	 * ThreadLocalProcessor that detects and warns about potential leaks.
  	 */
	  protected class WarningThreadLocalProcessor implements ThreadLocalProcessor {
	    
    	/* (non-Javadoc)
    	 * @see com.bbva.jee.arq.spring.core.util.ClassLoaderLeakPreventor.ThreadLocalProcessor#process(java.lang.Thread, java.lang.ref.Reference, java.lang.ThreadLocal, java.lang.Object)
    	 */
    	public final void process(Thread thread, Reference entry, ThreadLocal<?> threadLocal, Object value) {
	      final boolean customThreadLocal = isLoadedInWebApplication(threadLocal); // This is not an actual problem
	      final boolean valueLoadedInWebApp = isLoadedInWebApplication(value);
	      if(customThreadLocal || valueLoadedInWebApp ||
	         (value instanceof ClassLoader && isWebAppClassLoaderOrChild((ClassLoader) value))) { // The value is classloader (child) itself
	        // This ThreadLocal is either itself loaded by the web app classloader, or it's value is
	        // Let's do something about it
	        
	        StringBuilder message = new StringBuilder();
	        if(threadLocal != null) {
	          if(customThreadLocal) {
	            message.append("Custom ");
	          }
	          message.append("ThreadLocal of type ").append(threadLocal.getClass().getName()).append(": ").append(threadLocal);
	        }
	        else {
	          message.append("Unknown ThreadLocal");
	        }
	        message.append(" with value ").append(value);
	        if(value != null) {
	          message.append(" of type ").append(value.getClass().getName());
	          if(valueLoadedInWebApp) {
				message.append(" that is loaded by web app");
			}
	        }

	        warn(message.toString());
	        
	        processFurther(thread, entry, threadLocal, value); // Allow subclasses to perform further processing
	      }
	    }
	    
	    /**
    	 * After having detected potential ThreadLocal leak and warned about it, this method is called.
    	 * Subclasses may override this method to perform further processing, such as clean up.
    	 *
    	 * @param thread the thread
    	 * @param entry the entry
    	 * @param threadLocal the thread local
    	 * @param value the value
    	 */
	    protected void processFurther(Thread thread, Reference entry, ThreadLocal<?> threadLocal, Object value) {
	      // To be overridden in subclass
	    } 
	  }
	  
	  /**
  	 * ThreadLocalProcessor that not only detects and warns about potential leaks, but also tries to clear them.
  	 */
	  protected class ClearingThreadLocalProcessor extends WarningThreadLocalProcessor {
	    
    	/* (non-Javadoc)
    	 * @see com.bbva.jee.arq.spring.core.util.ClassLoaderLeakPreventor.WarningThreadLocalProcessor#processFurther(java.lang.Thread, java.lang.ref.Reference, java.lang.ThreadLocal, java.lang.Object)
    	 */
    	public void processFurther(Thread thread, Reference entry, ThreadLocal<?> threadLocal, Object value) {
	      if(threadLocal != null && thread == Thread.currentThread()) { // If running for current thread and we have the ThreadLocal ...
	        // ... remove properly
	        info("  Will be remove()d");
	        threadLocal.remove();
	      }
	      else { // We cannot remove entry properly, so just make it stale
	        info("  Will be made stale for later expunging");
	        entry.clear(); // Clear the key

	        if(java_lang_ThreadLocal1ThreadLocalMap1Entry_value == null) {
	          java_lang_ThreadLocal1ThreadLocalMap1Entry_value = findField(entry.getClass(), "value");
	        }

	        try {
	          java_lang_ThreadLocal1ThreadLocalMap1Entry_value.set(entry, null); // Clear value to avoid circular references
	        }
	        catch (IllegalAccessException iaex) {
	        	LOG.debug("ClassLoaderLeakPreventor.ClearingThreadLocalProcessor.error", iaex.getMessage());
	        }
	      }
	    }
	  }

	  /**
  	 * Parse init parameter for integer value, returning default if not found or invalid.
  	 *
  	 * @param servletContext the servlet context
  	 * @param parameterName the parameter name
  	 * @param defaultValue the default value
  	 * @return the int init parameter
  	 */
	  protected static int getIntInitParameter(ServletContext servletContext, String parameterName, int defaultValue) {
	    final String parameterString = servletContext.getInitParameter(parameterName);
	    if(parameterString != null && parameterString.trim().length() > 0) {
	      try {
	        return Integer.parseInt(parameterString);
	      }
	      catch (NumberFormatException e) {
	    	  LOG.debug("ClassLoaderLeakPreventor.getIntInitParameter.error", e.getMessage());
	      }
	    }
	    return defaultValue;
	  }

	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  // Log methods
	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  
	  /*
	   * Since logging frameworks are part of the problem, we don't want to depend on any of them here.
	   * Feel free however to subclass or fork and use a log framework, in case you think you know what you're doing.
	   */
	  
	  /**
  	 * Gets the log prefix.
  	 *
  	 * @return the log prefix
  	 */
  	protected String getLogPrefix() {
	    return ClassLoaderLeakPreventor.class.getSimpleName() + ": ";
	  }
	  
	  /**
  	 * Debug.
  	 *
  	 * @param s the s
  	 */
  	protected void debug(String s) {
	    LOG.debug(getLogPrefix() + s);
	  } 

	  /**
  	 * Info.
  	 *
  	 * @param s the s
  	 */
  	protected void info(String s) {
		  LOG.info(getLogPrefix() + s);
	  } 

	  /**
  	 * Warn.
  	 *
  	 * @param s the s
  	 */
  	protected void warn(String s) {
	    LOG.info(getLogPrefix() + s); // Changed
	  } 

	  /**
  	 * Warn.
  	 *
  	 * @param t the t
  	 */
  	protected void warn(Throwable t) {
	    // LOG.wart.printStackTrace(System.err);
  		LOG.info(getLogPrefix() + t.getMessage());
	  } 

	  /**
  	 * Error.
  	 *
  	 * @param s the s
  	 */
  	protected void error(String s) {
	    LOG.error(getLogPrefix() + s);
	  } 

	  /**
  	 * Error.
  	 *
  	 * @param t the t
  	 */
  	protected void error(Throwable t) {
	    //t.printStackTrace(System.err);
  		LOG.error(getLogPrefix() + t.getMessage());
	  } 
}
