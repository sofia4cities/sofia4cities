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
package com.indra.sofia2.support.util.log;

import org.joda.time.DateTime;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import com.indra.sofia2.support.entity.utils.CalendarAdapter;

@ApiObject(name = "loglevel")
public class LogLevelDto {

	@ApiObjectField(description = "Current timestamp")
	private DateTime currentTimestamp;

	@ApiObjectField(description = "Log level of the logger")
	private String currentLogLevel;

	@ApiObjectField(description = "The name of the logger")
	private String loggerName;

	public LogLevelDto(String currentLogLevel) {
		this.currentLogLevel = currentLogLevel.toUpperCase();
		this.currentTimestamp = CalendarAdapter.getUtcDate();
	}

	public LogLevelDto(String currentLogLevel, String loggersName) {
		this.currentLogLevel = currentLogLevel.toUpperCase();
		this.loggerName = loggersName;
		this.currentTimestamp = CalendarAdapter.getUtcDate();
	}

	public String getCurrentLogLevel() {
		return currentLogLevel;
	}

	public DateTime getCurrentTimestamp() {
		return currentTimestamp;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}
}
