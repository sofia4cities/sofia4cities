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
package com.indracompany.sofia2.resources.service;

import java.util.Properties;

public class IntegrationResourcesServiceImpl implements IntegrationResourcesService {

	private Properties env;
	
	private static String INTEGRATION_PREFIX="sofia2.module.integration.";
	
	public String getURL(String serviceKey) {
		return env.getProperty(INTEGRATION_PREFIX+serviceKey, "RESOURCE_URL_NOT_FOUND");
	}

	public void setEnv(Properties env) {
		this.env = env;
	}

}
