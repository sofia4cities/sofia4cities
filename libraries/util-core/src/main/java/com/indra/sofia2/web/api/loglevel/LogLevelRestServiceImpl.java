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
package com.indra.sofia2.web.api.loglevel;

import javax.ws.rs.core.Response;

import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indra.sofia2.grid.client.interfaces.LogProcess;
import com.indra.sofia2.support.util.log.LogLevelDto;
import com.indra.sofia2.support.util.log.LogLevelFIQL;
import com.indra.sofia2.support.util.rest.ResponseBuilder;

@Component("logLevelRestServiceImpl")
public class LogLevelRestServiceImpl implements LogLevelRestService {

	@Autowired
	LogProcess logProcess;

	@Override
	public Response getLogLevel() throws Exception {
		return ResponseBuilder.buildResponse(new LogLevelDto(logProcess.getLogLevel()));
	}

	@Override
	public Response updateLogLevel(String logLevel) throws Exception {
		Level level = logProcess.updateLogLevel(logLevel);
		return ResponseBuilder.buildResponse(new LogLevelDto(level.toString()));
	}
	
	@Override
	public Response updateLoggersLevel(String logLevel, String loggersName) throws Exception {
		LogLevelFIQL.validateThatLoggerExists(logLevel, loggersName);
		Level level = logProcess.updateLogLevel(logLevel, loggersName);
		return ResponseBuilder.buildResponse(new LogLevelDto(level.toString(), loggersName));
	}
}

