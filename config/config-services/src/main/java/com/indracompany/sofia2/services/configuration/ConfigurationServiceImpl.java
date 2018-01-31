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

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.ConfigurationType;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ConfigurationRepository;
import com.indracompany.sofia2.config.repository.ConfigurationTypeRepository;
import com.indracompany.sofia2.service.user.UserService;

@Service
public class ConfigurationServiceImpl implements ConfigurationService{
	@Autowired
	ConfigurationRepository configurationRepository;
	@Autowired
	ConfigurationTypeRepository configurationTypeRepository;
	@Autowired
	UserService userService;


	public List<Configuration> getAllConfigurations()
	{

		return this.configurationRepository.findAll();

	}
	@Transactional
	public void deleteConfiguration(String id)
	{
		this.configurationRepository.deleteById(id);
	}
	public List<ConfigurationType> getAllConfigurationTypes()
	{
		List<ConfigurationType> types= this.configurationTypeRepository.findAll();
		return types;

	}
	public Configuration getConfiguration(String id)
	{
		return this.configurationRepository.findById(id);
	}
	public void createConfiguration(Configuration configuration)
	{
		ConfigurationType configurationType=this.configurationTypeRepository.findByName(configuration.getConfigurationTypeId().getName());
		if(configurationType!=null)
		{
			User user= this.userService.getUser(configuration.getUserId().getUserId());
			if(user!=null)
			{
				configuration.setUserId(user);
				configuration.setConfigurationTypeId(configurationType);
				if(isValidJSON(configuration.getJsonSchema()))
				{
					this.configurationRepository.save(configuration);
				}
			}
		}
	}
	public void updateConfiguration(Configuration configuration)
	{
		if(this.existsConfiguration(configuration))
		{
			if(configuration.getUserId()!=null && configuration.getJsonSchema()!=null && configuration.getConfigurationTypeId()!=null)
			{
				Configuration newConfiguration=this.configurationRepository.findById(configuration.getId());
				newConfiguration.setConfigurationTypeId(this.configurationTypeRepository.findByName(configuration.getConfigurationTypeId().getName()));
				if(isValidJSON(configuration.getJsonSchema())) newConfiguration.setJsonSchema(configuration.getJsonSchema());
				this.configurationRepository.save(newConfiguration);
			}
		}
	}
	public boolean existsConfiguration(Configuration configuration)
	{
		if(this.configurationRepository.findById(configuration.getId())==null) return false;
		else return true;
		
	}
	public boolean isValidJSON(final String json){
		try{

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

		} catch(JsonProcessingException e)
		{
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
