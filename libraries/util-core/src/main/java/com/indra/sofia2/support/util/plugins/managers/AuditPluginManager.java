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

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.indra.sofia2.support.bbdd.plugins.PluginEntrypointsLoader;
import com.indra.sofia2.support.bbdd.plugins.PluginEntrypointsLoaderObserver;
import com.indra.sofia2.support.entity.gestion.dominio.Script;
import com.indra.sofia2.support.util.plugins.commands.ScriptAuditPluginCommand;
import com.indra.sofia2.support.util.plugins.commands.ScriptRuntimeAuditPluginCommand;
import com.indra.sofia2.support.util.plugins.commands.SsapMessageAuditPluginCommand;
import com.indra.sofia2.support.util.plugins.dto.audit.AuditDirection;
import com.indra.sofia2.support.util.plugins.dto.audit.AuditOperation;
import com.indra.sofia2.support.util.plugins.dto.audit.AuditType;
import com.indra.sofia2.support.util.plugins.dto.audit.ClientGatewayData;
import com.indra.sofia2.support.util.plugins.interfaces.audit.AuditPlugin;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolProperties;

@Component
public class AuditPluginManager implements AuditPlugin, PluginEntrypointsLoaderObserver {

	private static final Logger logger = LoggerFactory.getLogger(AuditPluginManager.class);
	private static final String GENERIC_AUDIT_PLUGIN_KEY = "AuditPluginRuntime";
	private static final String SCRIPT_AUDIT_PLUGIN_KEY = "AuditPluginScript";
	private static final String SCRIPT_RUNTIME_AUDIT_PLUGIN_KEY = "AuditPluginScript";

	@Value("${audit-plugins.hystrix.threadPool.maxQueueSize:5000}")
	private int maxQueueSize;
	@Value("${audit-plugins.hystrix.threadPool.coreSize:50}")
	private int coreSize;
	@Value("${audit-plugins.hystrix.executionTimeout.millis:10000}")
	private int executionTimeout;

	private List<AuditPlugin> plugins;
	private Setter auditPluginsSetter;
	private Setter scritAuditPluginsSetter;
	private Setter scriptRuntimeAuditPluginsSetter;

	@PostConstruct
	public void init() {
		this.auditPluginsSetter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(GENERIC_AUDIT_PLUGIN_KEY))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionIsolationStrategy(ExecutionIsolationStrategy.THREAD)
						.withExecutionIsolationSemaphoreMaxConcurrentRequests(coreSize)
						.withExecutionTimeoutInMilliseconds(executionTimeout)
						.withExecutionIsolationThreadInterruptOnTimeout(true))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(maxQueueSize)
						.withQueueSizeRejectionThreshold(maxQueueSize - 1).withCoreSize(coreSize));
		this.scritAuditPluginsSetter = Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SCRIPT_AUDIT_PLUGIN_KEY))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionIsolationStrategy(ExecutionIsolationStrategy.THREAD)
						.withExecutionIsolationSemaphoreMaxConcurrentRequests(coreSize))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(maxQueueSize)
						.withQueueSizeRejectionThreshold(maxQueueSize - 1).withCoreSize(coreSize));
		this.scriptRuntimeAuditPluginsSetter = Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SCRIPT_RUNTIME_AUDIT_PLUGIN_KEY))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionIsolationStrategy(ExecutionIsolationStrategy.THREAD)
						.withExecutionIsolationSemaphoreMaxConcurrentRequests(coreSize))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(maxQueueSize)
						.withQueueSizeRejectionThreshold(maxQueueSize - 1).withCoreSize(coreSize));
	}
	
	@Override
	public void onPluginEntrypointsLoaded(PluginEntrypointsLoader pluginLoader) {
		plugins = pluginLoader.getPluginsOfType(AuditPlugin.class);
	}

	@Override
	public void audit(SSAPMessage<?> message, String cipherKey, Date timeStamp, ClientGatewayData client,
			AuditDirection direction, AuditType type) {
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking audit plugins. SsapMessage = {}.", message);
		}
		for (AuditPlugin plugin : plugins) {
			try {
				SsapMessageAuditPluginCommand pluginRuntime = new SsapMessageAuditPluginCommand(auditPluginsSetter, plugin,
						message, cipherKey, timeStamp, client, direction, type);
				pluginRuntime.queue();
			} catch (Throwable e) {
				logger.error(
						"An exception was caught while invoking audit plugin. PluginClass = {}, message = {}, cause = {}, errorMessage = {}.",
						plugin.getClass(), message, e.getCause(), e.getMessage());
			}
		}
	}

	@Override
	public void audit(Script script, Date timeStamp, AuditOperation operation, String user, AuditType type) {
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking audit plugins. Script = {}.", script);
		}
		for (AuditPlugin plugin : plugins) {
			try {
				ScriptAuditPluginCommand pluginRuntime = new ScriptAuditPluginCommand(scritAuditPluginsSetter, plugin,
						script, timeStamp, operation, user, type);
				pluginRuntime.queue();
			} catch (Throwable e) {
				logger.error(
						"An exception was caught while invoking audit plugin. PluginClass = {}, script = {}, cause = {}, errorMessage = {}.",
						plugin.getClass(), script, e.getCause(), e.getMessage());
			}
		}
	}

	@Override
	public void audit(String scriptId, String ontology, String ontologyName, Date timeStamp, AuditOperation operation,
			String sessionKey, AuditType type) {
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking audit plugins. ScriptId = {}.", scriptId);
		}
		for (AuditPlugin plugin : plugins) {
			try {
				ScriptRuntimeAuditPluginCommand pluginRuntime = new ScriptRuntimeAuditPluginCommand(
						scriptRuntimeAuditPluginsSetter, plugin, scriptId, ontology, ontologyName, timeStamp, operation,
						sessionKey, type);
				pluginRuntime.queue();
			} catch (Throwable e) {
				logger.error(
						"An exception was caught while invoking audit plugin. PluginClass = {}, scriptId = {}, cause = {}, errorMessage = {}.",
						plugin.getClass(), scriptId, e.getCause(), e.getMessage());
			}
		}
	}
}
