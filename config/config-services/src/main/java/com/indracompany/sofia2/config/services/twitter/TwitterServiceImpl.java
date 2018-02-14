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
package com.indracompany.sofia2.config.services.twitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.ClientPlatformOntology;
import com.indracompany.sofia2.config.model.Configuration;
import com.indracompany.sofia2.config.model.ConfigurationType;
import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.Token;
import com.indracompany.sofia2.config.model.TwitterListening;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ClientPlatformOntologyRepository;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.DataModelRepository;
import com.indracompany.sofia2.config.repository.TokenRepository;
import com.indracompany.sofia2.config.repository.TwitterListeningRepository;
import com.indracompany.sofia2.config.services.configuration.ConfigurationService;
import com.indracompany.sofia2.config.services.ontology.OntologyService;
import com.indracompany.sofia2.config.services.user.UserService;
import com.indracompany.sofia2.scheduler.SchedulerType;
import com.indracompany.sofia2.scheduler.scheduler.bean.TaskInfo;
import com.indracompany.sofia2.scheduler.scheduler.service.TaskService;
@Service
public class TwitterServiceImpl implements TwitterService {

	@Autowired
	TwitterListeningRepository twitterListeningRepository;
	@Autowired
	ConfigurationService configurationService;
	@Autowired
	ClientPlatformRepository clientPlatformRepository;
	@Autowired
	ClientPlatformOntologyRepository clientPlatformOntologyRepository;
	@Autowired
	DataModelRepository dataModelRepository;
	@Autowired
	TokenRepository tokenRepository;
	@Autowired
	OntologyService ontologyService;
	@Autowired
	UserService userService;
	@Autowired
	TaskService taskService;

	@Override
	public List<TwitterListening> getAllListenings() {
		return this.twitterListeningRepository.findAll();
	}

	@Override
	public List<TwitterListening> getAllListeningsByUser(String userId) {
		User user = userService.getUser(userId);
		return this.twitterListeningRepository.findByUser(user);
	}

	@Override
	public TwitterListening getListenById(String id) {
		return this.twitterListeningRepository.findById(id);
	}

	@Override
	public TwitterListening getListenByIdentificator(String identificator) {
		return this.twitterListeningRepository.findByIdentificator(identificator);
	}
	@Override
	public List<Configuration> getAllConfigurations() {
		return this.configurationService.getConfigurations(ConfigurationType.Type.TwitterConfiguration);
	}

	@Override
	public List<Configuration> getConfigurationsByUserId(String userId) {
		List<Configuration> configurationsByUser = new ArrayList<Configuration>();
		for (Configuration configuration : this.getAllConfigurations()) {
			if (configuration.getUser().getUserId().equals(userId))
				configurationsByUser.add(configuration);
		}

		return configurationsByUser;

	}

	@Override
	public List<String> getClientsFromOntology(String ontologyId) {
		Ontology ontology = this.ontologyService.getOntologyByIdentification(ontologyId);
		List<String> clients = new ArrayList<String>();
		for (ClientPlatformOntology clientPlatform : this.clientPlatformOntologyRepository.findByOntology(ontology)) {
			clients.add(clientPlatform.getClientPlatform().getIdentification());
		}
		return clients;
	}

	@Override
	public List<String> getTokensFromClient(String clientPlatformId) {
		ClientPlatform clientPlatform = this.clientPlatformRepository.findByIdentification(clientPlatformId);
		List<String> tokens = new ArrayList<String>();
		for (Token token : this.tokenRepository.findByClientPlatform(clientPlatform)) {
			tokens.add(token.getToken());
		}
		return tokens;
	}

	@Override
	public TwitterListening createListening(TwitterListening twitterListening) {
		if (twitterListening.getOntology().getId() == null)
			twitterListening.setOntology(this.ontologyService
					.getOntologyByIdentification(twitterListening.getOntology().getIdentification()));
		if (twitterListening.getToken().getId() == null)
			twitterListening.setToken(this.tokenRepository.findByToken(twitterListening.getToken().getToken()));
		if (twitterListening.getConfiguration().getId() == null)
			twitterListening.setConfiguration(this.configurationService
					.getConfigurationByDescription(twitterListening.getConfiguration().getDescription()));
		
		twitterListening = this.twitterListeningRepository.save(twitterListening);
		return twitterListening;

	}

	@Override

	public void updateListen(TwitterListening twitterListening) {
		TwitterListening newTwitterListening = this.twitterListeningRepository.findById(twitterListening.getId());
		if (newTwitterListening != null) {
			newTwitterListening.setIdentificator(twitterListening.getIdentificator());
			newTwitterListening.setConfiguration(this.configurationService
					.getConfigurationByDescription(twitterListening.getConfiguration().getDescription()));
			newTwitterListening.setTopics(twitterListening.getTopics());
			newTwitterListening.setDateFrom(twitterListening.getDateFrom());
			newTwitterListening.setDateTo(twitterListening.getDateTo());

			this.twitterListeningRepository.save(newTwitterListening);
		}

	}

	@Override
	public boolean existOntology(String identification) {
		if (this.ontologyService.getOntologyByIdentification(identification) != null)
			return true;
		else
			return false;
	}

	@Override
	public boolean existClientPlatform(String identification) {
		if (this.clientPlatformRepository.findByIdentification(identification) != null)
			return true;
		else
			return false;
	}

	@Override
	public Ontology createTwitterOntology(String ontologyId, String dataModel) {
		DataModel dataModelTwitter = this.dataModelRepository.findByName(dataModel).get(0);
		Ontology ontology = new Ontology();
		ontology.setIdentification(ontologyId);
		if (dataModelTwitter.getType().equals(DataModel.MainType.Twitter.toString()))
			ontology.setDescription("Ontology created for tweet recollection");
		ontology.setJsonSchema(dataModelTwitter.getSchema());
		ontology.setActive(true);
		ontology.setPublic(false);
		ontology.setRtdbClean(false);
		ontology.setRtdbToHdb(false);
		return ontology;

	}
	
	@Override
	public boolean scheduleTwitterListening(TwitterListening twitterListening) {
		
		TaskInfo task = new TaskInfo();
		task.setJobName(twitterListening.getId());
		task.setSchedulerType(SchedulerType.Twitter);
		
		Map<String, Object> jobContext = new HashMap<String, Object>();
		jobContext.put("id", twitterListening.getId());
		jobContext.put("ontology", twitterListening.getOntology().getIdentification());
		jobContext.put("clientPlatform", twitterListening.getToken().getClientPlatform().getIdentification());
		jobContext.put("token", twitterListening.getToken().getToken());
		jobContext.put("topics", twitterListening.getTopics());
		jobContext.put("geolocation", false);
		jobContext.put("timeout", 2);
		if (twitterListening.getConfiguration() != null) {
			jobContext.put("configuration", twitterListening.getConfiguration().getId());
		} else {
			jobContext.put("configuration", null);
		}
		
		task.setUsername(twitterListening.getUser().getUserId());
		task.setData(jobContext);
		task.setSingleton(false);
		task.setCronExpression("0 * 0 ? * * *");
		return taskService.addJob(task).isSuccess();
		
		
		
	}
}
