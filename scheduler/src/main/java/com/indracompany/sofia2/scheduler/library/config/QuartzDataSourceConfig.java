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
package com.indracompany.sofia2.scheduler.library.config;

import static com.indracompany.sofia2.scheduler.library.PropertyNames.SCHEDULER_PROPERTIES_LOCATION;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.indracompany.sofia2.scheduler.library.PropertyNames.SCHEDULER_PREFIX;

@Configuration
@ConfigurationProperties(prefix = SCHEDULER_PREFIX)
@ConditionalOnResource(resources = SCHEDULER_PROPERTIES_LOCATION)
public class QuartzDataSourceConfig {
	
	private String url;	
	private String username;	
	private String password;	
	private List<String> autoStartupSchedulers;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getAutoStartupSchedulers() {
		return autoStartupSchedulers;
	}

	public void setAutoStartupSchedulers(List<String> autoStartupSchedulers) {
		this.autoStartupSchedulers = autoStartupSchedulers;
	}
	
}
