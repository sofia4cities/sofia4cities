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
package com.indracompany.sofia2.config.services.notebook.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class NotebookServiceConfiguration {
	@Value("${sofia2.analytics.notebook.zeppelinProtocol:http}")
	private String zeppelinProtocol;
	@Value("${sofia2.analytics.notebook.zeppelinHostname:localhost}")
	private String zeppelinHostname;
	@Value("${sofia2.analytics.notebook.zeppelinPort:8080}")
	private int zeppelinPort;
	@Value("${sofia2.analytics.notebook.zeppelinPathname:#{null}}")
	private String zeppelinPathname;
	@Value("${sofia2.analytics.notebook.shiroAdminUsername:#{null}}")
	private String zeppelinShiroAdminUsername;
	@Value("${sofia2.analytics.notebook.shiroAdminPass:#{null}}")
	private String zeppelinShiroAdminPass;
	@Value("${sofia2.analytics.notebook.shiroUsername:#{null}}")
	private String zeppelinShiroUsername;
	@Value("${sofia2.analytics.notebook.shiroPass:#{null}}")
	private String zeppelinShiroPass;
	@Value("${sofia2.analytics.notebook.restUsername:#{null}}")
	private String restUsername;
	@Value("${sofia2.analytics.notebook.restPass:#{null}}")
	private String restPass;
	@Value("${sofia2.analytics.notebook.zeppelinGlobalTimeout:#{120000}}")
	private String globalTimeout;
	private String baseURL;

	@PostConstruct
	public void init() {
		baseURL = String.format("%s://%s:%s/%s", zeppelinProtocol, zeppelinHostname, zeppelinPort, zeppelinPathname);
	}
}