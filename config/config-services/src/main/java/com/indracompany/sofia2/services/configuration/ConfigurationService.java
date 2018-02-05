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
package com.indracompany.sofia2.services.configuration;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.ConfigurationType;

public interface ConfigurationService {

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	List<Configuration> getAllConfigurations();

	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	void deleteConfiguration(String id);

	Configuration getConfiguration(String id);

	List<ConfigurationType> getAllConfigurationTypes();

	void createConfiguration(Configuration configuration);

	boolean existsConfiguration(Configuration configuration);

	void updateConfiguration(Configuration configuration);

	boolean isValidYML(final String yml);
}
