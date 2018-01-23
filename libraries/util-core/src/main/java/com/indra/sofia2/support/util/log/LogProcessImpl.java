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
package com.indra.sofia2.support.util.log;

import java.util.Calendar;
import java.util.Enumeration;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.indra.jee.arq.spring.core.contexto.ArqSpringContext;
import com.indra.sofia2.grid.client.exception.LogProcessException;
import com.indra.sofia2.grid.client.interfaces.LogProcess;
import com.indra.sofia2.log.IdentificationService;
import com.indra.sofia2.support.entity.gestion.dominio.OntologiaRepository;
import com.indra.sofia2.support.entity.gestion.dominio.ResultadoProceso;
import com.indra.sofia2.support.entity.gestion.dominio.ResultadoProcesoRepository;
import com.indra.sofia2.support.entity.gestion.dominio.TiposProceso;
import com.indra.sofia2.support.entity.gestion.dominio.TiposProcesoRepository;

@Component("logProcess")
@ManagedResource(objectName = "SOFIA2:type=Administracion,name=Log", description = "Gestion de Log")
public class LogProcessImpl implements LogProcess {

	private static final Logger logger = LoggerFactory.getLogger(LogProcessImpl.class);
	
	@Value("${arqspring.modulo.nombre}")
	private String moduleName;

	@ManagedOperation(description = "Modifies the log level (TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF, ALL)")
	@SuppressWarnings("unchecked")
	@Override
	public Level updateLogLevel(@NotNull @NotEmpty String level) {
		logger.info("Updating level of root logger. Level = {}.", level);
		Level logLevel = Level.toLevel(level);
		LogManager.getRootLogger().setLevel(logLevel);
		Enumeration<org.apache.log4j.Logger> loggers = LogManager.getCurrentLoggers();
		while (loggers.hasMoreElements()) {
			org.apache.log4j.Logger log4jLogger = loggers.nextElement();
			logger.info("Updating level of logger. LoggerName = {}, level = {}.", log4jLogger.getName(), level);
			log4jLogger.setLevel(logLevel);
		}
		return logLevel;
	}
	
	@Override
	public Level updateLogLevel(@NotNull @NotEmpty String level, String loggerName) {
		logger.info("Updating level of logger. LoggerName = {} , level = {}", loggerName, level);
		org.apache.log4j.Logger log4jLogger = LogManager.getLogger(loggerName);
		Level logLevel = Level.toLevel(level);
		log4jLogger.setLevel(logLevel);
		return logLevel;
	}
	
	@ManagedOperation(description = "Indica el nivel de traza de Log actual")
	@Override
	public String getLogLevel(){
		return LogManager.getRootLogger().getLevel().toString();
	}
	
	@Override
	public String createLog(String idProcessType, String idProcess, String descripcion, String status, String idOntologia, Calendar initTime, Calendar endTime, String details, String idUsuario, String idMessage) throws LogProcessException{
		ResultadoProceso registroLog;
		try {
			if (details != null && details.length() > 200)
				details = details.substring(0, 200);
			registroLog = new ResultadoProceso();
			registroLog.setDetalles(details);
			registroLog.setDescripcion(descripcion);
			registroLog.setFechaFinEjecucion(endTime);
			registroLog.setFechaIniEjecucion(initTime);
			registroLog.setIdProceso(idProcess);
			registroLog.setIdentificacion(idMessage);
			registroLog.setUsuarioId(idUsuario);

			if (idOntologia != null && !"".equals(idOntologia)) {
				registroLog.setOntologiaId(OntologiaRepository.findOntologia(idOntologia));
			}
			
			TiposProceso tipoProceso = TiposProcesoRepository.findTiposProceso(idProcessType);
			registroLog.setIdTipoProceso(tipoProceso);		
			if (status != null) {
				registroLog.setResultado(status);
			} 
			

			registroLog.setUsuarioId(idUsuario);
			JpaTransactionManager tx= (JpaTransactionManager)ArqSpringContext.getBean("transactionManager");
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setName("rootTransaction");
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus statuss = tx.getTransaction(def);
			registroLog.persist();
			tx.commit(statuss);
		} catch (Exception e) {
			throw new LogProcessException(e);
		}
		
		return registroLog.getId();
	}

	@Override
	public String createLog(String idProcessType, String idProcess, String descripcion, String status, String idOntologia, Calendar initTime, Calendar endTime, String details, String idUsuario) throws LogProcessException {
		return createLog(idProcessType, idProcess, descripcion, status, idOntologia, initTime, endTime, details, idUsuario, IdentificationService.getIdentificador());
	}

	@Override
	public void updateLog(String idLog, String status, Calendar initTime, Calendar endTime, String details) throws LogProcessException {
		
		
		try {
			ResultadoProceso registroLog = ResultadoProcesoRepository.findResultadoProceso(idLog);
			
			if (registroLog==null){
				throw new Exception ("No se ha encontrado el resultadoProceso con id: "+idLog);
			}
			if (details != null) {
				if (details.length() > 200)
					details = details.substring(0, 200);
				registroLog.setDetalles(details);
			}
			if (endTime != null) {
				registroLog.setFechaFinEjecucion(endTime);
			}
			if (initTime != null) {
				registroLog.setFechaIniEjecucion(initTime);
			}
			if (status != null) {
				registroLog.setResultado(status);
			}
			
			JpaTransactionManager tx= (JpaTransactionManager)ArqSpringContext.getBean("transactionManager");
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setName("rootTransaction");
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus statuss = tx.getTransaction(def);
			registroLog.merge();
			tx.commit(statuss);
		} catch (Exception e) {
			throw new LogProcessException(e);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String getModuleLogDirectory() {
		Enumeration<org.apache.log4j.Logger> loggers = LogManager.getCurrentLoggers();
		String logfile = null;
		boolean fileAppenderFound = false;
		while (!fileAppenderFound && loggers.hasMoreElements()) {
			org.apache.log4j.Logger logger = loggers.nextElement();
			Enumeration<org.apache.log4j.Appender> appenders = logger.getAllAppenders();
			while (!fileAppenderFound && appenders.hasMoreElements()) {
				Appender appender = appenders.nextElement();
				if (appender instanceof FileAppender) {
					fileAppenderFound = true;
					logfile = ((FileAppender) appender).getFile();
				}
			}
		}
		if (logfile == null)
			return null;
		int spliceEndIndex = logfile.indexOf(moduleName);
		if (spliceEndIndex >= 0)
			return logfile.substring(0, spliceEndIndex);
		else
			return null;
	}


}
