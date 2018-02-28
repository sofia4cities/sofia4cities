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
package com.indracompany.sofia2.config.services.flowdomain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Flow;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.model.FlowNode;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.FlowDomainRepository;
import com.indracompany.sofia2.config.repository.FlowNodeRepository;
import com.indracompany.sofia2.config.repository.FlowRepository;
import com.indracompany.sofia2.config.services.exceptions.FlowDomainServiceException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FlowDomainServiceImpl implements FlowDomainService {

	@Value("${sofia2.flowengine.port.domain.min:8000}")
	private int domainPortMin;
	@Value("${sofia2.flowengine.port.domain.max:8500}")
	private int domainPortMax;
	@Value("${sofia2.flowengine.port.service.min:7000}")
	private int servicePortMin;
	@Value("${sofia2.flowengine.port.service.max:7500}")
	private int servicePortMax;
	@Value("${sofia2.flowengine.home.base:/tmp/}")
	private String homeBase;

	@Autowired
	public FlowDomainRepository domainRepository;

	@Autowired
	private FlowRepository flowRepository;

	@Autowired
	private FlowNodeRepository nodeRepository;

	@Override
	public FlowDomain getFlowDomainByUser(String user) {
		return domainRepository.findByUser_userId(user);
	}

	@Override
	public void deleteFlowDomainFlows(String domainIdentification) {
		FlowDomain domain = domainRepository.findByIdentification(domainIdentification);
		// Delete all data from this Domain,
		// including flows, nodes and properties
		List<Flow> flows = flowRepository.findByFlowDomain_Identification(domain.getIdentification());
		for (Flow flow : flows) {
			List<FlowNode> nodes = nodeRepository.findByFlow_NodeRedFlowId(flow.getNodeRedFlowId());
			for (FlowNode node : nodes) {
				nodeRepository.delete(node);
			}
			flowRepository.delete(flow);
		}
	}

	@Override
	public void deleteFlowdomain(String domainIdentification) {
		this.deleteFlowDomainFlows(domainIdentification);
		domainRepository.deleteByIdentification(domainIdentification);
	}

	@Override
	public FlowDomain getFlowDomainByIdentification(String identification) {
		return this.domainRepository.findByIdentification(identification);
	}

	@Override
	public FlowDomain createFlowDomain(String identification, User user) {

		if (this.domainRepository.findByIdentification(identification) != null) {
			log.debug("Flow domain {} already exist.", identification);
			throw new FlowDomainServiceException("The requested flow domain already exists in CDB");
		}

		FlowDomain domain = new FlowDomain();
		domain.setIdentification(identification);
		domain.setActive(true);
		domain.setState("STOP");
		domain.setUser(user);
		domain.setHome(this.homeBase + user.getUserId());
		// Check free domain ports
		List<Integer> usedDomainPorts = this.domainRepository.findAllDomainPorts();
		Integer selectedPort = domainPortMin;
		boolean portFound = false;
		while (selectedPort <= domainPortMax && !portFound) {
			if (!usedDomainPorts.contains(selectedPort)) {
				portFound = true;
			} else {
				selectedPort++;
			}
		}
		if (!portFound) {
			log.error("No port available found for domain = {}.", identification);
			throw new FlowDomainServiceException("No port available found for domain " + identification);
		}
		domain.setPort(selectedPort);
		// Check free service ports
		List<Integer> usedServicePorts = this.domainRepository.findAllServicePorts();
		Integer selectedServicePort = servicePortMin;
		boolean servicePortFound = false;
		while (selectedServicePort <= servicePortMax && !servicePortFound) {
			if (!usedServicePorts.contains(selectedServicePort)) {
				servicePortFound = true;
			} else {
				selectedServicePort++;
			}
		}
		if (!servicePortFound) {
			log.error("No service port available found for domain = {}.", identification);
			throw new FlowDomainServiceException("No service port available found for domain " + identification);
		}
		domain.setServicePort(selectedServicePort);
		this.domainRepository.save(domain);
		return domain;
	}

	@Override
	public boolean flowDomainExists(FlowDomain domain) {
		if (this.domainRepository.findByIdentification(domain.getIdentification()) == null) {
			return false;
		}
		return true;
	}

	@Override
	public void updateDomain(FlowDomain domain) {

		if (!flowDomainExists(domain)) {
			log.error("Domain not found for identification = {}.", domain.getIdentification());
			throw new FlowDomainServiceException("Domain " + domain.getIdentification() + " not found.");
		} else {
			this.domainRepository.save(domain);
		}
	}

	@Override
	public boolean domainExists(String domainIdentification) {
		if (domainRepository.findByIdentification(domainIdentification) != null) {
			return true;
		}
		return false;
	}
}
